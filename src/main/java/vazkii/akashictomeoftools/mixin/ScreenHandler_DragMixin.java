package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(ScreenHandler.class)
public class ScreenHandler_DragMixin {
    @Redirect(method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private ItemStack untomeStack(ItemStack stack){
        if (stack instanceof ItemStackWrap tome && ((ItemStackWrap)stack).notself) return tome.getContent().copy();

        return stack.copy();
    }

    @ModifyVariable(method = "internalOnSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/ScreenHandler;getCursorStack()Lnet/minecraft/item/ItemStack;", ordinal = 4), ordinal = 0)
    private ItemStack untomeStack2(ItemStack stack){
        if (stack instanceof ItemStackWrap tome && tome.notself) return tome.getContent();

        return stack.copy();
    }

    @ModifyArgs(method = "insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;canCombine(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private void canCombineSwapForShiftItemTp(Args args) {
         ItemStack a = args.get(0);
         args.set(0, args.get(1));
         args.set(1, a);
    }
}
