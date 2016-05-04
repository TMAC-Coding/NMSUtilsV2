package net.tangentmc.nmsUtils.v1_9_R1.entities;

import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.EntityArmorStand;
import net.minecraft.server.v1_9_R1.World;
import net.tangentmc.nmsUtils.v1_9_R1.NMSUtilImpl;


public class FrozenStandEntity extends EntityArmorStand {
	Entity parent;
	public FrozenStandEntity(Entity parent, World world, double x, double y, double z) {
		super(world, x, y, z);
		this.parent = parent;
		setMarker(true);
		setGravity(false);
		setInvisible(true);
		setSmall(true);
		NMSUtilImpl.addEntityToWorld(world, this);
	}
	@Override
	public void m() {
		super.m();
		parent.startRiding(this);
	}


}
