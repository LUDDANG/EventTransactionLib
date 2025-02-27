package live.luya.eventtransactionlib.forge.impl;

import live.luya.eventtransactionlib.forge.util.ForgeReflectionUtil;
import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.securemodules.SecureModuleClassLoader;
import net.minecraftforge.securemodules.SecureModuleReference;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Field;
import java.util.*;
import java.util.jar.Attributes;

public class EventTransactionApiForgeImpl extends EventTransactionApiImpl {
	private final SecureModuleClassLoader forgeLoader;

	private final List<ClassLoader> classLoaderList;

	private final List<ClassLoader> cache = new ArrayList<>();

	private Map<String, ModuleReference> ourModules;

	private Map<String, ClassLoader> packageToParentLoader;

	private Map<String, ResolvedModule> packageToOurModules;

	private Map<String, SecureModuleReference> ourModulesSecure;

	private ClassLoader fallBack;
	private ClassLoader parent;

	@SuppressWarnings("unchecked")
	public EventTransactionApiForgeImpl() {
		forgeLoader = (SecureModuleClassLoader) findSecureModuleClassLoader();
		classLoaderList = extractClassLoaderFieldFrom(forgeLoader);
		try {
			ourModules = (Map<String, ModuleReference>) ForgeReflectionUtil.findObfuscatedField(SecureModuleClassLoader.class, "ourModules")
					.get(forgeLoader);
			packageToParentLoader = (Map<String, ClassLoader>) ForgeReflectionUtil.findObfuscatedField(SecureModuleClassLoader.class, "packageToParentLoader")
					.get(forgeLoader);
			fallBack = (ClassLoader) ForgeReflectionUtil.findObfuscatedField(SecureModuleClassLoader.class, "fallbackClassLoader")
					.get(forgeLoader);
			parent = (ClassLoader) ForgeReflectionUtil.findObfuscatedField(SecureModuleClassLoader.class, "parent")
					.get(forgeLoader);
			packageToOurModules = (Map<String, ResolvedModule>) ForgeReflectionUtil.findObfuscatedField(SecureModuleClassLoader.class, "packageToOurModules")
					.get(forgeLoader);
			ourModulesSecure = (Map<String, SecureModuleReference>) ForgeReflectionUtil.findObfuscatedField(SecureModuleClassLoader.class, "ourModulesSecure")
					.get(forgeLoader);
		} catch (IllegalAccessException e) {
			System.err.println("Cannot access SecureModuleClassLoader.ourModules field.");
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


	public void doDebug() {
		System.out.println("ForgeLoader: " + classLoaderList);
		System.out.println("Fallback: " + fallBack);
		System.out.println("Parent: " + parent);
		for (ClassLoader loader : classLoaderList) {
			try {
				loader.loadClass("org.bukkit.Bukkit");
				System.out.println("Bukkit class found in ForgeLoader index " + classLoaderList.indexOf(loader));
			} catch (Exception ignored) {
			}
		}
		for (Map.Entry<String, ClassLoader> entry : packageToParentLoader.entrySet()) {
			try {
				entry.getValue().loadClass("org.bukkit.Bukkit");
				System.out.println("Bukkit class found in PackageToParentLoader " + entry.getKey());
			} catch (Exception ignored) {
			}
		}
		for (Map.Entry<String, ModuleReference> entry : ourModules.entrySet()) {
			System.out.println("Module: " + entry.getKey() + " -> " + entry.getValue());
		}
		try {
			fallBack.loadClass("org.bukkit.Bukkit");
			System.out.println("Bukkit class found in fallback loader.");
		} catch (Exception ignored) {
		}

		try {
			parent.loadClass("org.bukkit.Bukkit");
			System.out.println("Bukkit class found in parent loader.");
		} catch (Exception ignored) {
		}
		String pkg = classToPackage("org.bukkit.Bukkit");
		System.out.println("Package: " + pkg);
		ResolvedModule module = packageToOurModules.get(pkg);
		if (module != null) {
			System.out.println("Module: " + module);
			ModuleReference ref = module.reference();
			try (ModuleReader reader = ref.open()) {
				Class<?> cls = (Class<?>) ForgeReflectionUtil.findObfuscatedMethod(SecureModuleClassLoader.class, "readerToClass", ModuleReader.class, ModuleReference.class, String.class).invoke(
						forgeLoader, reader, ref, "live.luya.eventtransactionlib.EventTransactionPlugin");
				System.out.println("Class: " + cls);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		for (ClassLoader cx : getClassLoaderSupplier().get()) {
			try {
				System.out.println("Testing plugin class with loader: " + cx);
				Class<?> cls = cx.loadClass("live.luya.eventtransactionlib.EventTransactionPlugin");
				System.out.println("Class: " + cls);
			} catch (Exception ignored) {
			}
		}

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
			try (InputStream is = (InputStream)read.get()) {
				return is.readAllBytes();
			}
		}
	}

	private static String classToPackage(String name) {
		int idx = name.lastIndexOf(46);
		return idx != -1 && idx != name.length() - 1 ? name.substring(0, idx) : "";
	}
}
