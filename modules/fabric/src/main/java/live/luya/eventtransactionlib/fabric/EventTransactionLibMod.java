package live.luya.eventtransactionlib.fabric;


import live.luya.eventtransactionlib.EventTransactionApiProvider;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.fabric.impl.EventTransactionFabricImpl;
import live.luya.eventtransactionlib.fabric.util.EventTransactionUtil;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class EventTransactionLibMod implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		EventTransactionFabricImpl impl = new EventTransactionFabricImpl();
		EventTransactionApiProvider.appendApi(RegistrationOrder.FABRIC, impl);
		EventTransactionApiProvider.stackRegistrationOrder(RegistrationOrder.FABRIC);

		System.out.println("EventTransactionLib has been registered.");
		EventTransactionApiProvider.getApi()
				.prepareRegistration(RegistrationOrder.HYBRID_MOD_BUKKIT, RegistrationOrder.FABRIC, (api) -> {
					impl.attachExternalClassLoaders(impl.getClassLoaderSupplier().get());
				});
		ServerLifecycleEvents.SERVER_STARTED.register(EventTransactionUtil::setServer);
	}
}
