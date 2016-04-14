package net.tangentmc.nmsUtils.v1_8_R3;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftEffect;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLightningStrike;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.metadata.BlockMetadataStore;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Cow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Weather;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockDiodeAbstract;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.EntityHanging;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minecraft.server.v1_8_R3.GroupDataEntity;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateTime;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftArmorStandEntity.ArmorStandEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftArrowEntity.ArrowEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftBatEntity.BatEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftBlazeEntity.BlazeEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftBoatEntity.BoatEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftCaveSpiderEntity.CaveSpiderEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftChickenEntity.ChickenEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftCowEntity.CowEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftCreeperEntity.CreeperEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftEggEntity.EggEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftEnderCrystalEntity.EnderCrystalEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftEnderDragonEntity.EnderDragonEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftEnderPearlEntity.EnderPearlEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftEnderSignalEntity.EnderSignalEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftEndermanEntity.EndermanEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftEndermiteEntity.EndermiteEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftExperienceOrbEntity.ExperienceOrbEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftFallingBlockEntity.FallingBlockEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftFireworksEntity.FireworksEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftGhastEntity.GhastEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftGiantZombieEntity.GiantZombieEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftGuardianEntity.GuardianEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftHorseEntity.HorseEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftIronGolemEntity.IronGolemEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftItemEntity.ItemEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftItemFrameEntity.ItemFrameEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftLargeFireballEntity.LargeFireballEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftLeashEntity.LeashEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMagmaCubeEntity.MagmaCubeEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMinecartChestEntity.MinecartChestEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMinecartFurnaceEntity.MinecartFurnaceEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMinecartHopperEntity.MinecartHopperEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMinecartMobSpawnerEntity.MinecartMobSpawnerEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMinecartRideableEntity.MinecartRideableEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMinecartTNTEntity.MinecartTNTEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftMushroomCowEntity.MushroomCowEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftOcelotEntity.OcelotEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftPaintingEntity.PaintingEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftPigEntity.PigEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftPigZombieEntity.PigZombieEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftPotionEntity.PotionEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftRabbitEntity.RabbitEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSheepEntity.SheepEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSilverfishEntity.SilverfishEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSkeletonEntity.SkeletonEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSlimeEntity.SlimeEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSmallFireballEntity.SmallFireballEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSnowballEntity.SnowballEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSnowmanEntity.SnowmanEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSpiderEntity.SpiderEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftSquidEntity.SquidEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftTNTPrimedEntity.TNTPrimedEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftThrownExpBottleEntity.ThrownExpBottleEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftVillagerEntity.VillagerEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftWitchEntity.WitchEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftWitherEntity.WitherEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftWitherSkullEntity.WitherSkullEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftWolfEntity.WolfEntity;
import net.tangentmc.nmsUtils.v1_8_R3.entities.basic.CraftZombieEntity.ZombieEntity;

public class NMSCraftWorld extends CraftWorld{

	private CraftWorld world;
	public NMSCraftWorld(CraftWorld world) {
		super(world.getHandle(), world.getGenerator(), world.getEnvironment());
		this.world = world;
	}
	@Override
	public FallingBlock spawnFallingBlock(Location location, org.bukkit.Material material, byte data) throws IllegalArgumentException {
		Validate.notNull(location, "Location cannot be null");
		Validate.notNull(material, "Material cannot be null");
		Validate.isTrue(material.isBlock(), "Material must be a block");
		double x = location.getBlockX() + 0.5;
		double y = location.getBlockY() + 0.5;
		double z = location.getBlockZ() + 0.5;
		EntityFallingBlock entity = new FallingBlockEntity(world.getHandle(), x, y, z, net.minecraft.server.v1_8_R3.Block.getById(material.getId()).fromLegacyData(data));
		entity.ticksLived = 1;

		world.addEntity(entity, SpawnReason.CUSTOM);
		return (FallingBlock) entity.getBukkitEntity();
	}

