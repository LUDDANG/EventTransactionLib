package live.luya.eventtransactionlib.util;

import live.luya.eventtransactionlib.data.EventBlockPosition;
import live.luya.eventtransactionlib.data.EventPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class EventTransactionUtil {
	public static EventBlockPosition asBlockPosition(World w, int x, int y, int z) {
		return new EventBlockPosition(w.getName(), x, y, z);
	}

	public static EventPosition asPosition(World w, double x, double y, double z) {
		return new EventPosition(w.getName(), x, y, z);
	}

	public static Location toLocation(EventPosition pos) {
		return new Location(Bukkit.getWorld(pos.getWorld()), pos.getX(), pos.getY(), pos.getZ());
	}

	public static Location toLocation(EventBlockPosition pos) {
		return new Location(Bukkit.getWorld(pos.getWorld()), pos.getX(), pos.getY(), pos.getZ());
	}
}
