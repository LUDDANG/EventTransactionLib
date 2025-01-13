package live.luya.eventtransactionlib;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class EventTransactionApiProvider {
	private static final Map<RegistrationOrder, EventTransactionApi> apiMap = new HashMap<>();

	private static final Set<RegistrationOrder> currentOrder = new LinkedHashSet<>();


	public static EventTransactionApi getApi() {
		return apiMap.values().stream().findFirst().orElse(null);
	}

	public static EventTransactionApi getApi(RegistrationOrder order) {
		return apiMap.get(order);
	}

	public static void appendApi(RegistrationOrder order, EventTransactionApi api) {
		api.registerOrderProvider(() -> currentOrder);
		api.registerClassLoaderProvider(() -> apiMap.values().stream().map(EventTransactionApi::getPlatformClassLoader).toList());
		apiMap.put(order, api);
	}

	@Deprecated
	public static void stackRegistrationOrder(RegistrationOrder registrationOrder) {
		currentOrder.add(registrationOrder);
		for (EventTransactionApi api : apiMap.values()) {
			api.onOrderStacked();
		}
	}
}
