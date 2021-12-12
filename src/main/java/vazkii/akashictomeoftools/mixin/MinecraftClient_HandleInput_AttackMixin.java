package vazkii.akashictomeoftools.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.akashictomeoftools.AkashicTome;
import vazkii.akashictomeoftools.ItemStackWrap;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClient_HandleInput_AttackMixin {

    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public HitResult crosshairTarget;
    @Shadow @Nullable public ClientWorld world;

    private static long t = 0;

    @Inject(method = "handleInputEvents()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()V"))
    public void checkMorph(CallbackInfo ci) {
        if (crosshairTarget != null && crosshairTarget.getType() != HitResult.Type.MISS) {
            if (crosshairTarget.getType() == HitResult.Type.BLOCK && world != null
                    && !world.getBlockState(((BlockHitResult) crosshairTarget).getBlockPos()).isAir())
                t = System.currentTimeMillis();
            return;
        }
        unmorphHeldTome();
    }

    @Inject(method = "handleBlockBreaking(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;cancelBlockBreaking()V"))
    public void checkPostBreakMorph(boolean bl, CallbackInfo ci) {
        if (System.currentTimeMillis() - t <= 100 && !bl) { t = 0; unmorphHeldTome();}
    }

    public void unmorphHeldTome() {
        if (player == null || !player.isSneaking()) return;
        ItemStack stack = player.getMainHandStack();
        if (!(stack instanceof ItemStackWrap)&&(!stack.hasNbt()||!Objects.requireNonNull(stack.getNbt()).contains("tag", 10)||!stack.getNbt().getCompound("tag").contains(ItemStackWrap.ACTUAL_TOME_KEY)))
            stack = player.getOffHandStack();
        if (!(stack instanceof ItemStackWrap)&&(!stack.hasNbt()||!Objects.requireNonNull(stack.getNbt()).contains("tag", 10)||!stack.getNbt().getCompound("tag").contains(ItemStackWrap.ACTUAL_TOME_KEY)))
            return;
        ((ItemStackWrap) ItemStackWrap.tryConvert(stack)).unmorph();
        ClientPlayNetworking.send(AkashicTome.AkashicChannel, new PacketByteBuf(PacketByteBufs.create().writeInt(-1)));
    }
}
