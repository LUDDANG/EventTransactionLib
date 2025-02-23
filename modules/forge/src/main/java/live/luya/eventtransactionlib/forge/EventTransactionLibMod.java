package live.luya.eventtransactionlib.forge;

import live.luya.eventtransactionlib.EventTransactionApiProvider;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.forge.impl.EventTransactionApiForgeImpl;
import net.minecraftforge.fml.common.Mod;

@Mod(EventTransactionLibMod.MODID)
public class EventTransactionLibMod {

	public static final String MODID = "event_transaction_lib";

	public EventTransactionLibMod() {
		EventTransactionApiForgeImpl impl = new EventTransactionApiForgeImpl();
		EventTransactionApiProvider.appendApi(RegistrationOrder.FORGE, impl);
		EventTransactionApiProvider.stackRegistrationOrder(RegistrationOrder.FORGE);

		System.out.println("EventTransactionLib has been registered.");
		EventTransactionApiProvider.getApi()
				.prepareRegistration(RegistrationOrder.HYBRID_MOD_BUKKIT, RegistrationOrder.FORGE, (api) -> {
					impl.attachExternalClassLoaders(impl.getClassLoaderSupplier().get());
					impl.doDebug();
				});
	}

}
