package vazkii.akashictomeoftools.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFrameEntity.class)
public class ItemFrameEntity_InsertMixin {
    @Redirect(method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/ItemFrameEntity;setHeldItemStack(Lnet/minecraft/item/ItemStack;)V"))
    public void unDupe(ItemFrameEntity itemFrameEntity, ItemStack stack, PlayerEntity player) {
        if (!player.getAbilities().creativeMode) {
            itemFrameEntity.setHeldItemStack(stack.split(1));
        }
        else itemFrameEntity.setHeldItemStack(stack);
    }
    @Redirect(method = "interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    public void unDecrement(ItemStack itemStack, int amount) { }
}
