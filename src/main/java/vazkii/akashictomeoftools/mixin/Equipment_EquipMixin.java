package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Equipment.class)
public interface Equipment_EquipMixin {
    @Redirect(method = "equipAndSwap", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copyAndEmpty()Lnet/minecraft/item/ItemStack;"))
    default ItemStack panic(ItemStack itemStack) {
        return itemStack.split(itemStack.getCount());
    }
}
