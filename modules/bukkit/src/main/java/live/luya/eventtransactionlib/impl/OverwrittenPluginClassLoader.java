package live.luya.eventtransactionlib.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class OverwrittenPluginClassLoader extends ClassLoader {
	private final List<ClassLoader> origin;
	private final Method findClassMethod;

	public OverwrittenPluginClassLoader(List<ClassLoader> origin) {
		this.origin = origin;
		try {
			this.findClassMethod = origin.getFirst().getClass().getDeclaredMethod("findClass", String.class);
			this.findClassMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		for (ClassLoader loader : origin) {
			try {
				return (Class<?>) findClassMethod.invoke(loader, name);
			} catch (IllegalAccessException | InvocationTargetException ignored) {
			}
		}
		throw new ClassNotFoundException();
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		for (ClassLoader loader : origin) {
			try {
				return (Class<?>) findClassMethod.invoke(loader, name);
			} catch (IllegalAccessException | InvocationTargetException ignored) {
			}
		}
		throw new ClassNotFoundException();
	}
}
