package live.luya.eventtransactionlib;

import java.util.function.Consumer;

public interface EventTransactionApi {

	static EventTransactionApi getApi() {
		return EventTransactionApiProvider.getApi();
	}

	<T> UnregisterHandler registerListener(Class<T> eventClass, Consumer<T> listener);

	/**
	 * Register listener.
	 * All {@link live.luya.eventtransactionlib.annotation.EventTransaction} annotated methods will be registered.
	 *
	 * @param listener Target event listener
	 * @return Unregister handler
	 */
	UnregisterHandler registerListener(EventTransactionListener listener);

	/**
	 * Unregister listener.
	 * Does not work with envor
	 *
	 * @param handler Handler object.
	 */
	void unregisterListener(UnregisterHandler handler);

	/**
	 * Trigger event handler.
	 *
	 * @param event Event object.
	 */
	void triggerHandler(Object event);

	/**
	 * Register via specific environment handler.
	 * **Warning** This handler DO NOT work with EventTransactionAPI event,
	 * just provide register for specific environment handler.
	 *
	 * @param handler  Environment handler
	 * @param listener Target event listener
	 */
	void registerEnvironmentHandler(String handler, EventTransactionListener listener);

	/**
	 * Prepare registration until requested order is matched.
	 * If order is already matched, then it will be executed immediately.
	 *
	 * @param order       Registration order
	 * @param apiConsumer API consumer
	 * -- Deprecated --
	 * Use {@link #prepareRegistration(RegistrationOrder, RegistrationOrder, Consumer)} instead.
	 */
	@Deprecated
	void prepareRegistration(RegistrationOrder order, Consumer<EventTransactionApi> apiConsumer);


	/**
	 * Prepare registration until requested order is matched.
	 *
	 * @param order Registration order
	 * @param targetPlatform Target platform
	 * @param apiConsumer API consumer
	 */
	void prepareRegistration(RegistrationOrder order, RegistrationOrder targetPlatform, Consumer<EventTransactionApi> apiConsumer);

	/**
	 * Stack registration order, and executes it when order is matched.
	 * This is internal API, so do not use unless you have to use it.
	 *
	 * @param order Registration order
	 */
	@Deprecated
	void stackRegistrationOrder(RegistrationOrder order);
}
