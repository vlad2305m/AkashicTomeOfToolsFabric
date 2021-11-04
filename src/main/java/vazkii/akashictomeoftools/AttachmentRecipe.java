package vazkii.akashictomeoftools;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.akashictomeoftools.config.AkashicTomeOfToolsConfig;
import vazkii.akashictomeoftools.config.ConfigManager;

import java.util.*;
import java.util.regex.Pattern;

public class AttachmentRecipe extends SpecialCraftingRecipe {
	public AttachmentRecipe(Identifier identifier) {
		super(identifier);
	}

	static private long lastCalledTime = 0;
	static private List<ItemStack> ciCache = new ArrayList<>();
	@Override
	public boolean matches(CraftingInventory craftingInventory, World world) {
		long dT = System.currentTimeMillis() - lastCalledTime;
		lastCalledTime += dT;
		List<ItemStack> cache2 = craftingInventory.stacks.delegate.stream().filter((i) -> !i.isEmpty()).toList();
		if (!ciCache.equals(cache2)) {
			ciCache = cache2;
			if (dT < 20) return false;
		}
		boolean foundItem = false;
		boolean foundTome = false;

		ItemStack item = null;
		for(int j = 0; j < craftingInventory.size(); ++j) {
			ItemStack itemStack = craftingInventory.getStack(j);
			if (!itemStack.isEmpty()) {
				if (itemStack.isOf(AkashicTome.TOME_ITEM)) {
					if (foundTome) {
						if (!itemStack.isEmpty() && foundItem) {
							return false;
						}
						foundItem = true;
						item = itemStack;
					} else foundTome = true;
				} else if (!(itemStack instanceof ItemStackWrap)) {
					if (!itemStack.isEmpty() && foundItem) {
						return false;
					}
					foundItem = true;
					item = itemStack;
				}
			}
		}

		if (foundItem && foundTome) {
			return checkItem(item);
		}

		return false;
	}

	public ItemStack craft(CraftingInventory craftingInventory) {
		ItemStack item = ItemStack.EMPTY;
		ItemStack tome = ItemStack.EMPTY;

		boolean foundItem = false;
		boolean foundTome = false;

		for(int j = 0; j < craftingInventory.size(); ++j) {
			ItemStack itemStack = craftingInventory.getStack(j);
			if (!itemStack.isEmpty()) {
				if (itemStack.isOf(AkashicTome.TOME_ITEM)) {
					if (foundTome) {
						if (!itemStack.isEmpty() && foundItem) {
							return ItemStack.EMPTY;
						}
						foundItem = true;
						item = itemStack;
					} else {
						foundTome = true;
						tome = itemStack;
					}
				} else if (!(itemStack instanceof ItemStackWrap)) {
					if (!itemStack.isEmpty() && foundItem) {
						return ItemStack.EMPTY;
					}
					foundItem = true;
					item = itemStack;
				}
			}
		}

		ItemStack newTome;
		if (tome instanceof ItemStackWrap) {
			newTome = tome;
			((ItemStackWrap) newTome).addToTome(item);
			item.setCount(0);
			newTome = ItemStack.EMPTY;
		}
		else {
			newTome = new ItemStackWrap(Optional.of(tome.writeNbt(new NbtCompound())));
			ItemStack newItem = item.copy();
			newItem.setCount(1);
			((ItemStackWrap) newTome).addToTome(newItem);
		}

		return newTome;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AkashicTome.ATTACHMENT_RECIPE_SERIALIZER;
	}

	private boolean checkItem(ItemStack item) {
		if (item == null) return false;
		AkashicTomeOfToolsConfig config = ConfigManager.getConfig();
		if (!config.allowAkashicTome && (item instanceof ItemStackWrap||item.isOf(AkashicTome.TOME_ITEM))) return false;

		final String key = item.getItem().getTranslationKey();
		for ( String s : config.whitelistedItems ) {
			if (Pattern.matches(s, key)) return true;
		}

		if ( config.requireUnstackable && ! (item.getMaxCount() == 1)) return false;

		final String mod = key.split("\\.")[1];
		for ( String s : config.blacklistedMods ) {
			if (Objects.equals(s, mod)) return false;
		}
		final String name = item.getName().getString().toLowerCase(Locale.ROOT);
		for ( String s : config.whitelistedNames ) {
			if (Pattern.matches(s, name)) return true;
		}

		return config.bypassWhitelist;
	}

}
