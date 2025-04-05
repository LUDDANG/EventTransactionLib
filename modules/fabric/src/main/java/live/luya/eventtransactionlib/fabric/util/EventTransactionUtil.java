package live.luya.eventtransactionlib.fabric.util;

import live.luya.eventtransactionlib.data.EventBlockPosition;
import live.luya.eventtransactionlib.data.EventPosition;
import live.luya.eventtransactionlib.fabric.data.WorldRef;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EventTransactionUtil {

	private static MinecraftServer server = null;

	public static void setServer(MinecraftServer server) {
		EventTransactionUtil.server = server;
		System.out.println("[EventTransactionLib-Fabric] MinecraftServer instance bound to utility");
	}

	public static EventPosition asPosition(String world, Vec3d pos) {
		return new EventPosition(world, pos.x, pos.y, pos.z);
	}

	public static EventBlockPosition asBlockPosition(String world, Vec3d pos) {
		return new EventBlockPosition(world, (int) pos.x, (int) pos.y, (int) pos.z);
	}

	public static EventBlockPosition asBlockPosition(String world, BlockPos pos) {
		return new EventBlockPosition(world, pos.getX(), pos.getY(), pos.getZ());
	}

	public static EventBlockPosition asBlockPosition(World level, BlockPos pos) {
		return asBlockPosition(level.getRegistryKey().toString(), pos);
	}

	public static EventBlockPosition asWorldlessBlockPosition(Vec3d pos) {
		return new EventBlockPosition("", (int) pos.x, (int) pos.y, (int) pos.z);
	}

	public static EventPosition asWorldlessPosition(Vec3d pos) {
		return new EventPosition("", pos.x, pos.y, pos.z);
	}

	public static Vec3d asVec3(EventPosition pos) {
		return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
	}

	public static Vec3d asVec3(EventBlockPosition pos) {
		return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
	}

	public static BlockPos asBlockPos(EventBlockPosition pos) {
		return new BlockPos(pos.getX(), pos.getY(), pos.getZ());
	}

	public static WorldRef<BlockPos> asReferencedBlockPos(EventBlockPosition pos) {
		testServer();
		return new WorldRef<>(server.getWorld(
				RegistryKey.of(RegistryKeys.WORLD, Identifier.of(pos.getWorld()))
		), new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
	}

	public static WorldRef<Vec3d> asReferencedVec3(EventPosition pos) {
		testServer();
		return new WorldRef<>(server.getWorld(
				RegistryKey.of(RegistryKeys.WORLD, Identifier.of(pos.getWorld()))
		), new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	private static void testServer() {
		if (server == null) {
			throw new IllegalStateException("Fabric server is not initialize yet");
		}
	}
}
