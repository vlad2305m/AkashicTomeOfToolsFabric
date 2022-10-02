package vazkii.akashictomeoftools.mixin.plugin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ModCompatPlugin implements IMixinConfigPlugin {
    //static boolean flag = true;
    @Override
    public void onLoad(String mixinPackage) {
        //if (flag){
        //    Mixins.getConfigs().forEach(c->{
        //        try {
        //            findAndKill(c,"stacker.mixins.json", "AnvilScreenHandlerMixin");
        //        } catch (NoSuchFieldException | IllegalAccessException e) {
        //            throw new RuntimeException(e);
        //        }
        //    });
        //    flag=false;}
    }

    //miserably failed
    /*static void findAndKill(Config c, String mod, String... mixin) throws NoSuchFieldException, IllegalAccessException {
        if(Objects.equals(c.getName(), mod)) {
            IMixinConfig config = c.getConfig();
            Field mixins = config.getClass().getDeclaredField("mixinClasses");
            mixins.setAccessible(true);
            List<String> l = (List<String>) mixins.get(config);
            l.removeAll(List.of(mixin));
            System.out.println("list:");
            l.forEach(e->System.out.println(e));
        }
    }*/

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean bl = false;
        //stacker Dispenser compat
        bl |= mixinClassName.equals("vazkii.akashictomeoftools.mixin.DispenserBehaviorInner_KeepBucketMixin") && FabricLoader.getInstance().isModLoaded("stacker");
        bl |= mixinClassName.equals("vazkii.akashictomeoftools.mixin.AnvilScreenHandler_2ndSlotDecrementMixin$StackerAnvilScreenHandler_2ndSlotDecrementMixin") && FabricLoader.getInstance().isModLoaded("stacker");
        //if (mixinClassName.equals("AnvilScreenHandler_2ndSlotDecrementMixin") && FabricLoader.getInstance().isModLoaded("stacker")) System.out.println("AkashicTomeOfTools: yeeted stacker's anvil mixin. mine is better.");
        //if (mixinClassName.equals("AnvilScreenHandler_2ndSlotDecrementMixin") && FabricLoader.getInstance().isModLoaded("charm")) System.out.println("AkashicTomeOfTools: yeeted charm's anvil mixin. mine is better.");
        return !bl;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