	public net.minecraft.server.v1_8_R3.Entity createEntity(Location location, Class<? extends Entity> clazz) throws IllegalArgumentException {
		if (location == null || clazz == null) {
			throw new IllegalArgumentException("Location or Entity class cannot be null");
		}

		net.minecraft.server.v1_8_R3.Entity Entity = null;

		double x = location.getX();
		double y = location.getY();
		double z = location.getZ();
		float pitch = location.getPitch();
		float yaw = location.getYaw();

		// order is important for some of these
		if (Boat.class.isAssignableFrom(clazz)) {
			Entity = new BoatEntity(world.getHandle(), x, y, z);
		} else if (FallingBlock.class.isAssignableFrom(clazz)) {
			x = location.getBlockX();
			y = location.getBlockY();
			z = location.getBlockZ();
			IBlockData blockData = world.getHandle().getType(new BlockPosition(x, y, z));
			int type = CraftMagicNumbers.getId(blockData.getBlock());
			int data = blockData.getBlock().toLegacyData(blockData);

			Entity = new FallingBlockEntity(world.getHandle(), x + 0.5, y + 0.5, z + 0.5, net.minecraft.server.v1_8_R3.Block.getById(type).fromLegacyData(data));
		} else if (Projectile.class.isAssignableFrom(clazz)) {
			if (Snowball.class.isAssignableFrom(clazz)) {
				Entity = new SnowballEntity(world.getHandle(), x, y, z);
			} else if (Egg.class.isAssignableFrom(clazz)) {
				Entity = new EggEntity(world.getHandle(), x, y, z);
			} else if (Arrow.class.isAssignableFrom(clazz)) {
				Entity = new ArrowEntity(world.getHandle());
				Entity.setPositionRotation(x, y, z, 0, 0);
			} else if (ThrownExpBottle.class.isAssignableFrom(clazz)) {
				Entity = new ThrownExpBottleEntity(world.getHandle());
				Entity.setPositionRotation(x, y, z, 0, 0);
			} else if (EnderPearl.class.isAssignableFrom(clazz)) {
				Entity = new EnderPearlEntity(world.getHandle(), null);
				Entity.setPositionRotation(x, y, z, 0, 0);
			} else if (ThrownPotion.class.isAssignableFrom(clazz)) {
				Entity = new PotionEntity(world.getHandle(), x, y, z, CraftItemStack.asNMSCopy(new ItemStack(org.bukkit.Material.POTION, 1)));
			} else if (Fireball.class.isAssignableFrom(clazz)) {
				if (SmallFireball.class.isAssignableFrom(clazz)) {
					Entity = new SmallFireballEntity(world.getHandle());
				} else if (WitherSkull.class.isAssignableFrom(clazz)) {
					Entity = new WitherSkullEntity(world.getHandle());
				} else {
					Entity = new LargeFireballEntity(world.getHandle());
				}
				Entity.setPositionRotation(x, y, z, yaw, pitch);
				Vector direction = location.getDirection().multiply(10);
				((EntityFireball) Entity).setDirection(direction.getX(), direction.getY(), direction.getZ());
			}
		} else if (Minecart.class.isAssignableFrom(clazz)) {
			if (PoweredMinecart.class.isAssignableFrom(clazz)) {
				Entity = new MinecartFurnaceEntity(world.getHandle(), x, y, z);
			} else if (StorageMinecart.class.isAssignableFrom(clazz)) {
				Entity = new MinecartChestEntity(world.getHandle(), x, y, z);
			} else if (ExplosiveMinecart.class.isAssignableFrom(clazz)) {
				Entity = new MinecartTNTEntity(world.getHandle(), x, y, z);
			} else if (HopperMinecart.class.isAssignableFrom(clazz)) {
				Entity = new MinecartHopperEntity(world.getHandle(), x, y, z);
			} else if (SpawnerMinecart.class.isAssignableFrom(clazz)) {
				Entity = new MinecartMobSpawnerEntity(world.getHandle(), x, y, z);
			} else { // Default to rideable minecart for pre-rideable compatibility
				Entity = new MinecartRideableEntity(world.getHandle(), x, y, z);
			}
		} else if (EnderSignal.class.isAssignableFrom(clazz)) {
			Entity = new EnderSignalEntity(world.getHandle(), x, y, z);
		} else if (EnderCrystal.class.isAssignableFrom(clazz)) {
			Entity = new EnderCrystalEntity(world.getHandle());
			Entity.setPositionRotation(x, y, z, 0, 0);
		} else if (LivingEntity.class.isAssignableFrom(clazz)) {
			if (Chicken.class.isAssignableFrom(clazz)) {
				Entity = new ChickenEntity(world.getHandle());
			} else if (Cow.class.isAssignableFrom(clazz)) {
				if (MushroomCow.class.isAssignableFrom(clazz)) {
					Entity = new MushroomCowEntity(world.getHandle());
				} else {
					Entity = new CowEntity(world.getHandle());
				}
			} else if (Golem.class.isAssignableFrom(clazz)) {
				if (Snowman.class.isAssignableFrom(clazz)) {
					Entity = new SnowmanEntity(world.getHandle());
				} else if (IronGolem.class.isAssignableFrom(clazz)) {
					Entity = new IronGolemEntity(world.getHandle());
				}
			} else if (Creeper.class.isAssignableFrom(clazz)) {
				Entity = new CreeperEntity(world.getHandle());
			} else if (Ghast.class.isAssignableFrom(clazz)) {
				Entity = new GhastEntity(world.getHandle());
			} else if (Pig.class.isAssignableFrom(clazz)) {
				Entity = new PigEntity(world.getHandle());
			} else if (Player.class.isAssignableFrom(clazz)) {
				// need a net server handler for this one
			} else if (Sheep.class.isAssignableFrom(clazz)) {
				Entity = new SheepEntity(world.getHandle());
			} else if (Horse.class.isAssignableFrom(clazz)) {
				Entity = new HorseEntity(world.getHandle());
			} else if (Skeleton.class.isAssignableFrom(clazz)) {
				Entity = new SkeletonEntity(world.getHandle());
			} else if (Slime.class.isAssignableFrom(clazz)) {
				if (MagmaCube.class.isAssignableFrom(clazz)) {
					Entity = new MagmaCubeEntity(world.getHandle());
				} else {
					Entity = new SlimeEntity(world.getHandle());
				}
			} else if (Spider.class.isAssignableFrom(clazz)) {
				if (CaveSpider.class.isAssignableFrom(clazz)) {
					Entity = new CaveSpiderEntity(world.getHandle());
				} else {
					Entity = new SpiderEntity(world.getHandle());
				}
			} else if (Squid.class.isAssignableFrom(clazz)) {
				Entity = new SquidEntity(world.getHandle());
			} else if (Tameable.class.isAssignableFrom(clazz)) {
				if (Wolf.class.isAssignableFrom(clazz)) {
					Entity = new WolfEntity(world.getHandle());
				} else if (Ocelot.class.isAssignableFrom(clazz)) {
					Entity = new OcelotEntity(world.getHandle());
				}
			} else if (PigZombie.class.isAssignableFrom(clazz)) {
				Entity = new PigZombieEntity(world.getHandle());
			} else if (Zombie.class.isAssignableFrom(clazz)) {
				Entity = new ZombieEntity(world.getHandle());
			} else if (Giant.class.isAssignableFrom(clazz)) {
				Entity = new GiantZombieEntity(world.getHandle());
			} else if (Silverfish.class.isAssignableFrom(clazz)) {
				Entity = new SilverfishEntity(world.getHandle());
			} else if (Enderman.class.isAssignableFrom(clazz)) {
				Entity = new EndermanEntity(world.getHandle());
			} else if (Blaze.class.isAssignableFrom(clazz)) {
				Entity = new BlazeEntity(world.getHandle());
			} else if (Villager.class.isAssignableFrom(clazz)) {
				Entity = new VillagerEntity(world.getHandle());
			} else if (Witch.class.isAssignableFrom(clazz)) {
				Entity = new WitchEntity(world.getHandle());
			} else if (Wither.class.isAssignableFrom(clazz)) {
				Entity = new WitherEntity(world.getHandle());
			} else if (ComplexLivingEntity.class.isAssignableFrom(clazz)) {
				if (EnderDragon.class.isAssignableFrom(clazz)) {
					Entity = new EnderDragonEntity(world.getHandle());
				}
			} else if (Ambient.class.isAssignableFrom(clazz)) {
				if (Bat.class.isAssignableFrom(clazz)) {
					Entity = new BatEntity(world.getHandle());
				}
			} else if (Rabbit.class.isAssignableFrom(clazz)) {
				Entity = new RabbitEntity(world.getHandle());
			} else if (Endermite.class.isAssignableFrom(clazz)) {
				Entity = new EndermiteEntity(world.getHandle());
			} else if (Guardian.class.isAssignableFrom(clazz)){
				Entity = new GuardianEntity(world.getHandle());
			} else if (ArmorStand.class.isAssignableFrom(clazz)) {
				Entity = new ArmorStandEntity(world.getHandle(), x, y, z);
			}

			if (Entity != null) {
				Entity.setLocation(x, y, z, yaw, pitch);
			}
		} else if (Hanging.class.isAssignableFrom(clazz)) {
			Block block = getBlockAt(location);
			BlockFace face = BlockFace.SELF;

			int width = 16; // 1 full block, also painting smallest size.
			int height = 16; // 1 full block, also painting smallest size.

			if (ItemFrame.class.isAssignableFrom(clazz)) {
				width = 12;
				height = 12;
			} else if (LeashHitch.class.isAssignableFrom(clazz)) {
				width = 9;
				height = 9;
			}

			BlockFace[] faces = new BlockFace[]{BlockFace.EAST,BlockFace.NORTH,BlockFace.WEST,BlockFace.SOUTH};
			final BlockPosition pos = new BlockPosition((int) x, (int) y, (int) z);
			for (BlockFace dir : faces) {
				net.minecraft.server.v1_8_R3.Block nmsBlock = CraftMagicNumbers.getBlock(block.getRelative(dir));
				if (nmsBlock.getMaterial().isBuildable() || BlockDiodeAbstract.d(nmsBlock)) {
					boolean taken = false;
					AxisAlignedBB bb = EntityHanging.calculateBoundingBox(pos,CraftBlock.blockFaceToNotch(dir).opposite(),width,height);
					List<net.minecraft.server.v1_8_R3.Entity> list = (List<net.minecraft.server.v1_8_R3.Entity>) world.getHandle().getEntities(null, bb);
					for (Iterator<net.minecraft.server.v1_8_R3.Entity> it = list.iterator(); !taken && it.hasNext();) {
						net.minecraft.server.v1_8_R3.Entity e = it.next();
						if (e instanceof EntityHanging) {
							taken = true; // Hanging entities do not like hanging entities which intersect them.
						}
					}

					if (!taken) {
						face = dir;
						break;
					}
				}
			}

			EnumDirection dir = CraftBlock.blockFaceToNotch(face).opposite();

			if (Painting.class.isAssignableFrom(clazz)) {
				Entity = new PaintingEntity(world.getHandle(), new BlockPosition((int) x, (int) y, (int) z), dir);
			} else if (ItemFrame.class.isAssignableFrom(clazz)) {
				Entity = new ItemFrameEntity(world.getHandle(), new BlockPosition((int) x, (int) y, (int) z), dir);
			} else if (LeashHitch.class.isAssignableFrom(clazz)) {
				Entity = new LeashEntity(world.getHandle(), new BlockPosition((int) x, (int) y, (int) z));
				Entity.attachedToPlayer = true;
			}

			if (Entity != null && !((EntityHanging) Entity).survives()) {
				throw new IllegalArgumentException("Cannot spawn hanging Entity for " + clazz.getName() + " at " + location);
			}
		} else if (TNTPrimed.class.isAssignableFrom(clazz)) {
			Entity = new TNTPrimedEntity(world.getHandle(), x, y, z, null);
		} else if (ExperienceOrb.class.isAssignableFrom(clazz)) {
			Entity = new ExperienceOrbEntity(world.getHandle(), x, y, z, 0);
		} else if (Weather.class.isAssignableFrom(clazz)) {
			// not sure what this can do
			if (LightningStrike.class.isAssignableFrom(clazz)) {
				Entity = new EntityLightning(world.getHandle(), x, y, z);
				// what is this, I don't even
			}
		} else if (Firework.class.isAssignableFrom(clazz)) {
			Entity = new FireworksEntity(world.getHandle(), x, y, z, null);
		}

		if (Entity != null) {
			return Entity;
		}

		throw new IllegalArgumentException("Cannot spawn an Entity for " + clazz.getName());
	}

