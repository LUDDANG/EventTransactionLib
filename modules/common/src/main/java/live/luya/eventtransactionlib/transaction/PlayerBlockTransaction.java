package live.luya.eventtransactionlib.transaction;

import live.luya.eventtransactionlib.data.EventBlockPosition;

public interface PlayerBlockTransaction extends PlayerTransaction {
	EventBlockPosition getBlockPosition();
}
