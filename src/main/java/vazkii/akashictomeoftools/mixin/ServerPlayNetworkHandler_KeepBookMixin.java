package vazkii.akashictomeoftools.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandler_KeepBookMixin {
    @Redirect(method = "addBook(Lnet/minecraft/server/filter/FilteredMessage;Ljava/util/List;I)V", at = @At( value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    public void keepBook(PlayerInventory playerInventory, int slot, ItemStack stack) {
        if ((playerInventory.getStack(slot) instanceof ItemStackWrap tome) && tome.notself) {
            tome.setContent(stack);
        }
        else {
            playerInventory.setStack(slot, stack);
        }
    }
}
