package live.luya.eventtransactionlib;

import java.util.HashMap;
import java.util.Map;

public class EventTransactionApiProvider {
	private static Map<RegistrationOrder, EventTransactionApi> apiMap = new HashMap<>();

	public static EventTransactionApi getApi() {
		return apiMap.values().stream().findFirst().orElse(null);
	}

	public static EventTransactionApi getApi(RegistrationOrder order) {
		return apiMap.get(order);
	}

	public static void appendApi(RegistrationOrder order, EventTransactionApi api) {
		System.out.println("[EventTransactionLib] Registered new EventTransaction API to platform " + order.name() + ". (" + api.getClass()
				.getName() + ")");
		apiMap.put(order, api);
	}
}
