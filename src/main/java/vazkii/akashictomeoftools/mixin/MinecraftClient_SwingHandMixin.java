package vazkii.akashictomeoftools.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.akashictomeoftools.AkashicTome;
import vazkii.akashictomeoftools.ItemStackWrap;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClient_SwingHandMixin {

    @Shadow @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "doAttack()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
    public void checkMorph(CallbackInfo ci) {
        if (player == null || !player.isSneaking()) return;
        ItemStack stack = player.getMainHandStack();
        if (!(stack instanceof ItemStackWrap)&&(!stack.hasNbt()||!stack.getNbt().contains("tag", 10)||!stack.getNbt().getCompound("tag").contains(ItemStackWrap.ACTUAL_TOME_KEY))) return;
        ((ItemStackWrap) ItemStackWrap.tryConvert(stack)).unmorph();
        ClientPlayNetworking.send(AkashicTome.AkashicChannel, new PacketByteBuf(PacketByteBufs.create().writeInt(-1)));
    }


}
