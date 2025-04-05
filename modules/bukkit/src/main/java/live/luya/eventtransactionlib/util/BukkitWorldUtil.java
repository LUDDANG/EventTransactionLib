package live.luya.eventtransactionlib.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class BukkitWorldUtil {
	public static World findWorldByName(String name) {
		return Bukkit.getWorld(name);
	}

	public static World findByNamespace(String namespace) {
		for (World world : Bukkit.getWorlds()) {
			if (world.getKey().toString().equals(namespace)) {
				return world;
			}
		}
		return null;
	}

	public static World findWorldBy(String name) {
		World world = findWorldByName(name);
		if (world != null) {
			return world;
		}
		return findByNamespace(name);
	}
}