	public Block getBlockAt(int x, int y, int z) {
		return world.getBlockAt(x, y, z);
	}

	public int getBlockTypeIdAt(int x, int y, int z) {
		return world.getBlockTypeIdAt(x,y,z);
	}

	public int getHighestBlockYAt(int x, int z) {
		return world.getHighestBlockYAt(x, z);
	}

	public Location getSpawnLocation() {
		return world.getSpawnLocation();
	}

	public boolean setSpawnLocation(int x, int y, int z) {
		return world.setSpawnLocation(x, y, z);
	}

	public Chunk getChunkAt(int x, int z) {
		return world.getChunkAt(x, z);
	}

	public Chunk getChunkAt(Block block) {
		return world.getChunkAt(block);
	}

	public boolean isChunkLoaded(int x, int z) {
		return world.isChunkLoaded(x, z);
	}

	public Chunk[] getLoadedChunks() {
		return world.getLoadedChunks();
	}

	public void loadChunk(int x, int z) {
		world.loadChunk(x,z);
	}

	public boolean unloadChunk(Chunk chunk) {
		return world.unloadChunk(chunk.getX(), chunk.getZ());
	}

	public boolean unloadChunk(int x, int z) {
		return world.unloadChunk(x, z, true);
	}

	public boolean unloadChunk(int x, int z, boolean save) {
		return world.unloadChunk(x, z, save, false);
	}

