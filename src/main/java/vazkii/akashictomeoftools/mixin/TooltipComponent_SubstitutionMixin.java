package vazkii.akashictomeoftools.mixin;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.akashictomeoftools.client.BetterBundleTooltipComponent;

@Mixin(TooltipComponent.class)
public interface TooltipComponent_SubstitutionMixin {
    @Inject(method = "of(Lnet/minecraft/client/item/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", cancellable = true, at = @At("HEAD"))
    private static void ofB(TooltipData data, CallbackInfoReturnable<TooltipComponent> cir) {
        if (data instanceof BundleTooltipData bData && !bData.getInventory().isEmpty() && bData.getInventory().get(0).isEmpty())
            cir.setReturnValue(new BetterBundleTooltipComponent(bData));
    }
}
