package net.tangentmc.nmsUtils.v1_8_R3.entities.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftExperienceOrb;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.BasicNMSEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.effects.Collideable;

public class CraftExperienceOrbEntity extends CraftExperienceOrb implements BasicNMSEntity {

	public CraftExperienceOrbEntity(ExperienceOrbEntity entity) {
		super((CraftServer) Bukkit.getServer(), entity);
	}
	//For whatever reason, this needs to be overridden...
		@Override
		public boolean teleport(Location location, TeleportCause cause) {
			if (entity.passenger != null || entity.dead) {
	            return false;
	        }

	        // If this entity is riding another entity, we must dismount before teleporting.
	        entity.mount(null);

	        entity.world = ((CraftWorld) location.getWorld()).getHandle();
	        entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	        // entity.setLocation() throws no event, and so cannot be cancelled
	        return true;
		}
	public void setWillSave(boolean b) {
		((ExperienceOrbEntity)entity).willSave = b;
	}
	@Override
	public boolean willSave() {
		return ((ExperienceOrbEntity)entity).willSave;
	}
	public static class ExperienceOrbEntity extends EntityExperienceOrb implements Collideable{
		public ExperienceOrbEntity(World world) {
			super(world);
		}
		public ExperienceOrbEntity(World world, double x, double y, double z, int i) {
			super(world,x,y,z,i);
		}
		@Override
		public CraftEntity getBukkitEntity() {
			return new CraftExperienceOrbEntity(this);
		}
		@Override
		public void t_() {
			this.testCollision();
			super.t_();
			this.testMovement();

		}
		boolean willSave = true;
		@Override
		public void a(NBTTagCompound nbttagcompound) {
			if (willSave) super.a(nbttagcompound);
		}
		@Override
		public void b(NBTTagCompound nbttagcompound) {
			if (willSave) super.b(nbttagcompound);
		}
		@Override
		public boolean c(NBTTagCompound nbttagcompound) {
			return willSave?super.c(nbttagcompound):false;
		}
		@Override
		public boolean d(NBTTagCompound nbttagcompound) { 
			return willSave?super.d(nbttagcompound):false;
		}
		@Override
		public void e(NBTTagCompound nbttagcompound) {
			if (willSave) super.e(nbttagcompound);
		}
	}
	@Override
	public void setFrozen(boolean b) {
		
	}
	@Override
	public boolean isFrozen() {
		return false;
	}
}
