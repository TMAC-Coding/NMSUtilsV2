package net.tangentmc.nmsUtils.v1_9_R1.entities.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftIronGolem;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_9_R1.EntityIronGolem;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.World;
import net.tangentmc.nmsUtils.v1_9_R1.entities.basic.BasicNMSEntity;
import net.tangentmc.nmsUtils.v1_9_R1.entities.effects.Collideable;

public class CraftIronGolemEntity extends CraftIronGolem implements BasicNMSEntity {
	
	public CraftIronGolemEntity(IronGolemEntity entity) {
		super((CraftServer) Bukkit.getServer(), entity);
	}
	
	public void setFrozen(boolean b) {
		((IronGolemEntity) entity).setFrozen(b);
	}
	public boolean isFrozen() {
		return ((IronGolemEntity) entity).isFrozen();
	}

	@Override
	public void setWillSave(boolean b) {
		((IronGolemEntity)entity).willSave = b;
	}
	@Override
	public boolean willSave() {
		return ((IronGolemEntity)entity).willSave;
	}
	public static class IronGolemEntity extends EntityIronGolem implements Collideable{
		@Getter
		@Setter
		boolean frozen;
		public IronGolemEntity(World world) {
			super(world);
		}
		@Override
		public CraftEntity getBukkitEntity() {
			return new CraftIronGolemEntity(this);
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
