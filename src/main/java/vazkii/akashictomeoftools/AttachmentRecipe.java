package vazkii.akashictomeoftools;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import vazkii.akashictomeoftools.config.AkashicTomeOfToolsConfig;
import vazkii.akashictomeoftools.config.ConfigManager;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class AttachmentRecipe extends SpecialCraftingRecipe {
	static private long lastCalledTime = 0;
	static private long lastCalledTimeClient = 0;

	public AttachmentRecipe(Identifier id, CraftingRecipeCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(RecipeInputInventory inventory, World world) {
		long dT;
		if (world.isClient()) {
			dT = System.currentTimeMillis() - lastCalledTimeClient;
			lastCalledTimeClient += dT;
		} else {
			dT = System.currentTimeMillis() - lastCalledTime;
			lastCalledTime += dT;
		}
		if (dT < 20) return false;
		boolean foundItem = false;
		boolean foundTome = false;

		ItemStack item = null;
		ItemStack tome = null;
		for(int j = 0; j < inventory.size(); ++j) {
			ItemStack itemStack = inventory.getStack(j);
			if (!itemStack.isEmpty()) {
				if (itemStack.isOf(AkashicTome.TOME_ITEM)) {
					if (foundTome) {
						if (foundItem) {
							return false;
						}
						foundItem = true;
						item = itemStack;
					} else {
						foundTome = true;
						tome = itemStack;
					}
				} else if (!(itemStack instanceof ItemStackWrap)) {
					if (foundItem) {
						return false;
					}
					foundItem = true;
					item = itemStack;
				} else return false;
			}
		}

		if (foundItem && foundTome && checkItem(item)) {
			if (tome instanceof ItemStackWrap wrap) {
				wrap.addToTome(item);
				item.setCount(0);
				return true;
			}
			return false;
		}

		if (foundTome && !foundItem){
			return !(tome instanceof ItemStackWrap);
		}

		return false;
	}

	@Override
	public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
		ItemStack item = ItemStack.EMPTY;
		ItemStack tome = ItemStack.EMPTY;

		boolean foundItem = false;
		boolean foundTome = false;

		for(int j = 0; j < inventory.size(); ++j) {
			ItemStack itemStack = inventory.getStack(j);
			if (!itemStack.isEmpty()) {
				if (itemStack.isOf(AkashicTome.TOME_ITEM)) {
					if (foundTome) {
						if (foundItem) {
							return ItemStack.EMPTY;
						}
						foundItem = true;
						item = itemStack;
					} else {
						foundTome = true;
						tome = itemStack;
					}
				} else if (!(itemStack instanceof ItemStackWrap)) {
					if (foundItem) {
						return ItemStack.EMPTY;
					}
					foundItem = true;
					item = itemStack;
				} else return ItemStack.EMPTY;
			}
		}

		if (foundTome && !foundItem && !(tome instanceof ItemStackWrap)){
			return new ItemStackWrap(Optional.of(tome.writeNbt(new NbtCompound())));
		}

		ItemStack newTome;
		if (tome instanceof ItemStackWrap wrap) {
			wrap.addToTome(item);
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
