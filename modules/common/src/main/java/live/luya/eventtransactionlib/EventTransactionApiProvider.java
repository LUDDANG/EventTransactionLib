package live.luya.eventtransactionlib;

import java.util.*;

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
		api.registerClassLoaderProvider(() -> apiMap.values().stream().map(EventTransactionApi::getPlatformClassLoader).flatMap(Collection::stream).toList());
		apiMap.put(order, api);
	}

	@Deprecated
	public static void stackRegistrationOrder(RegistrationOrder registrationOrder) {
		currentOrder.add(registrationOrder);
		for (EventTransactionApi api : apiMap.values()) {
			api.onOrderStacked();
		}
	}

	public static void triggerHandler(Object object) {
		getApi().triggerHandler(object);
	}
}
