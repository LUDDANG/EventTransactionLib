package live.luya.eventtransactionlib.internal;

import live.luya.eventtransactionlib.EventTransactionApi;
import live.luya.eventtransactionlib.EventTransactionListener;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.UnregisterHandler;
import live.luya.eventtransactionlib.annotation.EventTransaction;
import live.luya.eventtransactionlib.annotation.TransactionExclude;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class EventTransactionApiImpl implements EventTransactionApi {

	private final Map<RegistrationOrder, List<Consumer<EventTransactionApi>>> registrationOrderMap = new HashMap<>();

	private final Map<String, RegisteredClassData> eventConsumer = new HashMap<>();

	private Supplier<Set<RegistrationOrder>> orderProvider = null;

	@Getter
	private Supplier<List<ClassLoader>> classLoaderSupplier;

	@Override
	public void registerClassLoaderProvider(Supplier<List<ClassLoader>> provider) {
		this.classLoaderSupplier = provider;
	}

	@Override
	public void registerOrderProvider(Supplier<Set<RegistrationOrder>> provider) {
		this.orderProvider = provider;
	}

	@Override
	public <T> UnregisterHandler registerListener(Class<T> eventClass, Consumer<T> listener) {
		System.out.println("[EventTransactionLib] Registering listener for " + eventClass.getName());
		return new UnregisterHandler(eventConsumer.computeIfAbsent(eventClass.getName(), k -> new RegisteredClassData(eventClass))
				.addConsumer((Consumer<Object>) listener));
	}

	@Override
	public UnregisterHandler registerListener(EventTransactionListener listener) {
		attachExternalClassLoaders(classLoaderSupplier.get());
		List<Runnable> handlers = new ArrayList<>();

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
		detachExternalClassLoaders(classLoaderSupplier.get());
		return new UnregisterHandler(() -> {
			for (Runnable handler : handlers) {
				handler.run();
			}
		});
	}

	public String getTransactionIncompatibleCause(Class<?> cls) {
		if (cls.isRecord()) {
			if (cls.getConstructors()[0].getParameterCount() != cls.getDeclaredFields().length) {
				return "Transaction-Compatible record class does not allow any non-constructor fields.";
			}
			return null;
		}
		if (cls.getConstructors().length == 0) {
			return "Transaction-Compatible class must have at least one constructor.";
		}
		if (Arrays.stream(cls.getConstructors()).noneMatch(it -> it.getParameterCount() == 0)) {
			return "Transaction-Compatible class must have at least one empty constructor.";
		}
		return null;
	}

	@Override
	public void unregisterListener(UnregisterHandler handler) {
		handler.run();
	}

	@Override
	public <T> T triggerHandler(T event) {
		if (eventConsumer.containsKey(event.getClass().getName())) {
			return (T) eventConsumer.get(event.getClass().getName()).invokeEvent(event);
		}
		return event;
	}

	@Override
	public void registerEnvironmentHandler(String handler, EventTransactionListener listener) {

	}

	@Override
	public void prepareRegistration(RegistrationOrder order, Consumer<EventTransactionApi> apiConsumer) {
		if (order.doesPassed(orderProvider.get())) {
			apiConsumer.accept(this);
		} else {
			registrationOrderMap.computeIfAbsent(order, k -> new ArrayList<>()).add(apiConsumer);
		}
	}

	@Override
	@Deprecated
	public void prepareRegistration(RegistrationOrder order, RegistrationOrder targetPlatform, Consumer<EventTransactionApi> apiConsumer) {
		if (order.doesPassed(orderProvider.get())) {
			apiConsumer.accept(targetPlatform.getPlatformApi());
		} else {
			registrationOrderMap.computeIfAbsent(order, k -> new ArrayList<>()).add(api -> {
				apiConsumer.accept(targetPlatform.getPlatformApi());
			});
		}
	}

	@Override
	public void onOrderStacked() {
		System.out.println("[EventTransactionLib] Registration order stacked: " +
				orderProvider.get().stream().map(RegistrationOrder::name).collect(Collectors.joining(" -> ")));
		Iterator<Map.Entry<RegistrationOrder, List<Consumer<EventTransactionApi>>>> iterator = registrationOrderMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<RegistrationOrder, List<Consumer<EventTransactionApi>>> entry = iterator.next();
			if (entry.getKey().doesPassed(orderProvider.get())) {
				for (Consumer<EventTransactionApi> consumer : entry.getValue()) {
					consumer.accept(this);
				}
				iterator.remove();
			}
		}
	}

	private static class RegisteredClassData {
		private final Class<?> origin;
		private final List<Consumer<Object>> consumers = new ArrayList<>();

		public RegisteredClassData(Class<?> origin) {
			this.origin = origin;
		}

		public Runnable addConsumer(Consumer<Object> consumer) {
			consumers.add(consumer);
			System.out.println("[EventTransactionLib] Registered " + origin.getName() + " with " + consumers.size() + " consumers.");
			return () -> consumers.remove(consumer);
		}

		public Object reassemble(Object target) {
			try {
				if (origin.isRecord()) {
					// Assembling record class
					Parameter[] parameters = origin.getConstructors()[0].getParameters();
					Field[] fields = target.getClass().getDeclaredFields();
					Object[] objects = new Object[parameters.length];
					for (int i = 0; i < parameters.length; i++) {
						fields[i].setAccessible(true);
						objects[i] = fields[i].get(target);
					}
					return origin.getConstructors()[0].newInstance(objects);
				} else {
					// Assembling normal class
					Object newObject = origin.getConstructors()[0].newInstance();
					Field[] originFields = origin.getDeclaredFields();
					Field[] targetFields = target.getClass().getDeclaredFields();
					for (int i = 0; i < originFields.length; i++) {
						if (originFields[i].getAnnotation(TransactionExclude.class) != null) continue;
						if (Modifier.isFinal(originFields[i].getModifiers())) continue;
						originFields[i].setAccessible(true);
						targetFields[i].setAccessible(true);
						originFields[i].set(newObject, targetFields[i].get(target));
					}
					return newObject;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Object deassemble(Object assembled, Class<?> revertTo) {
			try {
				if (origin.isRecord()) {
					// Deassembling record class
					Field[] fields = origin.getDeclaredFields();
					Object[] objects = new Object[fields.length];
					for (int i = 0; i < fields.length; i++) {
						fields[i].setAccessible(true);
						objects[i] = fields[i].get(assembled);
					}
					return revertTo.getConstructors()[0].newInstance(objects);
				} else {
					// Deassembling normal class
					Object newObject = revertTo.getConstructors()[0].newInstance();
					Field[] originFields = revertTo.getDeclaredFields();
					Field[] targetFields = assembled.getClass().getDeclaredFields();
					for (int i = 0; i < originFields.length; i++) {
						if (originFields[i].getAnnotation(TransactionExclude.class) != null) continue;
						if (Modifier.isFinal(originFields[i].getModifiers())) continue;
						originFields[i].setAccessible(true);
						targetFields[i].setAccessible(true);
						originFields[i].set(newObject, targetFields[i].get(assembled));
					}
					return newObject;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Object invokeEvent(Object event) {
//			Object target = reassemble(event);
			for (Consumer<Object> consumer : consumers) {
				consumer.accept(event);
			}
//			return deassemble(target, event.getClass());
			return event;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			RegisteredClassData that = (RegisteredClassData) o;
			return Objects.equals(origin, that.origin);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(origin);
		}

		public int count() {
			return consumers.size();
		}
	}

}
