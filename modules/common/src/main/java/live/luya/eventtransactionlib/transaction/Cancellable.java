package live.luya.eventtransactionlib.transaction;

public interface Cancellable {
	boolean isCancelled();

	void setCancelled(boolean cancelled);
}
