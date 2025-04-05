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

import java.util.Objects;
import java.util.UUID;

public class EventTransactionUtil {
	public static EventBlockPosition asBlockPosition(World w, int x, int y, int z) {
		return new EventBlockPosition(w.getKey().toString(), x, y, z);
	}

	public static EventPosition asPosition(World w, double x, double y, double z) {
		return new EventPosition(w.getKey().toString(), x, y, z);
	}

	public static EventPosition asWorldlessPosition(double x, double y, double z) {
		return new EventPosition("", x, y, z);
	}

	public static EventBlockPosition asWorldlessBlockPosition(int x, int y, int z) {
		return new EventBlockPosition("", x, y, z);
	}

	public static EventBlockPosition asBlockPosition(Location loc) {
		return new EventBlockPosition(loc.getWorld().getKey().toString(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public static EventPosition asPosition(Location loc) {
		return new EventPosition(loc.getWorld().getKey().toString(), loc.getX(), loc.getY(), loc.getZ());
	}

	public static Location toLocation(EventPosition pos) {
		return new Location(BukkitWorldUtil.findWorldBy(pos.getWorld()), pos.getX(), pos.getY(), pos.getZ());
	}

	public static Location toLocation(EventBlockPosition pos) {
		return new Location(BukkitWorldUtil.findWorldBy(pos.getWorld()), pos.getX(), pos.getY(), pos.getZ());
	}

	public static Location toLocation(World w, EventPosition pos) {
		return new Location(w, pos.getX(), pos.getY(), pos.getZ());
	}

	public static Location toLocation(World w, EventBlockPosition pos) {
		return new Location(w, pos.getX(), pos.getY(), pos.getZ());
	}

	public static Player getPlayer(BaseTransaction<UUID> transaction) {
		return Bukkit.getPlayer(transaction.getBase());
	}

	public static Entity getEntity(BaseTransaction<UUID> transaction) {
		return Bukkit.getEntity(transaction.getBase());
	}

	public static Block getBlock(EventBlockPosition pos) {
		return Objects.requireNonNull(
				BukkitWorldUtil.findWorldBy(pos.getWorld())
		).getBlockAt(pos.getX(), pos.getY(), pos.getZ());
	}
}
