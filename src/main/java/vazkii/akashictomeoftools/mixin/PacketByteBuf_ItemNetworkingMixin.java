package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.akashictomeoftools.ItemStackWrap;

import static vazkii.akashictomeoftools.AkashicTome.TOME_ITEM;

@Mixin(PacketByteBuf.class)
public class PacketByteBuf_ItemNetworkingMixin {
    private static final String TOME_TAG_KEY = "AkashicTomeNBT";
    private boolean tome = false;
    private NbtCompound nbt1 = null;
    @Inject(method = "writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    public void getTome(ItemStack stack, CallbackInfoReturnable<PacketByteBuf> cir){
        tome = stack instanceof ItemStackWrap;
        if (tome) nbt1 = stack.writeNbt(new NbtCompound());
    }
    @Redirect(method = "writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;isDamageable()Z"))
    public boolean duckDamageable(Item item){
        if (tome) { return true; } return item.isDamageable();
    }
    @ModifyVariable(method = "writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getNbt()Lnet/minecraft/nbt/NbtCompound;", shift = At.Shift.AFTER), ordinal = 0)
    public NbtCompound write(NbtCompound nbtCompound) {
        if (tome && nbt1 != null) {
            nbtCompound = new NbtCompound();
            nbtCompound.put(TOME_TAG_KEY, nbt1);
        }
        return nbtCompound;
    }
    @SuppressWarnings("unchecked")
    @ModifyArg(method = "writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeRegistryValue(Lnet/minecraft/util/collection/IndexedIterable;Ljava/lang/Object;)V"), index = 1)
    public <T> T spoofItem(T item) {
        if (tome && nbt1 != null && item.equals(TOME_ITEM)) {
            item = (T)Items.BOOK;
        }
        return item;
    }



    private NbtCompound nbtt = null;
    @ModifyArg(method = "readItemStack()Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    public NbtCompound stealNbt(NbtCompound compound){
        nbtt = compound;
        return compound;
    }
    @Inject(method = "readItemStack()Lnet/minecraft/item/ItemStack;", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void read(CallbackInfoReturnable<ItemStack> cir){
        if(nbtt != null && nbtt.contains(TOME_TAG_KEY, 10))
            cir.setReturnValue(ItemStack.fromNbt(nbtt.getCompound(TOME_TAG_KEY)));
    }
}
