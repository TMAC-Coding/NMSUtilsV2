package net.tangentmc.nmsUtils.v1_9_R1.entities.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R1.CraftServer;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftEnderSignal;

import net.minecraft.server.v1_9_R1.EntityEnderSignal;
import net.minecraft.server.v1_9_R1.NBTTagCompound;
import net.minecraft.server.v1_9_R1.World;
import net.tangentmc.nmsUtils.v1_9_R1.entities.basic.BasicNMSEntity;
import net.tangentmc.nmsUtils.v1_9_R1.entities.effects.Collideable;

public class CraftEnderSignalEntity extends CraftEnderSignal implements BasicNMSEntity {

	public CraftEnderSignalEntity(EnderSignalEntity entity) {
		super((CraftServer) Bukkit.getServer(), entity);
	}
	
	public void setWillSave(boolean b) {
		((EnderSignalEntity)entity).willSave = b;
	}
	@Override
	public boolean willSave() {
		return ((EnderSignalEntity)entity).willSave;
	}
	public static class EnderSignalEntity extends EntityEnderSignal implements Collideable{
		public EnderSignalEntity(World world) {
			super(world);
		}
		public EnderSignalEntity(World world, double x, double y, double z) {
			super(world,x,y,z);
		}
		@Override
		public CraftEntity getBukkitEntity() {
			return new CraftEnderSignalEntity(this);
		}
		@Override
		public void m() {
			this.testCollision();
			super.m();
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
