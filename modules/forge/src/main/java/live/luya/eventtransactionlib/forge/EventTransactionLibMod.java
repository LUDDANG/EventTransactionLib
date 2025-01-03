package live.luya.eventtransactionlib.forge;

import live.luya.eventtransactionlib.EventTransactionApi;
import live.luya.eventtransactionlib.EventTransactionApiProvider;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import net.minecraftforge.fml.common.Mod;

@Mod(EventTransactionLibMod.MODID)
public class EventTransactionLibMod {

	public static final String MODID = "event_transaction_lib";

	public EventTransactionLibMod() {
		EventTransactionApiProvider.setApi(new EventTransactionApiImpl());
		EventTransactionApi.getApi().stackRegistrationOrder(RegistrationOrder.FORGE);
	}
}
