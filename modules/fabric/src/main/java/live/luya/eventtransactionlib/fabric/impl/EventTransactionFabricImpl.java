package live.luya.eventtransactionlib.fabric.impl;

import live.luya.eventtransactionlib.data.MixedClassLoader;
import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;

import java.util.ArrayList;
import java.util.List;

public class EventTransactionFabricImpl extends EventTransactionApiImpl {
	private final Thread targetThread = Thread.currentThread();
	private final List<ClassLoader> additionalLoaders = new ArrayList<>();
	private final MixedClassLoader mixedLoader = new MixedClassLoader(additionalLoaders);

	public EventTransactionFabricImpl() {
		additionalLoaders.add(targetThread.getContextClassLoader());
		targetThread.setContextClassLoader(mixedLoader);
	}

	@Override
	public void attachExternalClassLoaders(List<ClassLoader> loaders) {
		additionalLoaders.addAll(loaders);
	}

	@Override
	public void detachExternalClassLoaders(List<ClassLoader> loaders) {
		additionalLoaders.removeAll(loaders);
	}

	@Override
	public List<ClassLoader> getPlatformClassLoader() {
		return new ArrayList<>();
	}

}
