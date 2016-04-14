package net.tangentmc.nmsUtils.v1_9_R1.entities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEnderCrystal;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_9_R1.AxisAlignedBB;
import net.minecraft.server.v1_9_R1.DamageSource;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityDamageSource;
import net.minecraft.server.v1_9_R1.EntityGuardian;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.World;
import net.tangentmc.nmsUtils.entities.NMSLaser;
import net.tangentmc.nmsUtils.events.LaserCollideWithEntityEvent;
import net.tangentmc.nmsUtils.events.LaserTargetBlockEvent;
import net.tangentmc.nmsUtils.utils.LocationIterator;
import net.tangentmc.nmsUtils.v1_9_R1.NMSUtilImpl;
import net.tangentmc.nmsUtils.v1_9_R1.entities.LaserEntities.CraftLaserEntity.LaserEntity;
import net.tangentmc.nmsUtils.v1_9_R1.entities.LaserEntities.CraftLaserSourceEntity.LaserSourceEntity;
import net.tangentmc.nmsUtils.v1_9_R1.entities.LaserEntitiesGuardian.CraftLaserDestinationEntity.LaserDestinationEntity;
import net.tangentmc.nmsUtils.v1_9_R1.entities.basic.CraftEnderCrystalEntity.EnderCrystalEntity;
public class LaserEntities {

	public static class LaserBoundingBox extends AxisAlignedBB {
		public LaserBoundingBox(Location b) {
			super(b.getX()-0.5,b.getY()-0.5,b.getZ()-0.5,b.getX()+0.5,b.getY()+0.5,b.getZ()+0.5);
		}
	}
	public static class CraftLaserEntity extends CraftEntity implements NMSLaser {
		public CraftLaserEntity(LaserEntity entity) {
			super((CraftServer) Bukkit.getServer(), entity);
		}

		@Override
		public EntityType getType() {
			return EntityType.COMPLEX_PART;
		}

		@Override
		public NMSLaser getSourcePart() {
			return ((LaserEntity)this.getHandle()).source.getBukkitEntity();
		}
		@Override
		public NMSLaser getDestinationPart() {
			return ((LaserEntity)this.getHandle()).dest.getBukkitEntity();
		}
		@Override
		public void remove() {
			for (Entity e: ((LaserEntity)this.getHandle()).getOwnEntities()) {
				if (e != null)
					e.die();
			}
			LaserEntity extraLoop = ((LaserEntity)this.getHandle()).extra;
			while (extraLoop != null) {
				for (Entity e: extraLoop.getOwnEntities()) {
					if (e != null)
						e.die();
				}
				extraLoop = extraLoop.extra;
			}
		}
		public static class LaserEntity extends Entity {
			LaserSourceEntity source = null;
			LaserDestinationEntity dest = null;
			LaserSourceEntity source2 = null;
			LaserEntity extra = null;
			LaserEntity parent;
			public LaserEntity(World world, Location loc, LaserEntity parent) {
				super(world);
				this.parent = parent;
				this.setInvisible(true);
				source = new LaserSourceEntity(this.world,this);
				source2 = new LaserSourceEntity(this.world,this);
				source.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				source2.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				NMSUtilImpl.addEntityToWorld(world, source);
				NMSUtilImpl.addEntityToWorld(world, source2);
				NMSUtilImpl.addEntityToWorld(world, this);
			}
			public Entity[] getAllEntities() {
				ArrayList<Entity> extras = new ArrayList<>();
				LaserEntity extrab = extra;
				while (extrab != null) {
					extras.addAll(extrab.getOwnEntities());
					extrab = extrab.extra;
				}
				extras.add(source);
				extras.add(source2);
				extras.add(this);
				if (parent != null) {
					extras.addAll(Arrays.asList(parent.getAllEntities()));
				}
				return extras.toArray(new Entity[0]);
			}
			public List<Entity> getOwnEntities() {
				List<Entity> extras = new ArrayList<>();
				LaserEntity extrab = extra;
				while (extrab != null) {
					if (extrab != null)
						extras.add(extrab);
					extrab = extrab.extra;
				}
				extras.add(this);
				extras.add(source);
				extras.add(source2);
				extras = extras.stream().filter(e -> e != null).collect(Collectors.toList());
				return extras;
			}
			public LaserEntity(World world, Location loc) {
				this(world,loc,null);
			}
			@Override
			public void m() {
				if (this.dead) {
					getOwnEntities().forEach(e -> e.getBukkitEntity().remove());
					return;
				}
				if (source != null) {
					LocationIterator it = new LocationIterator(source.getBukkitEntity().getLocation(), 31);
					Location b =null;
					while (it.hasNext()) {
						b = it.next();
						LaserDestination newLoc = testCollision(this,new LaserBoundingBox(b.clone()));
						if (newLoc != null) {
							Location loc = newLoc.getDestination().clone();
							setDestination(b.clone(),loc);
							return;
						}
						LaserTargetBlockEvent evt = new LaserTargetBlockEvent(this.getBukkitEntity(),b.getBlock());
						Bukkit.getPluginManager().callEvent(evt);
						if (!evt.isGoThrough()) {
							break;
						}
					}

					setDestination(b.clone(),null);
				}
			}
			Location old;
			private void setDestination(Location loc, Location extral) {
				Location exc = null;
				if (extral != null) {
					exc = extral.clone();
				} else if (this.source.getBukkitEntity().getLocation().distance(loc) > 30) {
					loc = this.source.getBukkitEntity().getLocation().clone().add(this.source.getBukkitEntity().getLocation().getDirection().multiply(30));
					if (loc.getChunk().isLoaded()) 
						exc = loc.clone();
				}

				if (exc != null) {
					if (old != null && old.getX() == exc.getX() && old.getY() == exc.getY() && old.getZ() == exc.getZ() && old.getYaw() == exc.getYaw() && old.getPitch() == exc.getPitch()) {
						return;
					}
					old = exc;
					if (extra != null) {
						extra.source.setLocation(exc.getX(), exc.getY(), exc.getZ(), exc.getYaw(), exc.getPitch());
					} else {
						extra = new LaserEntity(world, exc, this);
					}
				} else {
					if (old != null && old.getWorld() == loc.getWorld() && loc.distance(old)<1) return;
					old = loc;
					if (extra != null){
						extra.getOwnEntities().forEach(e -> e.getBukkitEntity().remove());
						extra.getBukkitEntity().remove();
						extra = null;
					}
				}
				source2.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				source.targetLocation(loc);
				source2.targetLocation(source.getBukkitEntity().getLocation());
			}
			@Override
			public void a(NBTTagCompound nbttagcompound) {}
			@Override
			public void b(NBTTagCompound nbttagcompound) {}
			@Override
			public boolean c(NBTTagCompound nbttagcompound) {
				return false;
			}
			@Override
			public boolean d(NBTTagCompound nbttagcompound) { 
				return false;
			}
			@Override
			public void e(NBTTagCompound nbttagcompound) {}
			@Override
			protected void i() {}
			@Override
			public CraftLaserEntity getBukkitEntity() {
				return new CraftLaserEntity(this);
			}
			@Override
			public void g(double d0, double d1, double d2) {}

		}
		@Override
		public void spawn() {
			for (Entity en: ((LaserEntity)this.entity).getAllEntities()) {
				if (en != null)
					NMSUtilImpl.addEntityToWorld(en.world, en);
			}
		}

