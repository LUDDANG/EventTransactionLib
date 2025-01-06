package live.luya.eventtransactionlib.forge.util;

import live.luya.eventtransactionlib.data.EventBlockPosition;
import live.luya.eventtransactionlib.data.EventPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class EventTransactionUtil {
	public static EventPosition asPosition(String world, Vec3 pos) {
		return new EventPosition(world, pos.x, pos.y, pos.z);
	}

	public static EventBlockPosition asBlockPosition(String world, Vec3 pos) {
		return new EventBlockPosition(world, (int) pos.x, (int) pos.y, (int) pos.z);
	}

	public static EventBlockPosition asBlockPosition(String world, BlockPos pos) {
		return new EventBlockPosition(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vec3 asVec3(EventPosition pos) {
		return new Vec3(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vec3 asVec3(EventBlockPosition pos) {
		return new Vec3(pos.getX(), pos.getY(), pos.getZ());
	}
}
