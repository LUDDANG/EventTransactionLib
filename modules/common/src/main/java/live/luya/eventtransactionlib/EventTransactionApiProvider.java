package live.luya.eventtransactionlib;

import java.util.*;
import java.util.function.Consumer;

public class EventTransactionApiProvider {
	private static final Map<RegistrationOrder, EventTransactionApi> apiMap = new HashMap<>();

	private static final Set<RegistrationOrder> currentOrder = new LinkedHashSet<>();

	private static EventTransactionApi lastApi;


	public static EventTransactionApi getApi() {
		return lastApi;
	}

	public static EventTransactionApi getApi(RegistrationOrder order) {
		return apiMap.get(order);
	}

	public static void appendApi(RegistrationOrder order, EventTransactionApi api) {
		api.registerOrderProvider(() -> currentOrder);
		api.registerClassLoaderProvider(() -> apiMap.values().stream().map(EventTransactionApi::getPlatformClassLoader).flatMap(Collection::stream).toList());
		apiMap.put(order, api);
		if (lastApi == null)
			lastApi = api;
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

	public static void prepareRegistration(RegistrationOrder order, Consumer<EventTransactionApi> apiConsumer) {
		getApi().prepareRegistration(order, apiConsumer);
	}
}
