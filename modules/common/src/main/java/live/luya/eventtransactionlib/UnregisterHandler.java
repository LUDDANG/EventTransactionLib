package live.luya.eventtransactionlib;

public class UnregisterHandler implements Runnable {
	private final Runnable handler;

	public UnregisterHandler(Runnable handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		handler.run();
	}
}
