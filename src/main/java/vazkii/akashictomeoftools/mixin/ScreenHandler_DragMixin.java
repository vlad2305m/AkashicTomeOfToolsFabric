package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(ScreenHandler.class)
public class ScreenHandler_DragMixin {
    @Redirect(method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", ordinal = 1))
    private ItemStack untomeStack(ItemStack stack){
        if (stack instanceof ItemStackWrap && ((ItemStackWrap)stack).notself)
            return ((ItemStackWrap) stack).content.copy();

        return stack.copy();
    }

    @ModifyVariable(method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/ScreenHandler;getCursorStack()Lnet/minecraft/item/ItemStack;", ordinal = 4), ordinal = 1)
    private ItemStack untomeStack2(ItemStack stack){
        if (stack instanceof ItemStackWrap && ((ItemStackWrap)stack).notself)
            return ((ItemStackWrap) stack).content;

        return stack.copy();
    }
}
