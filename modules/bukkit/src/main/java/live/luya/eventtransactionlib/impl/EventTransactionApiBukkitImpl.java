package live.luya.eventtransactionlib.impl;

import live.luya.eventtransactionlib.EventTransactionListener;
import live.luya.eventtransactionlib.UnregisterHandler;
import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.function.Consumer;

public class EventTransactionApiBukkitImpl extends EventTransactionApiImpl {

	private final ClassLoader platformLoader;

	public EventTransactionApiBukkitImpl(ClassLoader loader) {
		this.platformLoader = loader;
	}


	@Override
	public UnregisterHandler registerListener(EventTransactionListener listener) {
		return super.registerListener(listener);
	}

	@Override
	public void attachExternalClassLoaders(List<ClassLoader> loaders) {

	}

	@Override
	public void detachExternalClassLoaders(List<ClassLoader> loaders) {

	}

	@Override
	public ClassLoader getPlatformClassLoader() {
		return platformLoader;
	}
}
