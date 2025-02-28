package live.luya.eventtransactionlib.util;

import live.luya.eventtransactionlib.data.EventBlockPosition;
import live.luya.eventtransactionlib.data.EventPosition;
import live.luya.eventtransactionlib.transaction.BaseTransaction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

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

	public static Player getPlayer(BaseTransaction<UUID> transaction) {
		return Bukkit.getPlayer(transaction.getBase());
	}

	public static Entity getEntity(BaseTransaction<UUID> transaction) {
		return Bukkit.getEntity(transaction.getBase());
	}

	public static Block getBlock(EventBlockPosition pos) {
		return Bukkit.getWorld(pos.getWorld()).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
	}
}
