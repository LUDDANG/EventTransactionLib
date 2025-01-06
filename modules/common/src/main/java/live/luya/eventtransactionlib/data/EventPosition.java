package live.luya.eventtransactionlib.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class EventPosition {
	private final String world;

	private final double x;

	private final double y;

	private final double z;

	public EventBlockPosition asBlockPosition() {
		return new EventBlockPosition(world, (int) x, (int) y, (int) z);
	}
}
