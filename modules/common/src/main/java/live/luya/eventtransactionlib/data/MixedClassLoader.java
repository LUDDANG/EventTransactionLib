package live.luya.eventtransactionlib.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MixedClassLoader extends ClassLoader {
	private final List<ClassLoader> origin;
	private boolean triedToFind = false;
	private Method findClassMethod = null;

	public MixedClassLoader(List<ClassLoader> origin) {
		this.origin = origin;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		for (ClassLoader loader : origin) {
			Class<?> clazz = findClassOrLoad(loader, name, true);
			if (clazz != null) {
				return clazz;
			}
		}
		throw new ClassNotFoundException();
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		for (ClassLoader loader : origin) {
			Class<?> clazz = findClassOrLoad(loader, name, true);
			if (clazz != null) {
				return clazz;
			}
		}
		throw new ClassNotFoundException();
	}

	private Class<?> findClassOrLoad(ClassLoader loader,String name,  boolean tryUseReflection) {
		if (tryUseReflection && findClassMethod != null) {
			updateFindClassMethod();
			try {
				return (Class<?>) findClassMethod.invoke(origin.get(0), "java.lang.String");
			} catch (IllegalAccessException | InvocationTargetException e) {
				return findClassOrLoad(loader, name, false);
			}
		} else {
			try {
				return loader.loadClass(name);
			} catch (Exception ignored) {}
		}
		return null;
	}

	private void updateFindClassMethod() {
		if (triedToFind) return;
		triedToFind = true;
		for(ClassLoader cl : origin) {
			try {
				this.findClassMethod = origin.getFirst().getClass().getDeclaredMethod("findClass", String.class);
				this.findClassMethod.setAccessible(true);
				break;
			} catch (NoSuchMethodException ignored) {}
		}
	}
}
