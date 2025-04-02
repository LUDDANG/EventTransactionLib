package live.luya.eventtransactionlib.neoforge.mixins;

import cpw.mods.cl.ModuleClassLoader;
import live.luya.eventtransactionlib.neoforge.data.ParentLoaderAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModuleClassLoader.class)
public class ModuleClassLoaderMixin implements ParentLoaderAccessor {
	@Shadow
	@Final
	private Map<String, ClassLoader> parentLoaders;

	@Shadow private ClassLoader fallbackClassLoader;

	@Inject(at = @At("HEAD"), method = "findClass(Ljava/lang/String;)Ljava/lang/Class;")
	public void onFindClass(String name, CallbackInfoReturnable<Class<?>> cir) {
		System.out.println("Mixin working for " + name);
	}

	@Inject(at = @At("HEAD"), method = "<clinit>")
	private static void onFindClass2(CallbackInfo ci) {
		System.out.println("[EventTransactionLib] ModuleClassLoaderMixin initialized");
	}



	@Override
	public Map<String, ClassLoader> get_parent_loaders() {
		return parentLoaders;
	}

	@Override
	public ClassLoader get_fallback_loader() {
		return fallbackClassLoader;
	}
}
