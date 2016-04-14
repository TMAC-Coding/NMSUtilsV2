package net.tangentmc.nmsUtils.v1_9_R1;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.SneakyThrows;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityMinecartAbstract;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.IWorldAccess;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.SoundCategory;
import net.minecraft.server.v1_9_R1.SoundEffect;
import net.minecraft.server.v1_9_R1.World;
import net.minecraft.server.v1_9_R1.WorldServer;
import net.tangentmc.nmsUtils.NMSUtils;
import net.tangentmc.nmsUtils.entities.NMSEntity;
import net.tangentmc.nmsUtils.events.EntityDespawnEvent;
import net.tangentmc.nmsUtils.events.EntitySpawnEvent;
import net.tangentmc.nmsUtils.utils.ReflectionManager;

public class WorldManager implements IWorldAccess {
	private HashSet<EntityHuman> players = new HashSet<EntityHuman>();
	private static Field f;
	private static Field f2;
	WorldServer world;
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public WorldManager(org.bukkit.World world) {
		this.world = ((CraftWorld)world).getHandle();
		this.world.addIWorldAccess(this);
		//Entities will return based on the world handle, 
		//while bukkit methods are based on the Bukkit.getServers() list of worlds
		CraftWorld nmsWorld = new NMSCraftWorld((CraftWorld) world);
		ReflectionManager.setFinalStatic(f2, this.world, nmsWorld);
		Map<String, org.bukkit.World> worlds = (Map<String, org.bukkit.World>) WorldManager.worlds.get(Bukkit.getServer());
		worlds.put(world.getName(), nmsWorld);
		ReflectionManager.setFinalStatic(WorldManager.worlds, Bukkit.getServer(), worlds);
	}
	private static Field worlds;
	static {
		try {
			f = World.class.getDeclaredField("u");
			f2 = World.class.getDeclaredField("world");
			worlds = CraftServer.class.getDeclaredField("worlds");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		worlds.setAccessible(true);
		f.setAccessible(true);
		f2.setAccessible(true);
	}
	@SneakyThrows
	public void remove() {
		@SuppressWarnings("unchecked")
		List<IWorldAccess> l = (List<IWorldAccess>)f.get(world);
		l.remove(this);
		f.set(world, l);
		this.players.clear();
	}
	@Override
	public final void a(Entity added) {
		if (added != null) {
			if (added instanceof EntityPlayer && !this.players.add((EntityPlayer) added)) {
				return;
			}
			if (!(added.getBukkitEntity() instanceof NMSEntity) && !(added instanceof EntityPlayer) && added.getClass().getName().contains("net.minecraft.server")) {
				
				new BukkitRunnable(){
					@Override
					public void run() {
						replaceEntity(added);
					}}.runTaskLater(NMSUtils.getInstance(),1l);
					return;
			}
			Bukkit.getPluginManager().callEvent(new EntitySpawnEvent(added.getBukkitEntity()));
		}
	}
	protected void replaceEntity(Entity added) {
		NBTTagCompound old = new NBTTagCompound();
		added.d(old);
		int tickslived = added.ticksLived;
		world.removeEntity(added);
		Entity tnew = a(old);
		if (tnew != null) {
			tnew.ticksLived = tickslived;
			NMSUtilImpl.addEntityToWorld(world, tnew);
		}
	}
	public Entity a(NBTTagCompound nbttagcompound) {
		Entity entity = null;

		if ("Minecart".equals(nbttagcompound.getString("id"))) {
			nbttagcompound.setString("id", EntityMinecartAbstract.EnumMinecartType.values()[nbttagcompound.getInt("Type")].b());
			nbttagcompound.remove("Type");
		}

		try {
			Class<? extends Entity> oclass = NMSEntityTypes.getClassById(nbttagcompound.getString("id"));
			if (oclass != null) {
				entity = oclass.getConstructor(new Class[] { World.class}).newInstance(world);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (entity != null) {
			entity.f(nbttagcompound);
		} else {
			Bukkit.getLogger().warning("Skipping Entity with id " + nbttagcompound.getString("id"));
		}

		return entity;
	}

	@Override
	public final void b(Entity removed) {
		if (removed != null) {
			if (removed instanceof EntityPlayer && !this.players.remove(removed)) {
				return;
			}
			Bukkit.getPluginManager().callEvent(new EntityDespawnEvent(removed.getBukkitEntity()));
		}
	}
	@Override
	public void a(BlockPosition arg0) {}
	@Override
	public void a(SoundEffect arg0, BlockPosition arg1) {}
	@Override
	public void a(int arg0, BlockPosition arg1, int arg2) {}
	@Override
	public void a(EntityHuman arg0, int arg1, BlockPosition arg2, int arg3) {}
	@Override
	public void a(World arg0, BlockPosition arg1, IBlockData arg2, IBlockData arg3, int arg4) {}
	@Override
	public void a(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {}
	@Override
	public void a(EntityHuman arg0, SoundEffect arg1, SoundCategory arg2, double arg3, double arg4, double arg5,
			float arg6, float arg7) {}
	@Override
	public void a(int arg0, boolean arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7,
			int... arg8) {}
	@Override
	public void b(int arg0, BlockPosition arg1, int arg2) {}
}
