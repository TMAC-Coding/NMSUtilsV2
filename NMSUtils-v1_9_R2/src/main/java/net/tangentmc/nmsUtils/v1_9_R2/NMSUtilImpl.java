package net.tangentmc.nmsUtils.v1_9_R2;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_9_R2.*;
import net.tangentmc.nmsUtils.NMSUtil;
import net.tangentmc.nmsUtils.NMSUtils;
import net.tangentmc.nmsUtils.entities.*;
import net.tangentmc.nmsUtils.events.EntityMoveEvent;
import net.tangentmc.nmsUtils.jinglenote.JingleNoteManager;
import net.tangentmc.nmsUtils.jinglenote.MidiJingleSequencer;
import net.tangentmc.nmsUtils.utils.Validator;
import net.tangentmc.nmsUtils.v1_9_R2.entities.CraftHologramEntity;
import net.tangentmc.nmsUtils.v1_9_R2.entities.LaserEntities;
import net.tangentmc.nmsUtils.v1_9_R2.entities.NPC;
import net.tangentmc.nmsUtils.v1_9_R2.entities.basic.BasicNMSArmorStand;
import net.tangentmc.nmsUtils.v1_9_R2.entities.basic.BasicNMSEntity;
import net.tangentmc.nmsUtils.v1_9_R2.entities.basic.NMSHologramWrapper;
import net.tangentmc.nmsUtils.v1_9_R2.entities.effects.Collideable;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkUnloadEvent;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class NMSUtilImpl implements NMSUtil, Listener, Runnable {

    private HashSet<UUID> riding = new HashSet<>();
    JingleNoteManager manager = new JingleNoteManager();
    private static Table<Integer, Integer, List<NMSEntity>> customEntities = HashBasedTable.create();
    NPCManager npcmanager;

    @SneakyThrows
    public NMSUtilImpl() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(this));
        validateEntityMethod = net.minecraft.server.v1_9_R2.World.class.getDeclaredMethod("b", net.minecraft.server.v1_9_R2.Entity.class);
        validateEntityMethod.setAccessible(true);
        NMSEntityTypes.registerEntities();
        npcmanager = new NPCManager();
        Bukkit.getPluginManager().registerEvents(this, NMSUtils.getInstance());
        Bukkit.getWorlds().forEach(this::trackWorldEntities);
        Bukkit.getScheduler().runTaskTimer(NMSUtils.getInstance(), this, 10L, 1L);
    }

    WeakHashMap<UUID, WorldManager> managers = new WeakHashMap<>();

    @Override
    public void trackWorldEntities(World w) {
        managers.put(w.getUID(), new WorldManager(w));
        w.getEntities().stream().filter(e -> Objects.nonNull(e.getCustomName())).filter(e -> e.getCustomName().equals("deleteme")).forEach(Entity::remove);
    }

    @Override
    public void untrackWorldEntities(World w) {
        managers.get(w.getUID()).remove();
    }

    public net.minecraft.server.v1_9_R2.World getWorld(World w) {
        return ((CraftWorld) w).getHandle();
    }


    public List<Integer> getEntityRemoveQueue(Player entity) {
        return ((CraftPlayer) entity).getHandle().removeQueue;
    }

    public void flushEntityRemoveQueue(Player pl) {
        final List<Integer> ids = getEntityRemoveQueue(pl);
        if (ids.isEmpty()) {
            return;
        }
        while (ids.size() >= 128) {
            final int[] rawIds = new int[127];
            for (int i = 0; i < rawIds.length; i++) {
                rawIds[i] = ids.remove(0);
            }
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            packet.getIntegerArrays().write(0, rawIds);
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(pl, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        final int[] rawIds = new int[ids.size()];
        for (int i = 0; i < rawIds.length; i++) {
            rawIds[i] = ids.remove(0);
        }
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, rawIds);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(pl, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        ids.clear();
    }

    @Override
    public boolean teleportFast(Entity entity, Location location, org.bukkit.util.Vector velocity) {
        if (entity.isInsideVehicle()) {
            entity = entity.getVehicle();
        }
        net.minecraft.server.v1_9_R2.Entity en = ((CraftEntity) entity).getHandle();
        //Flush the removal queue so that it is done first then we teleport and set velocity
        entity.getWorld().getPlayers().forEach(this::flushEntityRemoveQueue);
        EntityTracker tracker = ((WorldServer) getWorld(entity.getWorld())).getTracker();
        if (!(entity instanceof Minecart)) {
            tracker.untrackEntity(en);
        }
        en.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        en.motX = velocity.getX();
        en.motY = velocity.getY();
        en.motZ = velocity.getZ();

        if (!(entity instanceof Minecart)) {
            tracker.track(en);
        } else {
            EntityTrackerEntry entry = tracker.trackedEntities.get(en.getId());
            //Forcibly update this entity this tick
            //Why 3? The tracker runs every three ticks, but you cant set a to 0 or it wont run at all
            //At least, it runs every three ticks for MineCarts anyway.
            entry.a = 3;
        }
        tracker.updatePlayers();
        return true;
    }
    public static NBTTagCompound getTag(net.minecraft.server.v1_9_R2.Entity en) {
        NBTTagCompound tag = new NBTTagCompound();
        en.c(tag);
        return tag;
    }

    @Override
    public void stealPlayerControls(Location loc, Player who) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc.add(0, -0.9875, 0), EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setSmall(true);
        as.setPassenger(who);
        this.riding.add(who.getUniqueId());
    }

    @Override
    public NMSLaser spawnLaser(Location init) {
        LaserEntities.CraftLaserEntity.LaserEntity laser = new LaserEntities.CraftLaserEntity.LaserEntity(((CraftWorld) init.getWorld()).getHandle(), init);
        return laser.getBukkitEntity();
    }

    @Override
    public NMSHologram spawnHologram(Location loc, ArrayList<HologramFactory.HologramObject> lines) {
        CraftHologramEntity.HologramEntity holo = new CraftHologramEntity.HologramEntity(loc, lines);
        return NMSHologram.wrap(holo.getBukkitEntity());
    }

    @Override
    public void playMidi(Player to, boolean repeat, File midi)
            throws MidiUnavailableException, InvalidMidiDataException, IOException {
        MidiJingleSequencer seq = new MidiJingleSequencer(midi, repeat);
        manager.play(to.getName(), seq);
    }

    @Override
    public void playMidiNear(Location near, double area, boolean repeat, File midi)
            throws MidiUnavailableException, InvalidMidiDataException, IOException {
        MidiJingleSequencer seq = new MidiJingleSequencer(midi, repeat);
        manager.playNear(near, area, seq);
    }

    private static Method validateEntityMethod;

    public static boolean addEntityToWorld(net.minecraft.server.v1_9_R2.World world, net.minecraft.server.v1_9_R2.Entity nmsEntity) {
        Validator.isTrue(Bukkit.isPrimaryThread(), "Async entity add");

        if (validateEntityMethod == null) {
            return world.addEntity(nmsEntity, SpawnReason.CUSTOM);
        }
        final int chunkX = MathHelper.floor(nmsEntity.locX / 16.0);
        final int chunkZ = MathHelper.floor(nmsEntity.locZ / 16.0);
        //This function can be called to add entities that arent in loaded chunks. When that happenes,
        //queue them for later.
        if (!((WorldServer) world).getChunkProviderServer().isLoaded(chunkX, chunkZ)) {
            if (nmsEntity.getBukkitEntity() instanceof NMSEntity) {
                if (!customEntities.contains(chunkX, chunkZ)) customEntities.put(chunkX, chunkZ, new ArrayList<>());
                customEntities.get(chunkX, chunkZ).add((NMSEntity) nmsEntity.getBukkitEntity());
            }
            return false;
        }
        if (((WorldServer) world).getEntity(nmsEntity.getUniqueID()) != null) return false;
        if (((WorldServer) world).tracker.trackedEntities.b(nmsEntity.getId())) {
            return false;
        }
        if (Arrays.asList(world.getChunkAt(chunkX, chunkZ).getEntitySlices()).contains(nmsEntity)) {
            return false;
        }
        world.getChunkAt(chunkX, chunkZ).a(nmsEntity);
        world.entityList.add(nmsEntity);
        try {
            validateEntityMethod.invoke(world, nmsEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static Field b;
    static Field c;

    static {
        try {
            b = PathfinderGoalSelector.class.getDeclaredField("b");
            b.setAccessible(true);
            c = PathfinderGoalSelector.class.getDeclaredField("c");
            c.setAccessible(true);
        } catch (SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void clearSelectors(EntityInsentient entity) {
        try {
            b.set(entity.targetSelector, Sets.newLinkedHashSet());
            c.set(entity.targetSelector, Sets.newLinkedHashSet());
            b.set(entity.goalSelector, Sets.newLinkedHashSet());
            c.set(entity.goalSelector, Sets.newLinkedHashSet());

        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void unloadChunk(ChunkUnloadEvent evt) {
        Chunk c = evt.getChunk();
        customEntities.put(c.getX(), c.getZ(), Arrays.stream(c.getEntities()).filter(en -> en instanceof NMSEntity).filter(en -> !en.isDead()).map(en -> (NMSEntity) en).filter(nms -> !nms.willSave()).collect(Collectors.toList()));
    }

    @Override
    public void loadChunk(Chunk c) {
        if (!customEntities.contains(c.getX(), c.getZ())) return;
        customEntities.get(c.getX(), c.getZ()).forEach(NMSEntity::spawn);
        customEntities.remove(c.getX(), c.getZ());
    }

    @Override
    public net.tangentmc.nmsUtils.entities.NPC spawnNPC(String name, Location location, String value, String signature) {
        NPC npc = NPC.createNPC(NMSUtils.getInstance(), name, location, value, signature);
        this.npcmanager.addNPC(npc.getBukkitEntity());
        Bukkit.getOnlinePlayers().forEach(npc::spawn);
        return npc.getBukkitEntity();
    }

    @Override
    public net.tangentmc.nmsUtils.entities.NPC spawnNPC(String name, Location location) {
        return spawnNPC(name, location, "", "");
    }

    @Override
    public NMSEntity getNMSEntity(Entity en2) {
        if (en2 instanceof CraftHologramEntity) {
            return new NMSHologramWrapper(en2);
        } else if (en2.getType() == EntityType.ARMOR_STAND) {
            return new BasicNMSArmorStand(en2);
        } else {
            return new BasicNMSEntity(en2);
        }
    }

    @Override
    public void run() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity en : w.getEntities()) {
                net.minecraft.server.v1_9_R2.Entity added = ((CraftEntity) en).getHandle();
                if (added.lastX != added.locX || added.lastY != added.locY || added.lastZ != added.locZ || added.lastPitch != added.pitch || added.lastYaw != added.yaw) {
                    Bukkit.getPluginManager().callEvent(new EntityMoveEvent(en, added.lastX, added.lastY, added.lastZ, added.locX, added.locY, added.locZ, added.pitch, added.lastPitch, added.yaw, added.lastYaw));
                }
                if (en.hasMetadata(NMSEntity.COLLIDE_TAG)) {
                    Collideable.testCollision(added);
                }
            }
        }
    }
}
