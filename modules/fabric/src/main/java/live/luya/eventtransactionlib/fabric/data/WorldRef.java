package live.luya.eventtransactionlib.fabric.data;

import net.minecraft.world.World;

public class WorldRef<T> {
	private final World world;

	private final T data;

	public WorldRef(World world, T data) {
		this.world = world;
		this.data = data;
	}

	public World getWorld() {
		return world;
	}

	public T getData() {
		return data;
	}

	public boolean isReferencePresent() {
		return world != null;
	}
}