	public boolean unloadChunkRequest(int x, int z) {
		return world.unloadChunkRequest(x, z, true);
	}

	public boolean unloadChunkRequest(int x, int z, boolean safe) {
		return world.unloadChunkRequest(x, z, safe);
	}

	public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
		return world.unloadChunk(x, z, save, safe);
	}

	public boolean regenerateChunk(int x, int z) {
		return world.unloadChunk(x,z);
	}

	public boolean refreshChunk(int x, int z) {
		return world.refreshChunk(x, z);
	}

	public boolean isChunkInUse(int x, int z) {
		return world.isChunkInUse(x, z);
	}

	public boolean loadChunk(int x, int z, boolean generate) {
		return world.loadChunk(x, z, generate);
	}
	
	public boolean isChunkLoaded(Chunk chunk) {
		return world.isChunkLoaded(chunk.getX(), chunk.getZ());
	}

	public void loadChunk(Chunk chunk) {
		world.loadChunk(chunk);
	}

	public WorldServer getHandle() {
		return world.getHandle();
	}

	public org.bukkit.entity.Item dropItem(Location loc, ItemStack item) {
		Validate.notNull(item, "Cannot drop a Null item.");
		Validate.isTrue(item.getTypeId() != 0, "Cannot drop AIR.");
		EntityItem entity = new ItemEntity(world.getHandle(), loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
		entity.pickupDelay = 10;
		world.getHandle().addEntity(entity);
		return new CraftItem(world.getHandle().getServer(), entity);
	}

	private static void randomLocationWithinBlock(Location loc, double xs, double ys, double zs) {
		double prevX = loc.getX();
		double prevY = loc.getY();
		double prevZ = loc.getZ();
		loc.add(xs, ys, zs);
		if (loc.getX() < Math.floor(prevX)) {
			loc.setX(Math.floor(prevX));
		}
		if (loc.getX() >= Math.ceil(prevX)) {
			loc.setX(Math.ceil(prevX - 0.01));
		}
		if (loc.getY() < Math.floor(prevY)) {
			loc.setY(Math.floor(prevY));
		}
		if (loc.getY() >= Math.ceil(prevY)) {
			loc.setY(Math.ceil(prevY - 0.01));
		}
		if (loc.getZ() < Math.floor(prevZ)) {
			loc.setZ(Math.floor(prevZ));
		}
		if (loc.getZ() >= Math.ceil(prevZ)) {
			loc.setZ(Math.ceil(prevZ - 0.01));
		}
	}

	public org.bukkit.entity.Item dropItemNaturally(Location loc, ItemStack item) {
		double xs = world.getHandle().random.nextFloat() * 0.7F - 0.35D;
		double ys = world.getHandle().random.nextFloat() * 0.7F - 0.35D;
		double zs = world.getHandle().random.nextFloat() * 0.7F - 0.35D;
		loc = loc.clone();
		// Makes sure the new item is created within the block the location points to.
		// This prevents item spill in 1-block wide farms.
		randomLocationWithinBlock(loc, xs, ys, zs);
		return dropItem(loc, item);
	}

	public Arrow spawnArrow(Location loc, Vector velocity, float speed, float spread) {
		Validate.notNull(loc, "Can not spawn arrow with a null location");
		Validate.notNull(velocity, "Can not spawn arrow with a null velocity");

		EntityArrow arrow = new ArrowEntity(world.getHandle());
		arrow.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		arrow.shoot(velocity.getX(), velocity.getY(), velocity.getZ(), speed, spread);
		world.getHandle().addEntity(arrow);
		return (Arrow) arrow.getBukkitEntity();
	}

	@Deprecated
	public LivingEntity spawnCreature(Location loc, CreatureType creatureType) {
		return spawnCreature(loc, creatureType.toEntityType());
	}

	@Deprecated
	public LivingEntity spawnCreature(Location loc, EntityType creatureType) {
		Validate.isTrue(creatureType.isAlive(), "EntityType not instance of LivingEntity");
		return (LivingEntity) spawnEntity(loc, creatureType);
	}

	public Entity spawnEntity(Location loc, EntityType entityType) {
		return spawn(loc, entityType.getEntityClass());
	}

	public LightningStrike strikeLightning(Location loc) {
		EntityLightning lightning = new EntityLightning(world.getHandle(), loc.getX(), loc.getY(), loc.getZ());
		world.getHandle().strikeLightning(lightning);
		return new CraftLightningStrike(world.getHandle().getServer(), lightning);
	}

	public LightningStrike strikeLightningEffect(Location loc) {
		EntityLightning lightning = new EntityLightning(world.getHandle(), loc.getX(), loc.getY(), loc.getZ(), true);
		world.getHandle().strikeLightning(lightning);
		return new CraftLightningStrike(world.getHandle().getServer(), lightning);
	}

	public boolean generateTree(Location loc, TreeType type) {
		return world.generateTree(loc, type);
	}

	public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
		return world.generateTree(loc, type, delegate);
	}

	public TileEntity getTileEntityAt(final int x, final int y, final int z) {
		return world.getTileEntityAt(x,y,z);
	}

	public String getName() {
		return world.getHandle().worldData.getName();
	}

	@Deprecated
	public long getId() {
		return world.getHandle().worldData.getSeed();
	}

	public UUID getUID() {
		return world.getHandle().getDataManager().getUUID();
	}

	@Override
	public String toString() {
		return "CraftWorld{name=" + getName() + '}';
	}

	public long getTime() {
		long time = getFullTime() % 24000;
		if (time < 0) time += 24000;
		return time;
	}

	public void setTime(long time) {
		long margin = (time - getFullTime()) % 24000;
		if (margin < 0) margin += 24000;
		setFullTime(getFullTime() + margin);
	}

	public long getFullTime() {
		return world.getHandle().getDayTime();
	}

	public void setFullTime(long time) {
		world.getHandle().setDayTime(time);

		// Forces the client to update to the new time immediately
		for (Player p : getPlayers()) {
			CraftPlayer cp = (CraftPlayer) p;
			if (cp.getHandle().playerConnection == null) continue;

			cp.getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateTime(cp.getHandle().world.getTime(), cp.getHandle().getPlayerTime(), cp.getHandle().world.getGameRules().getBoolean("doDaylightCycle")));
		}
	}

	public boolean createExplosion(double x, double y, double z, float power) {
		return createExplosion(x, y, z, power, false, true);
	}

	public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
		return createExplosion(x, y, z, power, setFire, true);
	}

	public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
		return !world.getHandle().createExplosion(null, x, y, z, power, setFire, breakBlocks).wasCanceled;
	}

	public boolean createExplosion(Location loc, float power) {
		return createExplosion(loc, power, false);
	}

	public boolean createExplosion(Location loc, float power, boolean setFire) {
		return createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire);
	}

	public Environment getEnvironment() {
		return world.getEnvironment();
	}

	public void setEnvironment(Environment env) {
		world.setEnvironment(env);
	}

	public Block getBlockAt(Location location) {
		return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public int getBlockTypeIdAt(Location location) {
		return getBlockTypeIdAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public int getHighestBlockYAt(Location location) {
		return getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
	}

	public Chunk getChunkAt(Location location) {
		return getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
	}

	public ChunkGenerator getGenerator() {
		return world.getGenerator();
	}

	public List<BlockPopulator> getPopulators() {
		return world.getPopulators();
	}

	public Block getHighestBlockAt(int x, int z) {
		return getBlockAt(x, getHighestBlockYAt(x, z), z);
	}

	public Block getHighestBlockAt(Location location) {
		return getHighestBlockAt(location.getBlockX(), location.getBlockZ());
	}

	public Biome getBiome(int x, int z) {
		return CraftBlock.biomeBaseToBiome(this.world.getHandle().getBiome(new BlockPosition(x, 0, z)));
	}

	public void setBiome(int x, int z, Biome bio) {
		world.setBiome(x, z, bio);
	}

	public double getTemperature(int x, int z) {
		return this.world.getHandle().getBiome(new BlockPosition(x, 0, z)).temperature;
	}

	public double getHumidity(int x, int z) {
		return this.world.getHandle().getBiome(new BlockPosition(x, 0, z)).humidity;
	}

	public List<Entity> getEntities() {
		return world.getEntities();
	}

	public List<LivingEntity> getLivingEntities() {
		return world.getLivingEntities();
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
		return (Collection<T>)getEntitiesByClasses(classes);
	}

	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> clazz) {
		return world.getEntitiesByClass(clazz);
	}

	public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
		return world.getEntitiesByClasses(classes);
	}

	@Override
	public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
		return world.getNearbyEntities(location, x, y, z);
	}

	public List<Player> getPlayers() {
		return world.getPlayers();
	}

	public void save() {
		world.save();
	}

	public boolean isAutoSave() {
		return world.isAutoSave();
	}

	public void setAutoSave(boolean value) {
		world.setAutoSave(value);
	}

	public void setDifficulty(Difficulty difficulty) {
		world.getHandle().worldData.setDifficulty(EnumDifficulty.getById(difficulty.getValue()));
	}

	public Difficulty getDifficulty() {
		return Difficulty.getByValue(world.getHandle().getDifficulty().ordinal());
	}

	public BlockMetadataStore getBlockMetadata() {
		return world.getBlockMetadata();
	}

	public boolean hasStorm() {
		return world.getHandle().worldData.hasStorm();
	}

	public void setStorm(boolean hasStorm) {
		world.getHandle().worldData.setStorm(hasStorm);
	}

	public int getWeatherDuration() {
		return world.getHandle().worldData.getWeatherDuration();
	}

	public void setWeatherDuration(int duration) {
		world.getHandle().worldData.setWeatherDuration(duration);
	}

	public boolean isThundering() {
		return world.getHandle().worldData.isThundering();
	}

	public void setThundering(boolean thundering) {
		world.getHandle().worldData.setThundering(thundering);
	}

	public int getThunderDuration() {
		return world.getHandle().worldData.getThunderDuration();
	}

	public void setThunderDuration(int duration) {
		world.getHandle().worldData.setThunderDuration(duration);
	}

	public long getSeed() {
		return world.getHandle().worldData.getSeed();
	}

	public boolean getPVP() {
		return world.getHandle().pvpMode;
	}

	public void setPVP(boolean pvp) {
		world.getHandle().pvpMode = pvp;
	}

	public void playEffect(Player player, Effect effect, int data) {
		playEffect(player.getLocation(), effect, data, 0);
	}

	public void playEffect(Location location, Effect effect, int data) {
		playEffect(location, effect, data, 64);
	}

	public <T> void playEffect(Location loc, Effect effect, T data) {
		playEffect(loc, effect, data, 64);
	}

	public <T> void playEffect(Location loc, Effect effect, T data, int radius) {
		if (data != null) {
			Validate.isTrue(data.getClass().isAssignableFrom(effect.getData()), "Wrong kind of data for this effect!");
		} else {
			Validate.isTrue(effect.getData() == null, "Wrong kind of data for this effect!");
		}

		int datavalue = data == null ? 0 : CraftEffect.getDataValue(effect, data);
		playEffect(loc, effect, datavalue, radius);
	}

	public void playEffect(Location location, Effect effect, int data, int radius) {
		Validate.notNull(location, "Location cannot be null");
		Validate.notNull(effect, "Effect cannot be null");
		Validate.notNull(location.getWorld(), "World cannot be null");
		int packetData = effect.getId();
		PacketPlayOutWorldEvent packet = new PacketPlayOutWorldEvent(packetData, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), data, false);
		int distance;
		radius *= radius;

		for (Player player : getPlayers()) {
			if (((CraftPlayer) player).getHandle().playerConnection == null) continue;
			if (!location.getWorld().equals(player.getWorld())) continue;

			distance = (int) player.getLocation().distanceSquared(location);
			if (distance <= radius) {
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}

	public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
		return spawn(location, clazz, SpawnReason.CUSTOM);
	}

	public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException {
		return spawnFallingBlock(location, org.bukkit.Material.getMaterial(blockId), blockData);
	}

	

	@SuppressWarnings("unchecked")
	public <T extends Entity> T addEntity(net.minecraft.server.v1_8_R3.Entity entity, SpawnReason reason) throws IllegalArgumentException {
		Preconditions.checkArgument(entity != null, "Cannot spawn null entity");

		if (entity instanceof EntityInsentient) {
			((EntityInsentient) entity).prepare(getHandle().E(new BlockPosition(entity)), (GroupDataEntity) null);
		}

		world.getHandle().addEntity(entity, reason);
		return (T) entity.getBukkitEntity();
	}

	public <T extends Entity> T spawn(Location location, Class<T> clazz, SpawnReason reason) throws IllegalArgumentException {
		net.minecraft.server.v1_8_R3.Entity entity = createEntity(location, clazz);

		return addEntity(entity, reason);
	}

	public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
		return world.getEmptyChunkSnapshot(x, z, includeBiome, includeBiomeTempRain);
	}

	public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
		world.setSpawnFlags(allowMonsters, allowAnimals);
	}

	public boolean getAllowAnimals() {
		return world.getAllowAnimals();
	}

	public boolean getAllowMonsters() {
		return world.getAllowMonsters();
	}

	public int getMaxHeight() {
		return world.getMaxHeight();
	}

	public int getSeaLevel() {
		return world.getSeaLevel();
	}

	public boolean getKeepSpawnInMemory() {
		return world.getKeepSpawnInMemory();
	}

	public void setKeepSpawnInMemory(boolean keepLoaded) {
		world.setKeepSpawnInMemory(keepLoaded);
	}

	@Override
	public int hashCode() {
		return world.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return world.equals(obj);
	}

	public File getWorldFolder() {
		return world.getWorldFolder();
	}

	public void sendPluginMessage(Plugin source, String channel, byte[] message) {
		world.sendPluginMessage(source, channel, message);
	}

	public Set<String> getListeningPluginChannels() {
		return world.getListeningPluginChannels();
	}

	public org.bukkit.WorldType getWorldType() {
		return world.getWorldType();
	}

	public boolean canGenerateStructures() {
		return world.canGenerateStructures();
	}

	public long getTicksPerAnimalSpawns() {
		return world.getTicksPerAnimalSpawns();
	}

	public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
		world.setTicksPerAnimalSpawns(ticksPerAnimalSpawns);
	}

	public long getTicksPerMonsterSpawns() {
		return world.getTicksPerMonsterSpawns();
	}

	public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
		world.setTicksPerMonsterSpawns(ticksPerMonsterSpawns);
	}

	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		world.setMetadata(metadataKey, newMetadataValue);
	}

	public List<MetadataValue> getMetadata(String metadataKey) {
		return world.getMetadata(metadataKey);
	}

	public boolean hasMetadata(String metadataKey) {
		return world.hasMetadata(metadataKey);
	}

	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		world.removeMetadata(metadataKey, owningPlugin);
	}

	public int getMonsterSpawnLimit() {
		return world.getMonsterSpawnLimit();
	}

	public void setMonsterSpawnLimit(int limit) {
		world.setMonsterSpawnLimit(limit);
	}

	public int getAnimalSpawnLimit() {
		return world.getAnimalSpawnLimit();
	}

	public void setAnimalSpawnLimit(int limit) {
		world.setAnimalSpawnLimit(limit);
	}

	public int getWaterAnimalSpawnLimit() {
		return world.getWaterAnimalSpawnLimit();
	}

	public void setWaterAnimalSpawnLimit(int limit) {
		world.setWaterAnimalSpawnLimit(limit);
	}

	public int getAmbientSpawnLimit() {
		return world.getAmbientSpawnLimit();
	}

	public void setAmbientSpawnLimit(int limit) {
		world.setAmbientSpawnLimit(limit);
	}


	public void playSound(Location loc, Sound sound, float volume, float pitch) {
		world.playSound(loc, sound, volume, pitch);
	}

	public String getGameRuleValue(String rule) {
		return world.getGameRuleValue(rule);
	}

	public boolean setGameRuleValue(String rule, String value) {
		return world.setGameRuleValue(rule, value);
	}

	public String[] getGameRules() {
		return world.getGameRules();
	}

	public boolean isGameRule(String rule) {
		return world.isGameRule(rule);
	}

	@Override
	public WorldBorder getWorldBorder() {
		return world.getWorldBorder();
	}

	public void processChunkGC() {
		world.processChunkGC();
	}
}
