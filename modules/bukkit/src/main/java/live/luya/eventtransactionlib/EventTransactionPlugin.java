package live.luya.eventtransactionlib;

import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import org.bukkit.plugin.java.JavaPlugin;

public class EventTransactionPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		EventTransactionApiProvider.appendApi(RegistrationOrder.BUKKIT, new EventTransactionApiImpl());
		EventTransactionApi.getApi().stackRegistrationOrder(RegistrationOrder.BUKKIT);
	}
}
