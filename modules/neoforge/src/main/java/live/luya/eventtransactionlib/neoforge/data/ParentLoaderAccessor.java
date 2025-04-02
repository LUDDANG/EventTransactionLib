package live.luya.eventtransactionlib.neoforge.data;

import java.util.Map;

public interface ParentLoaderAccessor {
	Map<String, ClassLoader> get_parent_loaders();

	ClassLoader get_fallback_loader();
}
