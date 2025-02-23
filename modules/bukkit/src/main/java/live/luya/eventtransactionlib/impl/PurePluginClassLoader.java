package live.luya.eventtransactionlib.impl;

import com.google.common.io.ByteStreams;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PurePluginClassLoader extends URLClassLoader {

	private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

	private java.util.logging.Logger logger; // Paper - add field
	private final List<PluginData> pluginData;

	static {
		ClassLoader.registerAsParallelCapable();
	}

	public PurePluginClassLoader(final ClassLoader parent, List<PluginData> files) throws IOException { // Paper - use JarFile provided by SpigotPluginProvider
		super(files.stream().map(file -> {
			try {
				return file.url;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).toArray(URL[]::new), parent);
		this.pluginData = files;
	}

	@Override
	public URL getResource(String name) {
		// Paper start
		URL resource = findResource(name);
		//		if (resource == null && libraryLoader != null) {
		//			return libraryLoader.getResource(name);
		//		}
		return resource;
		// Paper end
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		// Paper start
		java.util.ArrayList<URL> resources = new java.util.ArrayList<>();
		addEnumeration(resources, findResources(name));
		//		if (libraryLoader != null) {
		//			addEnumeration(resources, libraryLoader.getResources(name));
		//		}
		return Collections.enumeration(resources);
		// Paper end
	}

	private <T> void addEnumeration(java.util.ArrayList<T> list, Enumeration<T> enumeration) {
		while (enumeration.hasMoreElements()) {
			list.add(enumeration.nextElement());
		}
	}

	// Paper end

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		return loadClass0(name, resolve, true, true);
	}

	Class<?> loadClass0(String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
		try {
			//			int count = 0;
			//			for (StackTraceElement element : new Throwable().getStackTrace()) {
			//				if (element.getClassName().contains("PurePluginClassLoader")) {
			//					count++;
			//				}
			//			}
			//			if (count > 4) {
			//				System.out.println("Infinite loop detected in class loading for " + name + ". This is likely a bug in the plugin.");
			//				// This is infinite-loop. To prevent this, just return null.
			//				return findClass(name);
			//			}
			Class<?> result = ClassLoader.getSystemClassLoader().loadClass(name);
			if (result != null) {
				return result;
			}
			// SPIGOT-6749: Library classes will appear in the above, but we don't want to return them to other plugins
			//			if (checkGlobal || result.getClassLoader() == this) {
			//				return result;
			//			}
		} catch (ClassNotFoundException ex) {
			try {
				return findClass(name);
			} catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Failed to load custom class", e);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		throw new ClassNotFoundException(name);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
//		if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
//			throw new ClassNotFoundException(name);
//		}
		Class<?> result = classes.get(name);

		if (result == null) {
			for (PluginData data : pluginData) {
				String path = name.replace('.', '/').concat(".class");
				JarEntry entry = data.jar.getJarEntry(path);

				if (entry != null) {
					byte[] classBytes;

					try (InputStream is = data.jar.getInputStream(entry)) {
						classBytes = ByteStreams.toByteArray(is);
					} catch (IOException ex) {
						throw new ClassNotFoundException(name, ex);
					}

					classBytes = org.bukkit.Bukkit.getServer().getUnsafe()
							.processClass(data.description, path, classBytes); // Paper

					int dot = name.lastIndexOf('.');
					if (dot != -1) {
						String pkgName = name.substring(0, dot);
						if (getPackage(pkgName) == null) {
							try {
								//							if (manifest != null) {
								//								definePackage(pkgName, manifest, url);
								//							} else {
								definePackage(pkgName, null, null, null, null, null, null, null);
								//							}
							} catch (IllegalArgumentException ex) {
								if (getPackage(pkgName) == null) {
									throw new IllegalStateException("Cannot find package " + pkgName);
								}
							}
						}
					}

					CodeSigner[] signers = entry.getCodeSigners();
					CodeSource source = new CodeSource(data.url, signers);

					result = defineClass(name, classBytes, 0, classBytes.length, source);
					classes.put(name, result);
					this.setClass(name, result); // Paper
				}

				//			if (result == null) {
				//				result = super.findClass(name);
				//			}

			}
		}

		return result;
	}

	@Override
	public void close() throws IOException {
		try {
			// Paper start
			Collection<Class<?>> classes = getClasses();
			for (Class<?> clazz : classes) {
				removeClass(clazz);
			}
			// Paper end
			super.close();
		} finally {
			for (PluginData data : pluginData) {
				data.jar.close();
			}
		}
	}


	Collection<Class<?>> getClasses() {
		return classes.values();
	}

	// Paper start
	@Override
	public String toString() {
		return "PurePluginClassLoader{" +
				", url=" + pluginData.size() +
				" Plugins }";
	}

	void setClass(final String name, final Class<?> clazz) {
		if (org.bukkit.configuration.serialization.ConfigurationSerializable.class.isAssignableFrom(clazz)) {
			Class<? extends org.bukkit.configuration.serialization.ConfigurationSerializable> serializable = clazz.asSubclass(org.bukkit.configuration.serialization.ConfigurationSerializable.class);
			org.bukkit.configuration.serialization.ConfigurationSerialization.registerClass(serializable);
		}
	}

	private void removeClass(Class<?> clazz) {
		if (org.bukkit.configuration.serialization.ConfigurationSerializable.class.isAssignableFrom(clazz)) {
			Class<? extends org.bukkit.configuration.serialization.ConfigurationSerializable> serializable = clazz.asSubclass(org.bukkit.configuration.serialization.ConfigurationSerializable.class);
			org.bukkit.configuration.serialization.ConfigurationSerialization.unregisterClass(serializable);
		}
	}

	public static class PluginData {
		private final JavaPlugin plugin;
		private final URL url;
		private final PluginDescriptionFile description;
		private File file;
		private final JarFile jar;


		public PluginData(JavaPlugin plugin) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
			this.plugin = plugin;
			Method fileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
			fileMethod.setAccessible(true);
			this.file = (File) fileMethod.invoke(plugin);
			this.url = file.toURI().toURL();
			this.description = plugin.getDescription();
			this.jar = new JarFile(file);
		}

	}
}
