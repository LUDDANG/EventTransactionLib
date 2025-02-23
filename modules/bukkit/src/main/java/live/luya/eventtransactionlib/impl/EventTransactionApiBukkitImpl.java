package live.luya.eventtransactionlib.impl;

import live.luya.eventtransactionlib.EventTransactionListener;
import live.luya.eventtransactionlib.UnregisterHandler;
import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;

import java.util.List;

public class EventTransactionApiBukkitImpl extends EventTransactionApiImpl {

	private final List<ClassLoader> platformLoader;

	public EventTransactionApiBukkitImpl(List<ClassLoader> loader) {
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
	public List<ClassLoader> getPlatformClassLoader() {
		return platformLoader;
	}
}
