package live.luya.eventtransactionlib.fabric;


import live.luya.eventtransactionlib.EventTransactionApiProvider;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.fabric.impl.EventTransactionFabricImpl;
import net.fabricmc.api.DedicatedServerModInitializer;

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

	}
}
