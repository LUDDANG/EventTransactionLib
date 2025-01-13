package live.luya.eventtransactionlib.forge;

import cpw.mods.modlauncher.TransformingClassLoader;
import live.luya.eventtransactionlib.EventTransactionApi;
import live.luya.eventtransactionlib.EventTransactionApiProvider;
import live.luya.eventtransactionlib.RegistrationOrder;
import live.luya.eventtransactionlib.forge.impl.EventTransactionApiForgeImpl;
import live.luya.eventtransactionlib.internal.EventTransactionApiImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.securemodules.SecureModuleClassLoader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mod(EventTransactionLibMod.MODID)
public class EventTransactionLibMod {

	public static final String MODID = "event_transaction_lib";

	public EventTransactionLibMod() {
		EventTransactionApiProvider.appendApi(RegistrationOrder.FORGE, new EventTransactionApiForgeImpl());
		EventTransactionApiProvider.stackRegistrationOrder(RegistrationOrder.FORGE);
	}

}
