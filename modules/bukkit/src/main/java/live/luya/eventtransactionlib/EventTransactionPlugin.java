package live.luya.eventtransactionlib;

import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EventTransactionPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		EventTransactionApiProvider.setApi(new EventTransactionApiImpl());
		EventTransactionApi.getApi().stackRegistrationOrder(RegistrationOrder.BUKKIT);
	}
}
