package live.luya.eventtransactionlib;

public class EventTransactionApiProvider {
	private static EventTransactionApi api;

	public static EventTransactionApi getApi() {
		return api;
	}

	public static void setApi(EventTransactionApi api) {
		if (EventTransactionApiProvider.api != null) {
			System.err.println("[EventTransactionLib] API is already set. Ignoring new API.");
			return;
		}
		System.out.println("[EventTransactionLib] Registered new EventTransaction API. (" + api.getClass().getName() + ")");
		EventTransactionApiProvider.api = api;
	}
}
