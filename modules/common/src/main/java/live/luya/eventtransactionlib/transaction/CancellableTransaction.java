package live.luya.eventtransactionlib.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancellableTransaction implements Cancellable {
	private boolean cancelled = false;
}
