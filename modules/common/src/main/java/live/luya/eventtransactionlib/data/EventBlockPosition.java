package live.luya.eventtransactionlib.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class EventBlockPosition {
	private final String world;

	private final int x;

	private final int y;

	private final int z;

	public EventPosition asPosition() {
		return new EventPosition(world, x, y, z);
	}

	public boolean isWorldless() {
		return world.isEmpty();
	}
}
