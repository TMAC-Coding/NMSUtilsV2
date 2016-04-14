package net.tangentmc.nmsUtils.v1_8_R3.entities.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftRabbit;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityRabbit;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.BasicNMSEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.effects.Collideable;

public class CraftRabbitEntity extends CraftRabbit implements BasicNMSEntity {
	
	public CraftRabbitEntity(RabbitEntity entity) {
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
	public void setFrozen(boolean b) {
		((RabbitEntity) entity).setFrozen(b);
	}
	public boolean isFrozen() {
		return ((RabbitEntity) entity).isFrozen();
	}
	@Override
	public void setWillSave(boolean b) {
		((RabbitEntity)entity).willSave = b;
	}
	@Override
	public boolean willSave() {
		return ((RabbitEntity)entity).willSave;
	}
	public static class RabbitEntity extends EntityRabbit implements Collideable{
		@Getter
		@Setter
		boolean frozen;
		public RabbitEntity(World world) {
			super(world);
		}
		@Override
		public CraftEntity getBukkitEntity() {
			return new CraftRabbitEntity(this);
		}
		@Override
		public void t_() {
			this.testCollision();
			super.t_();
			this.testMovement();
		}
		@Override
		public void m() {
			if (!frozen) {
				super.m();
			}
		}
		@Override
		public void g(double d0, double d1, double d2) {
			if (!frozen) {
				super.g(d0,d1,d2);
			}
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
}
