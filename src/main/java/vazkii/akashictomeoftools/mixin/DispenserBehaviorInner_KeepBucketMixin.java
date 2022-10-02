package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$8")
public class DispenserBehaviorInner_KeepBucketMixin {
    @Inject(method = "dispenseSilently(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack", ordinal = 0), cancellable = true)
    public void getBucket(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir){
        if (stack instanceof ItemStackWrap tome && tome.notself) {
            tome.setContent(new ItemStack(Items.BUCKET));
            cir.setReturnValue(tome);
        }
    }

}
