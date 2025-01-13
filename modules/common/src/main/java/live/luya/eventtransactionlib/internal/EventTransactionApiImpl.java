package live.luya.eventtransactionlib.internal;

import live.luya.eventtransactionlib.EventTransactionApi;
import live.luya.eventtransactionlib.EventTransactionListener;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.UnregisterHandler;
import live.luya.eventtransactionlib.annotation.EventTransaction;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EventTransactionApiImpl implements EventTransactionApi {
	private final Set<RegistrationOrder> currentRegistrationOrder = new LinkedHashSet<>();

	private final Map<RegistrationOrder, List<Consumer<EventTransactionApi>>> registrationOrderMap = new HashMap<>();

	private final Map<Class<?>, List<Consumer<Object>>> eventConsumer = new HashMap<>();

	@Override
	public <T> UnregisterHandler registerListener(Class<T> eventClass, Consumer<T> listener) {
		eventConsumer.computeIfAbsent(eventClass, k -> new ArrayList<>()).add((Consumer<Object>) listener);
		return new UnregisterHandler(() -> eventConsumer.get(eventClass).remove(listener));
	}

	@Override
	public UnregisterHandler registerListener(EventTransactionListener listener) {
		List<UnregisterHandler> handlers = new ArrayList<>();
		for (Method method : listener.getClass().getDeclaredMethods()) {
			if (method.getAnnotation(EventTransaction.class) != null) {
				if (method.getParameterCount() == 1) {
					method.setAccessible(true);
					handlers.add(registerListener(method.getParameterTypes()[0], event -> {
						try {
							method.invoke(listener, event);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}));
				} else {
					System.err.println("[EventTransactionLib] Cannot register listener " + listener.getClass()
							.getName() + " due to invalid parameter count.");
				}
			}
		}
		return new UnregisterHandler(() -> {
			for (UnregisterHandler handler : handlers) {
				handler.run();
			}
		});
	}

	@Override
	public void unregisterListener(UnregisterHandler handler) {
		handler.run();
	}

	@Override
	public void triggerHandler(Object event) {
		if (eventConsumer.containsKey(event.getClass())) {
			for (Consumer<Object> consumer : eventConsumer.get(event.getClass())) {
				consumer.accept(event);
			}
		}
	}

	@Override
	public void registerEnvironmentHandler(String handler, EventTransactionListener listener) {

	}

	@Override
	public void prepareRegistration(RegistrationOrder order, Consumer<EventTransactionApi> apiConsumer) {
		if (order.doesPassed(currentRegistrationOrder)) {
			apiConsumer.accept(this);
		} else {
			registrationOrderMap.computeIfAbsent(order, k -> new ArrayList<>()).add(apiConsumer);
		}
	}

	@Override
	public void prepareRegistration(RegistrationOrder order, RegistrationOrder targetPlatform, Consumer<EventTransactionApi> apiConsumer) {
		if (order.doesPassed(currentRegistrationOrder)) {
			apiConsumer.accept(targetPlatform.getPlatformApi());
		} else {
			registrationOrderMap.computeIfAbsent(order, k -> new ArrayList<>()).add(api -> {
				apiConsumer.accept(targetPlatform.getPlatformApi());
			});
		}
	}

	@Override
	public void stackRegistrationOrder(RegistrationOrder order) {
		currentRegistrationOrder.add(order);
		System.out.println("[EventTransactionLib] Registration order stacked: " +
				currentRegistrationOrder.stream().map(RegistrationOrder::name).collect(Collectors.joining(" -> ")));
		Iterator<Map.Entry<RegistrationOrder, List<Consumer<EventTransactionApi>>>> iterator = registrationOrderMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<RegistrationOrder, List<Consumer<EventTransactionApi>>> entry = iterator.next();
			if (entry.getKey().doesPassed(currentRegistrationOrder)) {
				for (Consumer<EventTransactionApi> consumer : entry.getValue()) {
					consumer.accept(this);
				}
				iterator.remove();
			}
		}
	}
}
