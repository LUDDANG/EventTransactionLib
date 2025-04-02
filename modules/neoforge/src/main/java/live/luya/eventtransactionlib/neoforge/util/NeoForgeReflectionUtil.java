package live.luya.eventtransactionlib.neoforge.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class NeoForgeReflectionUtil {
	public static Method findObfuscatedMethod(Class<?> clazz, String name, Class<?>... params) {
		try {
			Method method = clazz.getDeclaredMethod(name, params);
			method.setAccessible(true);
			return method;
		} catch (NoSuchMethodException e) {
			try {
				List<String> remapped = remapName(ReflectionRemappingHelper.MappingType.METHOD, name);
				for (String s : remapped) {
					try {
						Method method = clazz.getDeclaredMethod(s, params);
						method.setAccessible(true);
						return method;
					} catch (NoSuchMethodException ignored) {
					}
				}
				throw new NoSuchMethodException("Cannot find method " + name + " in " + clazz.getName());
			} catch (Exception ex) {
				throw new IllegalStateException("Cannot find method " + name + " in " + clazz.getName());
			}
		}
	}

	public static Field findObfuscatedField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException e) {
			try {
				List<String> remapped = remapName(ReflectionRemappingHelper.MappingType.FIELD, name);
				for (String s : remapped) {
					try {
						Field field = clazz.getDeclaredField(s);
						field.setAccessible(true);
						return field;
					} catch (NoSuchFieldException ignored) {
					}
				}
				throw new NoSuchFieldException("Cannot find field " + name + " in " + clazz.getName());
			} catch (Exception ex) {
				throw new IllegalStateException("Cannot find field " + name + " in " + clazz.getName());
			}
		}
	}

	private static final ReflectionRemappingHelper service = new ReflectionRemappingHelper();

	public static @NotNull List<String> remapName(ReflectionRemappingHelper.MappingType domain, String name) {
		return service.namingFunction().apply(domain, name);
	}

}
