package live.luya.eventtransactionlib.neoforge.impl;

import cpw.mods.cl.ModuleClassLoader;
import cpw.mods.modlauncher.TransformingClassLoader;
import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import net.neoforged.fml.loading.FMLLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;

public class EventTransactionApiNeoForgeImpl extends EventTransactionApiImpl {
	private final ModuleClassLoader forgeLoader;

	private final List<ClassLoader> classLoaderList = new ArrayList<>();

	private ClassLoader fallBack;

	private ClassLoader parent;

	private ClassLoader mixedClassLoader;

	@SuppressWarnings("unchecked")
	public EventTransactionApiNeoForgeImpl() {
		forgeLoader = (TransformingClassLoader) findSecureModuleClassLoader();
//		ParentLoaderAccessor parentLoaderAccessor = (ParentLoaderAccessor) forgeLoader;
		//			classLoaderList = ;
//		try {
//			Field f = ModuleClassLoader.class.getDeclaredField("fallbackClassLoader");
//			f.setAccessible(true);
//			classLoaderList.add((ClassLoader) f.get(forgeLoader));
//		} catch (NoSuchFieldException e) {
//			throw new RuntimeException(e);
//		} catch (IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
//
//		classLoaderList.add(((ParentLoaderAccessor) forgeLoader).get_fallback_loader());
//
//		mixedClassLoader = new MixedClassLoader(classLoaderList);

//		forgeLoader.setFallbackClassLoader(mixedClassLoader);
		try {
			ClassLoader cl = (ClassLoader) MethodHandles.privateLookupIn(ModuleClassLoader.class, MethodHandles.lookup()).findVarHandle(
					ModuleClassLoader.class,
					"fallbackClassLoader",
					ClassLoader.class
			).get(forgeLoader);
			System.out.println(cl);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void attachExternalClassLoaders(List<ClassLoader> loaders) {
		//		cache.clear();
		//		cache.addAll(classLoaderList);
		//		classLoaderList.clear();
		for (ClassLoader loader : loaders) {
			if (loader != forgeLoader) {
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
	public List<ClassLoader> getPlatformClassLoader() {
		return new ArrayList<>();
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
				if (currentClass.getName().contains("TransformingClassLoader")) {
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


	private static String read(Attributes main, Attributes trusted, Attributes.Name name) {
		if (trusted != null && trusted.containsKey(name)) {
			return trusted.getValue(name);
		} else {
			return main == null ? null : main.getValue(name);
		}
	}


	private static <E extends Throwable, R> R sneak(Exception exception) throws E, Exception {
		throw exception;
	}


	protected byte[] maybeTransformClassBytes(byte[] bytes, String name, String context) {
		return bytes;
	}


	private String classToResource(String name) {
		return name.replace('.', '/') + ".class";
	}


	protected byte[] getClassBytes(ModuleReader reader, ModuleReference ref, String name) throws IOException {
		Optional<InputStream> read = reader.open(this.classToResource(name));
		if (!read.isPresent()) {
			return new byte[0];
		} else {
			try (InputStream is = (InputStream) read.get()) {
				return is.readAllBytes();
			}
		}
	}

	private static String classToPackage(String name) {
		int idx = name.lastIndexOf(46);
		return idx != -1 && idx != name.length() - 1 ? name.substring(0, idx) : "";
	}
}
