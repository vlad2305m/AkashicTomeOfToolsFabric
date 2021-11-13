package vazkii.akashictomeoftools.mixin;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.akashictomeoftools.ItemStackWrap;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntity_CraftKeepMixin {
    @Redirect(method = "craft(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/collection/DefaultedList;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private static <E> E keepTome(DefaultedList<E> defaultedList, int index, E element){
        E stack = defaultedList.get(index);
        if ((stack instanceof ItemStackWrap tome) && tome.notself) {
            tome.setContent((ItemStack) element); return stack;
        }
        return defaultedList.set(index, element);
    }
}
