package live.luya.eventtransactionlib.forge.util;

import com.mojang.logging.LogUtils;
import cpw.mods.modlauncher.api.INameMappingService;
import live.luya.eventtransactionlib.forge.EventTransactionLibMod;
import net.minecraftforge.fml.loading.LogMarkers;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class ReflectionRemappingHelper {
	private static final Logger LOGGER = LogUtils.getLogger();
	private HashMap<String, List<String>> methods;
	private HashMap<String, List<String>> fields;


	public ReflectionRemappingHelper() {
		findFieldMapping("");
		findMethodMapping("");
	}


	public String mappingName() {
		return "srgtomcp";
	}

	public String mappingVersion() {
		return "1234";
	}

	public Map.Entry<String, String> understanding() {
		return Pair.of("srg", "mcp");
	}

	public BiFunction<INameMappingService.Domain, String, List<String>> namingFunction() {
		return this::findMapping;
	}

	private List<String> findMapping(INameMappingService.Domain domain, String srgName) {
		switch (domain) {
			case FIELD -> {
				return this.findFieldMapping(srgName);
			}
			case METHOD -> {
				return this.findMethodMapping(srgName);
			}
			default -> {
				return Collections.singletonList(srgName);
			}
		}
	}

	private List<String> findMethodMapping(String origin) {
		if (this.methods == null) {
			HashMap<String, List<String>> tmpmethods = new HashMap<>(1000);
			Objects.requireNonNull(tmpmethods);
			loadMappings("/methods.csv", (k, v) -> {
				tmpmethods.computeIfAbsent(k, (e) -> new ArrayList<>()).add(v);
			});
			this.methods = tmpmethods;
			System.out.println("Loaded " + this.methods.size() + " method mappings from methods.csv");
		}

		return this.methods.getOrDefault(origin, new ArrayList<>());
	}

	private List<String> findFieldMapping(String origin) {
		if (this.fields == null) {
			HashMap<String, List<String>> tmpfields = new HashMap<>(1000);
			Objects.requireNonNull(tmpfields);
			loadMappings("/fields.csv", (k, v) -> {
				tmpfields.computeIfAbsent(k, (e) -> new ArrayList<>()).add(v);
			});
			this.fields = tmpfields;
			System.out.println("Loaded " + this.fields.size() + " field mappings from fields.csv");
		}

		return this.fields.getOrDefault(origin, new ArrayList<>());
	}

	private static void loadMappings(String mappingFileName, BiConsumer<String, String> mapStore) {
		try (InputStream stream = EventTransactionLibMod.class.getResourceAsStream(mappingFileName)) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
				reader.lines().skip(1L).map((e) -> e.split(",")).forEach((e) -> mapStore.accept(e[1], e[0]));
			} catch (IOException e1) {
				LOGGER.error(LogMarkers.CORE, "Error reading mappings", e1);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
