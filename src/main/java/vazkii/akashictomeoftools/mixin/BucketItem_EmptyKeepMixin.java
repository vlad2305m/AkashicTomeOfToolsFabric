package vazkii.akashictomeoftools.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(BucketItem.class)
public class BucketItem_EmptyKeepMixin {
    @Inject(method = "getEmptiedStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private static void getBucket(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if ((stack instanceof ItemStackWrap tome) && tome.notself && !player.getAbilities().creativeMode) {
            tome.setContent(new ItemStack(Items.BUCKET));
            cir.setReturnValue(tome);
        }
    }
}
