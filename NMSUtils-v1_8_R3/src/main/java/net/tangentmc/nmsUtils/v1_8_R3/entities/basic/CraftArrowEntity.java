package net.tangentmc.nmsUtils.v1_8_R3.entities.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;
import net.tangentmc.nmsUtils.v1_8_R3.entities.effects.Collideable;

public class CraftArrowEntity extends CraftArrow implements BasicNMSEntity {

	public CraftArrowEntity(ArrowEntity entity) {
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
	public static class ArrowEntity extends EntityArrow implements Collideable{
		public ArrowEntity(World world) {
			super(world);
		}
		@Override
		public CraftEntity getBukkitEntity() {
			return new CraftArrowEntity(this);
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
	@Override
	public void setWillSave(boolean b) {
		((ArrowEntity)entity).willSave = b;
	}
	@Override
	public boolean willSave() {
		return ((ArrowEntity)entity).willSave;
	}
}
