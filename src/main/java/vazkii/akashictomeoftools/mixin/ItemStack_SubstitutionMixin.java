package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(ItemStack.class)
public class ItemStack_SubstitutionMixin {

    @Inject(method = "fromNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/item/ItemStack;", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private static void loadTome(NbtCompound nbt, CallbackInfoReturnable<ItemStack> cir) {
        if(!nbt.contains("tag", 10)) return;
        NbtCompound nbt2 = nbt.getCompound("tag");
        if (nbt2.contains(ItemStackWrap.ACTUAL_TOME_KEY) || nbt2.contains(ItemStackWrap.ITEMS_KEY) || nbt2.contains(ItemStackWrap.SELECTED_KEY)) cir.setReturnValue(new ItemStackWrap(java.util.Optional.of(nbt)));
    }

    @Inject(method = "canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void combineMixin(ItemStack stackBase, ItemStack stack2, CallbackInfoReturnable<Boolean> cir) {
        if (stackBase instanceof ItemStackWrap tome && tome.notself) cir.setReturnValue(ItemStack.canCombine(((ItemStackWrap) stackBase).getContent(), stack2));
    }
}
