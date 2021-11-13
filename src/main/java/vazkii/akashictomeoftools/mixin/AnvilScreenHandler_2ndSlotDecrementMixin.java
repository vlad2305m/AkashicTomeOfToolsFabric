package vazkii.akashictomeoftools.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandler_2ndSlotDecrementMixin {
    @Redirect(method = "onTakeOutput(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    public void decrement(Inventory inventory, int slot, ItemStack stack) {
        if (slot != 1 || stack != ItemStack.EMPTY) { inventory.setStack(slot, stack); return; }
        ItemStack stack1 = inventory.getStack(slot);
        if (!(stack1 instanceof ItemStackWrap tome) || !tome.notself) { inventory.setStack(slot, stack); return; }
        ((ItemStackWrap)stack1).removeSelectedStack();
    }
}
