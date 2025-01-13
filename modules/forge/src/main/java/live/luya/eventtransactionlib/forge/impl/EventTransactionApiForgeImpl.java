package live.luya.eventtransactionlib.forge.impl;

import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.securemodules.SecureModuleClassLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EventTransactionApiForgeImpl extends EventTransactionApiImpl {
	private final SecureModuleClassLoader forgeLoader;

	private final List<ClassLoader> classLoaderList;

	private final List<ClassLoader> cache = new ArrayList<>();

	public EventTransactionApiForgeImpl() {
		forgeLoader = (SecureModuleClassLoader) findSecureModuleClassLoader();
		classLoaderList = extractClassLoaderFieldFrom(forgeLoader);
	}

	@Override
	public void attachExternalClassLoaders(List<ClassLoader> loaders) {
//		cache.clear();
//		cache.addAll(classLoaderList);
//		classLoaderList.clear();
		for (ClassLoader loader : loaders) {
			if (loader != forgeLoader) {
//				classLoaderList.add(new ClassLoader() {
//					@Override
//					public Class<?> loadClass(String name) throws ClassNotFoundException {
//						System.out.println("Requesting class: " + name);
//						return super.loadClass(name);
//					}
//				});
				classLoaderList.add(loader);
			}
		}
	}

	@Override
	public void detachExternalClassLoaders(List<ClassLoader> loaders) {
		for (ClassLoader loader : loaders) {
			if (loader != forgeLoader)
				classLoaderList.remove(loader);
		}
//		classLoaderList.clear();
//		classLoaderList.addAll(cache);
//		cache.clear();
	}

	@Override
	public ClassLoader getPlatformClassLoader() {
		return forgeLoader;
	}

	public static ClassLoader findSecureModuleClassLoader() {
		ModuleLayer gameLayer = FMLLoader.getGameLayer();
		if (gameLayer == null) return null;

		// Get a module from the game layer
		Optional<Module> anyModule = gameLayer.modules().stream().findFirst();
		if (anyModule.isEmpty()) return null;

		ClassLoader currentLoader = anyModule.get().getClassLoader();
		while (currentLoader != null) {
			// Check the full class name hierarchy
			Class<?> currentClass = currentLoader.getClass();
			while (currentClass != null) {
				if (currentClass.getName().contains("SecureModuleClassLoader")) {
					return currentLoader;
				}
				// Check superclass
				currentClass = currentClass.getSuperclass();
			}
			// Move to parent loader
			currentLoader = currentLoader.getParent();
		}

		// If we can't find it, return the TransformingClassLoader instead
		return Thread.currentThread().getContextClassLoader();
	}

	private List<ClassLoader> extractClassLoaderFieldFrom(SecureModuleClassLoader loader) {
		try {
			Class<?> c = SecureModuleClassLoader.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType().equals(List.class)) {
					f.setAccessible(true);
					return (List<ClassLoader>) f.get(loader);
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		throw new IllegalStateException("Cannot find List<ClassLoader> field in SecureModuleClassLoader");
	}


}
