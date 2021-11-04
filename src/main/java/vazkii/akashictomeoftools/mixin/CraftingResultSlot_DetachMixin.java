package vazkii.akashictomeoftools.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlot_DetachMixin {
    @Inject(method = "onTakeItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/inventory/CraftingInventory;getStack(I)Lnet/minecraft/item/ItemStack;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void detachSpend(PlayerEntity player, ItemStack stack, CallbackInfo ci, DefaultedList<ItemStack> defaultedList, int i, ItemStack itemStack) {
        if (itemStack instanceof ItemStackWrap && itemStack.getCount() == 1) ((ItemStackWrap) itemStack).detach = true;
    }
}
