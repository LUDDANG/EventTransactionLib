package live.luya.eventtransactionlib.neoforge;

import live.luya.eventtransactionlib.EventTransactionApiProvider;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.neoforge.impl.EventTransactionApiNeoForgeImpl;
import net.neoforged.fml.common.Mod;

@Mod(EventTransactionLibMod.MODID)
public class EventTransactionLibMod {
	public static final String MODID = "event_transaction_lib";


	public EventTransactionLibMod() {
		EventTransactionApiNeoForgeImpl impl = new EventTransactionApiNeoForgeImpl();
		EventTransactionApiProvider.appendApi(RegistrationOrder.FORGE, impl);
		EventTransactionApiProvider.stackRegistrationOrder(RegistrationOrder.FORGE);

		System.out.println("EventTransactionLib has been registered.");
		EventTransactionApiProvider.getApi()
				.prepareRegistration(RegistrationOrder.HYBRID_MOD_BUKKIT, RegistrationOrder.FORGE, (api) -> {
					impl.attachExternalClassLoaders(impl.getClassLoaderSupplier().get());
				});
	}
}
