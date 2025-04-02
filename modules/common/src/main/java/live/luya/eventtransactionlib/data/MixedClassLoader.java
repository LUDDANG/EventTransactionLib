package live.luya.eventtransactionlib.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MixedClassLoader extends ClassLoader {
	private final List<ClassLoader> origin;
	private Method findClassMethod = null;

	public MixedClassLoader(List<ClassLoader> origin) {
		this.origin = origin;
		updateFindClassMethod();
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		updateFindClassMethod();
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
		updateFindClassMethod();
		for (ClassLoader loader : origin) {
			try {
				return (Class<?>) findClassMethod.invoke(loader, name);
			} catch (IllegalAccessException | InvocationTargetException ignored) {
			}
		}
		throw new ClassNotFoundException();
	}

	private void updateFindClassMethod() {
		if (findClassMethod != null) return;
		try {
			if (origin.isEmpty()) return;
			this.findClassMethod = origin.getFirst().getClass().getDeclaredMethod("findClass", String.class);
			this.findClassMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
}
