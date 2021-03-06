package net.tangentmc.nmsUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import net.tangentmc.nmsUtils.entities.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.tangentmc.nmsUtils.entities.HologramFactory.HologramObject;

public interface NMSUtil {
	void trackWorldEntities(World w);
	void untrackWorldEntities(World w);

    List<Player> getStolenControls();

    NMSHologram spawnHologram(Location loc, ArrayList<HologramObject> lines);
	boolean teleportFast(Entity entity, Location location, org.bukkit.util.Vector velocity);

	void stealPlayerControls(Location loc, Player who);
	void playMidi(Player to, boolean repeat, File midi) throws MidiUnavailableException, InvalidMidiDataException, IOException;
	void playMidiNear(Location near, double area, boolean repeat, File midi) throws MidiUnavailableException, InvalidMidiDataException, IOException;
	NPC spawnNPC(String name, Location location, String value, String signature);
	NPC spawnNPC(String name, Location location);
	NMSEntity getNMSEntity(Entity en);
}
