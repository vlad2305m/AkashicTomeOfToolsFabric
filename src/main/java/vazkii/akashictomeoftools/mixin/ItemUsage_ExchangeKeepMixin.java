package vazkii.akashictomeoftools.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(ItemUsage.class)
public class ItemUsage_ExchangeKeepMixin {
    @Inject(method = "exchangeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", shift = At.Shift.BEFORE), cancellable = true)
    private static void exchangeContent(ItemStack inputStack, PlayerEntity player, ItemStack outputStack, boolean creativeOverride, CallbackInfoReturnable<ItemStack> cir) {
        if ((inputStack instanceof ItemStackWrap tome) && tome.notself && tome.getContent().isEmpty()) {
            tome.setContent(outputStack);
            cir.setReturnValue(inputStack);
        }
    }
}
