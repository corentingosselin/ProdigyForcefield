package fr.cocoraid.prodigyforcefield.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class UtilMath {

	public static final Random random = new Random(System.nanoTime());




	/**
	 * Un vecteur de bump (projection)
	 * @param entity
	 * @param from
	 * @param power
	 * @return
	 */
	public static Vector getBumpVector(Entity entity, Location from, double power) {
		Vector bump = entity.getLocation().toVector().subtract(from.toVector()).normalize();
		bump.multiply(power);
		return bump;
	}

	/**
	 * Projette l'entité à partir d'une location
	 * @param entity
	 * @param from projeté à partir de
	 * @param power multiplicateur de puissance
	 */
	public static void bumpEntity(Entity entity, Location from, double power) {
		entity.setVelocity(getBumpVector(entity, from, power));
	}

	/**
	 * Projette l'entité
	 * @param entity
	 * @param from
	 * @param power
	 * @param fixedY fix le Y 
	 */
	public static void bumpEntity(Entity entity, Location from, double power, double fixedY) {
		if(entity instanceof Player && entity.hasMetadata("NPC")) return;
		Vector vector = getBumpVector(entity, from, power);
		vector.setY(fixedY);
		entity.setVelocity(vector);
	}


	public static Vector getRandomVector() {
		double x, y, z;
		x = random.nextDouble() * 2 - 1;
		y = random.nextDouble() * 2 - 1;
		z = random.nextDouble() * 2 - 1;

		return new Vector(x, y, z).normalize();
	}

	/**
	 *
	 * @param location
	 * @param distance
	 * @return
	 */
	public static List<Player> getClosestPlayersFromLocation(Location location, double distance) {
		List<Player> result = new ArrayList<Player>();
		double d2 = distance * distance;
		for (Player player : location.getWorld().getPlayers()) {
			if (player.getLocation().add(0, 0.85D, 0).distanceSquared(location) <= d2) {
				result.add(player);
			}
		}
		return result;
	}

}
