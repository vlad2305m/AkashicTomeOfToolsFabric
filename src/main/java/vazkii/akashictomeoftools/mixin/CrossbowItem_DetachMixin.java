package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(CrossbowItem.class)
public class CrossbowItem_DetachMixin {
    @Redirect(method = "loadProjectile(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;ZZ)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;split(I)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack detachArrow(ItemStack itemStack, int amount) {
        if (itemStack instanceof ItemStackWrap tome && itemStack.getCount() == 1) tome.detach = true;
        return itemStack.split(amount);
    }
}