		//Lasers never save.
		@Override
		public boolean willSave() {	return false; }

		@Override
		public void setWillSave(boolean b) {}

		@Override
		public List<NMSLaser> getAllEntities() {
			return Arrays.stream(((LaserEntity)entity).getAllEntities()).filter(m->m != null).map(m->(NMSLaser)m.getBukkitEntity()).collect(Collectors.toList());
		}
	}



	public static class CraftLaserSourceEntity extends CraftEnderCrystal implements NMSLaser{
		public CraftLaserSourceEntity(LaserSourceEntity entity) {
			super((CraftServer) Bukkit.getServer(), entity);

		}

		@Override
		public NMSLaser getSourcePart() {
			return this;
		}
		@Override
		public NMSLaser getDestinationPart() {
			return ((LaserSourceEntity)this.getHandle()).parent.dest.getBukkitEntity();
		}

		public static class LaserSourceEntity extends EnderCrystalEntity {
			@Getter
			LaserEntity parent;
			Entity target;
			public LaserSourceEntity(World world, LaserEntity parent) {
				super(world);
				this.parent = parent;
				this.setInvisible(true);

			}
			@Override
			public void b(NBTTagCompound nbttagcompound) {}
			@Override
			public boolean c(NBTTagCompound nbttagcompound) {
				return false;
			}
			@Override
			public boolean d(NBTTagCompound nbttagcompound) { 
				return false;
			}
			@Override
			public void e(NBTTagCompound nbttagcompound) {}

			@Override
			public boolean damageEntity(DamageSource damageSource, float amount) {
				if (damageSource instanceof EntityDamageSource) {
					EntityDamageSource entityDamageSource = (EntityDamageSource) damageSource;
					if (entityDamageSource.getEntity() instanceof EntityPlayer) {
						Bukkit.getPluginManager().callEvent(new PlayerInteractEntityEvent(((EntityPlayer) entityDamageSource.getEntity()).getBukkitEntity(), ((Entity)this).getBukkitEntity()));
					}
				}
				return false;
			}
			@Override
			public boolean isInvulnerable(DamageSource source) {
				return true;
			}
			@Override
			public void m() {}

			@Override
			public void g(double d0, double d1, double d2) {}

			@Override
			public CraftLaserSourceEntity getBukkitEntity() {
				return new CraftLaserSourceEntity(this);
			}
		}
		@Override
		public void spawn() {
			NMSUtilImpl.addEntityToWorld(((Entity)entity).world, ((Entity)entity));
		}

		//Lasers never save.
		@Override
		public boolean willSave() {	return false; }

		@Override
		public void setWillSave(boolean b) {}

		@Override
		public List<NMSLaser> getAllEntities() {
			return Arrays.stream(((LaserSourceEntity)entity).getParent().getAllEntities()).filter(m->m != null).map(m->(NMSLaser)m.getBukkitEntity()).collect(Collectors.toList());
		}
	}
	@Getter
	@AllArgsConstructor
	public static class LaserDestination {
		public Entity source;
		public Location destination;
	}
	public static LaserDestination testCollision(LaserEntity en, LaserBoundingBox box) {
		org.bukkit.entity.Entity e = en.getBukkitEntity();
		List<Entity> list = en.world.getEntities(en, box);
		list.removeAll(Arrays.asList(en.getAllEntities()));
		for (int i = 0; i < list.size(); ++i) {
			Entity entity1 = list.get(i);
			LaserCollideWithEntityEvent ev = new LaserCollideWithEntityEvent(e, entity1.getBukkitEntity());
			Bukkit.getPluginManager().callEvent(ev);
			if (ev.getNewLoc() != null) return new LaserDestination(entity1,ev.getNewLoc().clone());
		}

		return null;
	}


}
