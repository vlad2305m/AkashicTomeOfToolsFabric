package vazkii.akashictomeoftools.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandler_2ndSlotDecrementMixin {

    @Mixin(AnvilScreenHandler.class)
    public static class StackerAnvilScreenHandler_2ndSlotDecrementMixin {
        // Credit to @ZoeyTheEgoist for the original code behind this mixin and @Andrew6rant for Stacker mod
        @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 3))
        private void setDecrementSlot1StackCountCpFromStackerToAkashic(Inventory inventory, int slot, ItemStack stack) {
            ItemStack newStack = inventory.getStack(1);
            newStack.decrement(1);
            inventory.setStack(1, newStack);
        }
    }
    @Redirect(method = "onTakeOutput(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"))
    public int dontVoidSecondStackInAnvilAkashic(ItemStack instance) {
       return Integer.MAX_VALUE;
    }

}
