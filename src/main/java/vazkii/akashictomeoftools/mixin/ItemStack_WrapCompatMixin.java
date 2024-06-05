package vazkii.akashictomeoftools.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.akashictomeoftools.ItemStackWrap;

import java.util.Objects;

import static net.minecraft.item.ItemStack.canCombine;

@Mixin(ItemStack.class)
public class ItemStack_WrapCompatMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean areEqual(ItemStack left, ItemStack right) {
        if (left instanceof ItemStackWrap wrap) {
            if (!(right instanceof ItemStackWrap)) return false;
            return !wrap.notself ? areEqualOriginal(wrap, right) : wrap.getContent() !=null && ((ItemStackWrap) right).getContent() !=null
                    && areEqual(wrap.getContent(), ((ItemStackWrap) right).getContent());
        } else if (right instanceof ItemStackWrap) {
            return areEqual(right, left);
        } else {
            return areEqualOriginal(left, right);
        }
    }

    private static boolean areEqualOriginal(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        } else {
            return left.getCount() != right.getCount() ? false : canCombine(left, right);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        if (stack1 instanceof ItemStackWrap wrap) {
            return !wrap.notself ? canCombineOriginal(wrap, stack2) : canCombine(wrap, stack2);
        } else {
            return canCombineOriginal(stack1, stack2);
        }
    }

    private static boolean canCombineOriginal(ItemStack stack, ItemStack otherStack) {
        if (!stack.isOf(otherStack.getItem())) {
            return false;
        } else {
            return stack.isEmpty() && otherStack.isEmpty() || Objects.equals(stack.getNbt(), otherStack.getNbt());
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static boolean areItemsEqual(ItemStack left, ItemStack right) {
        if (left instanceof ItemStackWrap wrap) {
            return !wrap.notself ? areItemsEqualOriginal(left, right) : areItemsEqual(wrap.getContent(), right);
        } else if (right instanceof ItemStackWrap) {
            return areItemsEqual(right, left);
        } else {
            return areItemsEqualOriginal(left, right);
        }
    }

    private static boolean areItemsEqualOriginal(ItemStack left, ItemStack right) {
        return left.isOf(right.getItem());
    }
}
