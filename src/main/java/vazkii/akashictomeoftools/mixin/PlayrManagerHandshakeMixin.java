package vazkii.akashictomeoftools.mixin;

import net.minecraft.recipe.Recipe;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vazkii.akashictomeoftools.AttachmentRecipe;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(PlayerManager.class)
public class PlayrManagerHandshakeMixin {
    @ModifyArg(method = "onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/SynchronizeRecipesS2CPacket;<init>(Ljava/util/Collection;)V"))
    public Collection<Recipe<?>> removeAkashicRecipeSync(Collection<Recipe<?>> recipes){
        recipes = recipes.stream().filter((r) -> !(r instanceof AttachmentRecipe)).collect(Collectors.toSet());
        return recipes;
    }
}
