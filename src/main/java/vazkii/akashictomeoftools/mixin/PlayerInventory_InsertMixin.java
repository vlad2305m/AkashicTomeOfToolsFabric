package vazkii.akashictomeoftools.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(PlayerInventory.class)
public class PlayerInventory_InsertMixin {

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"))
    public void panic(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack instanceof ItemStackWrap) ((ItemStackWrap) stack).panic = true;
    }

    @Final @Shadow public PlayerEntity player;
    @Inject(method = "dropSelectedItem(Z)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/player/PlayerInventory;getMainHandStack()Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void detach(boolean entireStack, CallbackInfoReturnable<ItemStack> cir, ItemStack stack){
        if (entireStack && stack instanceof ItemStackWrap && player.isSneaking()) ((ItemStackWrap) stack).detach = true;
    }

}
