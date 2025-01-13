package live.luya.eventtransactionlib;

import live.luya.eventtransactionlib.impl.EventTransactionApiBukkitImpl;
import live.luya.eventtransactionlib.impl.PurePluginClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class EventTransactionPlugin extends JavaPlugin {
	private PurePluginClassLoader pureClassLoader;

	@Override
	public void onEnable() {
		try {
			List<PurePluginClassLoader.PluginData> data = new ArrayList<>();
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				JavaPlugin jp = (JavaPlugin) plugin;
				data.add(new PurePluginClassLoader.PluginData(jp));
			}
			pureClassLoader = new PurePluginClassLoader(
					getClassLoader().getParent(),
					data
			);
		} catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		Bukkit.getScheduler().runTask(this, () -> {
			EventTransactionApiProvider.appendApi(RegistrationOrder.BUKKIT, new EventTransactionApiBukkitImpl(pureClassLoader));
			EventTransactionApiProvider.stackRegistrationOrder(RegistrationOrder.BUKKIT);
		});
	}
}
