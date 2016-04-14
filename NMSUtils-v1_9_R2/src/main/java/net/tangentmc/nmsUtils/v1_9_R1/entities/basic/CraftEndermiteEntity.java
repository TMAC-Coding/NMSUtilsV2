package net.tangentmc.nmsUtils.v1_9_R1.entities.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEndermite;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_9_R1.EntityEndermite;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.World;
import net.tangentmc.nmsUtils.v1_9_R1.entities.basic.BasicNMSEntity;
import net.tangentmc.nmsUtils.v1_9_R1.entities.effects.Collideable;

public class CraftEndermiteEntity extends CraftEndermite implements BasicNMSEntity {
	
	public CraftEndermiteEntity(EndermiteEntity entity) {
		super((CraftServer) Bukkit.getServer(), entity);
	}
	
	public void setFrozen(boolean b) {
		((EndermiteEntity) entity).setFrozen(b);
	}
	public boolean isFrozen() {
		return ((EndermiteEntity) entity).isFrozen();
	}
	public void setWillSave(boolean b) {
		((EndermiteEntity)entity).willSave = b;
	}
	@Override
	public boolean willSave() {
		return ((EndermiteEntity)entity).willSave;
	}
	public static class EndermiteEntity extends EntityEndermite implements Collideable{
		@Getter
		@Setter
		boolean frozen;
		public EndermiteEntity(World world) {
			super(world);
		}
		@Override
		public CraftEntity getBukkitEntity() {
			return new CraftEndermiteEntity(this);
		}
		@Override
		public void m() {
			this.testCollision();
			super.m();
			this.testMovement();
		}
		@Override
		public void n() {
			if (!frozen) {
				super.n();
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
			return willSave && super.c(nbttagcompound);
		}
		@Override
		public boolean d(NBTTagCompound nbttagcompound) { 
			return willSave && super.d(nbttagcompound);
		}
		@Override
		public void e(NBTTagCompound nbttagcompound) {
			if (willSave) super.e(nbttagcompound);
		}
	}
}
