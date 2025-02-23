package live.luya.eventtransactionlib;

import live.luya.eventtransactionlib.impl.EventTransactionApiBukkitImpl;
import live.luya.eventtransactionlib.impl.OverwrittenPluginClassLoader;
import live.luya.eventtransactionlib.impl.PurePluginClassLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
					Bukkit.class.getClassLoader(),
					data
			);
		} catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		try {
			Method classLoaderExtractor = JavaPlugin.class.getDeclaredMethod("getClassLoader");
			classLoaderExtractor.setAccessible(true);
			Bukkit.getScheduler().runTask(this, () -> {
				List<ClassLoader> loaders = Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(
						it -> it != this
				).map(
						it -> extract(it, classLoaderExtractor)
				).toList();

				EventTransactionApiProvider.appendApi(RegistrationOrder.BUKKIT, new EventTransactionApiBukkitImpl(
						List.of(
								new OverwrittenPluginClassLoader(loaders)
						)
				));
				EventTransactionApiProvider.stackRegistrationOrder(RegistrationOrder.BUKKIT);
			});
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}



	}

	public static ClassLoader extract(Plugin p, Method mtd) {
		try {
			return (ClassLoader) mtd.invoke(p);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
