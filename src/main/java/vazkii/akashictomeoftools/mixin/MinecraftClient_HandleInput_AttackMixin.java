package vazkii.akashictomeoftools.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
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
import vazkii.akashictomeoftools.config.ConfigManager;

import java.util.Objects;
import java.util.regex.Pattern;

import static vazkii.akashictomeoftools.AkashicTome.t;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_HandleInput_AttackMixin {

    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public HitResult crosshairTarget;
    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "handleInputEvents()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z"))
    public void checkMorph(CallbackInfo ci) {
        if (crosshairTarget != null && crosshairTarget.getType() != HitResult.Type.MISS) {
            if (crosshairTarget.getType() == HitResult.Type.BLOCK && world != null
                    && !world.getBlockState(((BlockHitResult) crosshairTarget).getBlockPos()).isAir())
                t = System.currentTimeMillis();
            return;
        }
        unmorphHeldTome();
    }

    @Mixin(ClientPlayerInteractionManager.class) static class ClientPlayerInteractionManager_InstamineMixin {
        @Shadow private boolean breakingBlock;
    @Inject(method = "cancelBlockBreaking()V", at = @At(value = "HEAD"))
    public void checkPostBreakMorphBlockInstamined(CallbackInfo ci) {
        if (!breakingBlock) t = 0;
    }}

    @Inject(method = "handleBlockBreaking(Z)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;cancelBlockBreaking()V"))
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
        if (player.getPitch() != -90F) {
            String key = stack.getItem().getTranslationKey();
            boolean sendMessage = ConfigManager.getConfig().unmorphBlacklistMessage;
            for (String s : ConfigManager.getConfig().blacklistedUnmorphItems) {
                if (Pattern.matches(s, key)) {
                    if (sendMessage) player.sendMessage(Text.translatable("akashictomeoftools.message_special_unmorph"), true);
                    return;
                }
            }
        }
        ((ItemStackWrap) ItemStackWrap.tryConvert(stack)).morph(-1);
        ClientPlayNetworking.send(AkashicTome.AkashicChannel, new PacketByteBuf(PacketByteBufs.create().writeInt(-1)));
    }
}
