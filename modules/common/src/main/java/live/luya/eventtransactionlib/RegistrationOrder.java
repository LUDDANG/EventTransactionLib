package live.luya.eventtransactionlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum RegistrationOrder {
	BUKKIT(List.of()), FORGE(List.of()), FABRIC(List.of()), MOD_BUKKIT(List.of(FORGE, FABRIC), Set.of(FORGE), Set.of(FABRIC)), HYBRID_MOD_BUKKIT(List.of(FORGE, FABRIC, BUKKIT), Set.of(FORGE, BUKKIT), Set.of(FABRIC, BUKKIT));

	private final List<RegistrationOrder> platformApi;

	private final List<Set<RegistrationOrder>> orders;

	@SafeVarargs
	RegistrationOrder(List<RegistrationOrder> platformApi, Set<RegistrationOrder>... acceptableOrders) {
		this.platformApi = platformApi;
		this.orders = new ArrayList<>();
		orders.addAll(Arrays.asList(acceptableOrders));
	}

	RegistrationOrder() {
		this(List.of(), Set.of());
	}

	public EventTransactionApi getPlatformApi() {
		EventTransactionApi api = EventTransactionApiProvider.getApi(this);
		if (api == null) {
			for (RegistrationOrder order : platformApi) {
				api = EventTransactionApiProvider.getApi(order);
				if (api != null) {
					return api;
				}
			}
		} else {
			return api;
		}

		throw new IllegalStateException("Platform " + this + " is not ready yet; Did the wrong platform requested, or the priority is not set correctly?");
	}

	public boolean isSingleOrder() {
		return orders.isEmpty() || (orders.size() == 1 && orders.get(0).isEmpty());
	}

	public boolean doesPassed(Set<RegistrationOrder> currentOrder) {
		if (isSingleOrder()) {
			return currentOrder.contains(this);
		}
		for (Set<RegistrationOrder> order : orders) {
			if (currentOrder.containsAll(order)) {
				return true;
			}
		}
		return false;
	}
}
