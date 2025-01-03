package live.luya.eventtransactionlib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum RegistrationOrder {
	BUKKIT(), FORGE(), FABRIC(), MOD_BUKKIT(Set.of(FORGE), Set.of(FABRIC)), HYBRID_MOD_BUKKIT(Set.of(FORGE, BUKKIT), Set.of(FABRIC, BUKKIT));

	private final List<Set<RegistrationOrder>> orders;

	@SafeVarargs
	RegistrationOrder(Set<RegistrationOrder>... acceptableOrders) {
		this.orders = new ArrayList<>();
		orders.addAll(Arrays.asList(acceptableOrders));
	}

	RegistrationOrder() {
		this(Set.of());
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
