package vazkii.akashictomeoftools;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import vazkii.akashictomeoftools.config.ConfigManager;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ItemStackWrap extends ItemStack {

    public static final String ITEMS_KEY = "AkashicItems";
    public static final String SELECTED_KEY = "SelectedPos";
    public static final String ACTUAL_TOME_KEY = "AkashicTome";
    public static final String DUMMY_KEY = "AkashicDummy";
    public boolean notself = false;
    private boolean nbtNotself = true;
    private ItemStack content = null;
    private boolean tome = false;

    public ItemStackWrap(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<NbtCompound> optionalNbtCompound) {
        super(AkashicTome.TOME_ITEM, 1);
        if(optionalNbtCompound.isEmpty()||!optionalNbtCompound.get().contains("tag", 10)) {setSelectedSlot(-1); prepareContent();}
        else {
            NbtCompound compound = optionalNbtCompound.get().getCompound("tag");
            if (!compound.contains(ACTUAL_TOME_KEY)) {
                super.setNbt(compound);
                prepareContent();
            }
            else {
                NbtCompound nestedParent = compound;
                NbtCompound nestedParent1 = nestedParent.getCompound(ACTUAL_TOME_KEY);
                NbtCompound nested = nestedParent1.getCompound("tag");
                while (nested.contains(ACTUAL_TOME_KEY)) {
                    nestedParent = nested;
                    nestedParent1 = nestedParent.getCompound(ACTUAL_TOME_KEY);
                    nested = nestedParent1.getCompound("tag");
                }
                super.setNbt(nested);
                nestedParent.remove(ACTUAL_TOME_KEY);
                if (nestedParent.isEmpty()) nestedParent1.remove("tag");
                if (optionalNbtCompound.get().getCompound("tag").isEmpty()) optionalNbtCompound.get().remove("tag");
                prepareContent(optionalNbtCompound.get());
            }
        }
    }

    public static ItemStack tryConvert(ItemStack stack){
        if (!(stack instanceof ItemStackWrap tome)) {
            if (stack.isOf(AkashicTome.TOME_ITEM)) {
                stack = new ItemStackWrap(Optional.of(stack.writeNbt(new NbtCompound())));
            }
        } else {
            if (tome.notself) { ItemStack test = tryConvert(tome.getContent()); if (test != tome.getContent()) tome.setContent(test);}
        }
        return stack;
    }

    private void prepareContent(){
        NbtCompound nbt = super.getNbt();
        if (nbt == null || !nbt.contains(SELECTED_KEY)) { setSelectedSlot(-1); return;}
        int pos = nbt.getInt(SELECTED_KEY);
        if (pos == -1) { notself = false; content = null; tome = false; }
        else {
            NbtList nbtList = nbt.getList(ITEMS_KEY, 10);
            if (nbtList.size() <= pos || pos < 0) {
                setSelectedSlot(-1);
                return;
            }
            if (((NbtCompound)((NbtList) Objects.requireNonNull(nbt.get(ITEMS_KEY))).get(nbt.getInt(SELECTED_KEY))).contains(DUMMY_KEY)) {saveSelectedStack();}
            content = ItemStack.fromNbt((NbtCompound) nbtList.get(pos));
            notself = true;
            tome = content instanceof ItemStackWrap;
        }
        updateEmpty();
    }

    private void prepareContent(NbtCompound compound){
        NbtCompound nbt = super.getNbt();
        if (nbt == null) return;
        content = ItemStack.fromNbt(compound);
        notself = true;
        tome = content instanceof ItemStackWrap;
        if (((NbtCompound)((NbtList) Objects.requireNonNull(nbt.get(ITEMS_KEY))).get(nbt.getInt(SELECTED_KEY))).contains(DUMMY_KEY)) saveSelectedStack();
        else prepareContent();
        updateEmpty();
    }

    public void addToTome(ItemStack stack) {
        if (!stack.isEmpty()) {
            NbtCompound nbtCompound = this.getOrCreateNbt();
            if (!nbtCompound.contains(ITEMS_KEY)) {
                nbtCompound.put(ITEMS_KEY, new NbtList());
            }
            NbtList nbtList = nbtCompound.getList(ITEMS_KEY, 10);

            NbtCompound nbtCompound3 = new NbtCompound();
            stack.writeNbt(nbtCompound3);
            if (ConfigManager.getConfig().addToFront)
            nbtList.add(0, nbtCompound3);
            else nbtList.add(nbtCompound3);
        }
    }

    private void updateEmpty() {
        if (!notself||content==null) return;
        if (content.isEmpty()) removeSelectedStack();
    }

    private void setSelectedSlot(int pos) {
        NbtCompound nbt = super.getOrCreateNbt();
        nbt.putInt(SELECTED_KEY, pos);
        prepareContent();
    }

    public void removeSelectedStack() {
        NbtCompound nbt = super.getNbt();
        if (nbt == null || !nbt.contains(ITEMS_KEY) || !nbt.contains(SELECTED_KEY)) return;
        int pos = nbt.getInt(SELECTED_KEY);
        NbtList nbtList = nbt.getList(ITEMS_KEY, 10);
        if (pos < 0 || pos >= nbtList.size()) return;
        nbtList.remove(pos);
        content = null; tome = false;
        setSelectedSlot(-1);
    }

    public void changeSelectedStack(int pos) {
        saveSelectedStack();
        setSelectedSlot(pos);
    }

    public void saveSelectedStack() {
        if (!notself) return;
        NbtCompound nbt = super.getNbt();
        if (nbt == null || !nbt.contains(ITEMS_KEY) || !nbt.contains(SELECTED_KEY)) return;
        NbtList nbtList = nbt.getList(ITEMS_KEY, 10);
        int pos = nbt.getInt(SELECTED_KEY);
        if (pos < 0 || pos >= nbtList.size()) return;
        NbtCompound sel = content.writeNbt(new NbtCompound());
        nbtList.set(pos, sel);
    }

    private void dummySelectedStack() {
        if (!notself) return;
        NbtCompound nbt = super.getNbt();
        if (nbt == null || !nbt.contains(ITEMS_KEY) || !nbt.contains(SELECTED_KEY)) return;
        NbtList nbtList = nbt.getList(ITEMS_KEY, 10);
        int pos = nbt.getInt(SELECTED_KEY);
        if (pos < 0 || pos >= nbtList.size()) return;
        NbtCompound sel = new NbtCompound(){{putBoolean(DUMMY_KEY, true);}};
        nbtList.set(pos, sel);
    }

    private void unmorph() {
        if (tome && ((ItemStackWrap) content).notself) ((ItemStackWrap) content).unmorph();
        else {
            changeSelectedStack(-1);
        }
    }

    public synchronized void morph(int pos) {
        if (pos == -1) unmorph();
        else if (tome) ((ItemStackWrap) content).morph(pos);
        else {
            if (pos > -2)
             changeSelectedStack(pos);
        }
    }

    public boolean setContent(ItemStack stack) {
        if (!notself) return false;
        if (!tome || !((ItemStackWrap) content).setContent(stack)) content = stack;
        return true;
    }

    public ItemStack getContent() {
        if (!notself) return this;
        if (tome) return ((ItemStackWrap) content).getContent();
        else return content;
    }

    //below: modified methods ======================================================

    public boolean detach = false;
    public ItemStack split(int amount) {
        if (amount <= 0) return ItemStack.EMPTY;
        if (!notself || amount >= getContent().getCount()) {
            if (detach && notself && content != null) {
                if (content instanceof ItemStackWrap wrap) wrap.detach = true;
                ItemStack itemStack = this.content.split(amount);
                detach = false;
                updateEmpty();
                return itemStack;
            }
            ItemStack itemStack = this.copy();
            notself = false; content = null; tome = false;
            super.decrement(1);
            return itemStack;
        }
        ItemStack itemStack = getContent().copy();
        itemStack.setCount(amount);
        getContent().decrement(amount);
        return itemStack;
    }

    public boolean isEmpty() {
        updateEmpty();
        return !notself ? super.isEmpty() : content.isEmpty();
    }

    /**
     * Writes the serialized item stack into the given {@link NbtCompound}.
     *
     * @return the written NBT compound
     * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
     *
     * @param nbt the NBT compound to write to
     */
    public synchronized NbtCompound writeNbt(NbtCompound nbt) {
        if (!notself) return super.writeNbt(nbt);

        content.writeNbt(nbt);
        if (!nbt.contains("tag", 10)) nbt.put("tag", new NbtCompound());
        NbtCompound nbt2 = nbt.getCompound("tag");

        dummySelectedStack();
        nbtNotself = false;
        NbtCompound tomeNbt = super.writeNbt(new NbtCompound());
        nbtNotself = true;
        if (!nbt2.contains(ACTUAL_TOME_KEY)) nbt2.put(ACTUAL_TOME_KEY, tomeNbt);
        else {
            NbtCompound nested = nbt2;
            while (nested.contains(ACTUAL_TOME_KEY)) {
                nested = nested.getCompound(ACTUAL_TOME_KEY).getCompound("tag");
            }
            nested.put(ACTUAL_TOME_KEY, tomeNbt);
        }
        return nbt;
    }

    /**
     * Creates and returns a copy of this item stack.
     */
    public ItemStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            ItemStack itemStack = new ItemStackWrap(Optional.ofNullable(this.writeNbt(new NbtCompound())));
            itemStack.setBobbingAnimationTime(this.getBobbingAnimationTime());
            if (panic) setSelectedSlot(-1);
            return itemStack;
        }
    }

    public String toString() {
        return "Akashic Tome"+ (!notself ? "" : (" of " + content.toString()));
    }

    public void onItemEntityDestroyed(ItemEntity entity) {
        saveSelectedStack();
        NbtCompound nbtCompound = super.getNbt();
        if (nbtCompound != null) {
            NbtList nbtList = nbtCompound.getList(ITEMS_KEY, 10);
            Stream<NbtElement> stream = nbtList.stream();
            Objects.requireNonNull(NbtCompound.class);
            ItemUsage.spawnItemContents(entity, stream.map(NbtCompound.class::cast).map(ItemStack::fromNbt));
        }
    }

    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        List<Text> ls;
        if (notself) {
            ls = content.getTooltip(player, context);
            Text WATERMARK = ((MutableText)AkashicTome.TOME_ITEM.getName()).formatted(Formatting.AQUA);
            if (!WATERMARK.equals(ls.get(ls.size() - 1))) ls.add(WATERMARK);
        }
        else {
            ls = super.getTooltip(player, context);
            NbtCompound nbt = super.getNbt();
            if (nbt == null || !nbt.contains(ITEMS_KEY) || !nbt.contains(SELECTED_KEY)) return ls;
            NbtList list = nbt.getList(ITEMS_KEY, 10);
            if (list.size() == 0) return ls;
            ls.add(Text.translatable("akashictome.tooltip_count", list.size()));
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                if (!ConfigManager.getConfig().bundleTooltip && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                    list.stream().map(NbtCompound.class::cast).forEach((tag) -> {
                        if(tag.contains("id")) ls.add(((MutableText)Text.of(" - ")).append(Registries.ITEM.get(new Identifier(tag.getString("id"))).getName()));
                    });
                }
                else {
                    ls.add(Text.translatable("akashictome.tooltip_hint").formatted(Formatting.GRAY));
                }
            }
            else ls.add(Text.translatable("akashictome.tooltip_hint").formatted(Formatting.RED));
        }
        return ls;
    }

    public Optional<TooltipData> getTooltipData() {
        if (notself) return content.getTooltipData();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            if (ConfigManager.getConfig().bundleTooltip && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                DefaultedList<ItemStack> defaultedList = DefaultedList.of();
                defaultedList.add(ItemStack.EMPTY);
                saveSelectedStack();
                NbtCompound nbtCompound = super.getNbt();
                if (nbtCompound != null) {
                    NbtList nbtList = nbtCompound.getList(ITEMS_KEY, 10);
                    Stream<NbtElement> stream = nbtList.stream();
                    Objects.requireNonNull(defaultedList);
                    Objects.requireNonNull(NbtCompound.class);
                    stream.map(NbtCompound.class::cast).map(ItemStack::fromNbt).forEach(defaultedList::add);
                    return Optional.of(new BundleTooltipData(defaultedList, 6));
                }
            }
        }
        return Optional.empty();
    }

    public ItemStack finishUsing(World world, LivingEntity user) {
        if (notself) {
            content = this.getItem().finishUsing(content, world, user);
            updateEmpty();
            return this;
        }
        return this.getItem().finishUsing(this, world, user);
    }

    //lightly modified:


    public ItemStack setCustomName(@Nullable Text name) {
        if (!notself) super.setCustomName(name); else content.setCustomName(name);
        return this;
    }

    public boolean isInFrame() {
        return super.isInFrame();
    }

    public void setHolder(@Nullable Entity holder) {
        super.setHolder(holder);
        if (notself) content.setHolder(holder);
    }

    @Nullable
    public ItemFrameEntity getFrame() {
        return super.getFrame();
    }

    @Nullable
    public Entity getHolder() {
        return super.getHolder();
    }

    public boolean panic = false;

    public boolean isDamaged() {
        if (panic) return true;
        return !notself ? super.isDamaged() : content.isDamaged();
    }



    //below: parsed methods ========================================================


    public Item getItem() {
        return (!notself || this.content == null || !nbtNotself) ? super.getItem() : content.getItem();
    }

    public boolean isIn(TagKey<Item> tag) {
        return !notself ? super.isIn(tag) : content.isIn(tag);
    }

    public boolean isOf(Item item) {
        return !notself ? super.isOf(item) : content.isOf(item);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        if (checkUseBlacklist()) return ActionResult.PASS;
        if (this.getItem() instanceof MusicDiscItem) panic = true;
        if(!notself) return super.useOnBlock(context); else { ActionResult ret = content.useOnBlock(context); panic = false; return ret; }
    }

    public float getMiningSpeedMultiplier(BlockState state) {
        return !notself ? super.getMiningSpeedMultiplier(state) : content.getMiningSpeedMultiplier(state);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (checkUseBlacklist()) return TypedActionResult.pass(user.getStackInHand(hand));
        return !notself ? super.use(world, user, hand) : content.use(world, user, hand);
    }

    public int getMaxCount() {
        return !notself ? super.getMaxCount() : content.getMaxCount();
    }

    public boolean isStackable() {
        return !notself ? super.isStackable() : content.isStackable();
    }

    public boolean isDamageable() {
        return !notself ? super.isDamageable() : content.isDamageable();
    }

    public int getDamage() {
        return !notself ? super.getDamage() : content.getDamage();
    }

    public void setDamage(int damage) {
        if (!notself) super.setDamage(damage); else content.setDamage(damage);
    }

    public int getMaxDamage() {
        return !notself ? super.getMaxDamage() : content.getMaxDamage();
    }

    public boolean isItemBarVisible() {
        return !notself ? super.isItemBarVisible() : content.isItemBarVisible();
    }

    public int getItemBarStep() {
        return !notself ? super.getItemBarStep() : content.getItemBarStep();
    }

    public int getItemBarColor() {
        return !notself ? super.getItemBarColor() : content.getItemBarColor();
    }

    public boolean onStackClicked(Slot slot, ClickType clickType, PlayerEntity player) {
        return !notself ? super.onStackClicked(slot, clickType, player) : content.onStackClicked(slot, clickType, player);
    }

    public boolean onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        return !notself ? super.onClicked(stack, slot, clickType, player, cursorStackReference) : content.onClicked(stack, slot, clickType, player, cursorStackReference);
    }

    public void postHit(LivingEntity target, PlayerEntity attacker) {
        if (!notself) super.postHit(target, attacker); else content.postHit(target, attacker);
    }

    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {
        if (!notself) super.postMine(world, state, pos, miner); else content.postMine(world, state, pos, miner);
    }

    /**
     * Determines whether this item can be used as a suitable tool for mining the specified block.
     */
    public boolean isSuitableFor(BlockState state) {
        return !notself ? super.isSuitableFor(state) : content.isSuitableFor(state);
    }

    public ActionResult useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand) {
        if (checkUseBlacklist()) return ActionResult.PASS;
        return !notself ? super.useOnEntity(user, entity, hand) : content.useOnEntity(user, entity, hand);
    }

    public String getTranslationKey() {
        return !notself ? super.getTranslationKey() : content.getTranslationKey();
    }

    public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
        if (!notself) super.inventoryTick(world, entity, slot, selected); else content.inventoryTick(world, entity, slot, selected);
    }

    public void onCraft(World world, PlayerEntity player, int amount) {
        if (!notself) super.onCraft(world, player, amount); else content.onCraft(world, player, amount);
    }

    public int getMaxUseTime() {
        if (checkUseBlacklist()) return 0;
        return !notself ? super.getMaxUseTime() : content.getMaxUseTime();
    }

    public UseAction getUseAction() {
        if (checkUseBlacklist()) return UseAction.NONE;
        return !notself ? super.getUseAction() : content.getUseAction();
    }

    public void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks) {
        this.getItem().onStoppedUsing(this, world, user, remainingUseTicks);
    }

    public boolean isUsedOnRelease() {
        return !notself ? super.isUsedOnRelease() : content.isUsedOnRelease();
    }

    public boolean hasNbt() {
        return !notself ? super.hasNbt() : content.hasNbt();
    }

    @Nullable
    public NbtCompound getNbt() {
        return !notself ? super.getNbt() : content.getNbt();
    }

    public NbtCompound getOrCreateNbt() {
        return !notself ? super.getOrCreateNbt() : content.getOrCreateNbt();
    }

    public NbtCompound getOrCreateSubNbt(String key) {
        return !notself ? super.getOrCreateSubNbt(key) : content.getOrCreateSubNbt(key);
    }

    @Nullable
    public NbtCompound getSubNbt(String key) {
        return !notself ? super.getSubNbt(key) : content.getSubNbt(key);
    }


    public void removeSubNbt(String key) {
        if (!notself) super.removeSubNbt(key); else content.removeSubNbt(key);
    }

    public NbtList getEnchantments() {
        return !notself ? super.getEnchantments() : content.getEnchantments();
    }

    public void setNbt(@Nullable NbtCompound nbt) {
        if (!notself) super.setNbt(nbt); else content.setNbt(nbt);
    }

    public Text getName() {
        return !notself ? super.getName() : content.getName();
    }

    public void removeCustomName() {
        if (!notself) super.removeCustomName(); else content.removeCustomName();
    }

    public boolean hasCustomName() {
        return !notself ? super.hasCustomName() : content.hasCustomName();
    }

    public void addHideFlag(ItemStack.TooltipSection tooltipSection) {
        if (!notself) super.addHideFlag(tooltipSection); else content.addHideFlag(tooltipSection);
    }

    public boolean hasGlint() {
        return !notself ? super.hasGlint() : content.hasGlint();
    }

    public Rarity getRarity() {
        return !notself ? super.getRarity() : content.getRarity();
    }

    public boolean isEnchantable() {
        return !notself ? super.isEnchantable() : content.isEnchantable();
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        if (!notself) super.addEnchantment(enchantment, level); else content.addEnchantment(enchantment, level);
    }

    public boolean hasEnchantments() {
        return !notself ? super.hasEnchantments() : content.hasEnchantments();
    }

    /**
     * Sets the given NBT element in the item stack's custom NBT at the specified key.
     *
     * @see <a href="#nbt-operations">Item Stack NBT Operations</a>
     *
     * @param key the key where to put the given {@link NbtElement}
     * @param element the NBT element to put
     */
    public void setSubNbt(String key, NbtElement element) {
        if (!notself) super.setSubNbt(key, element); else content.setSubNbt(key, element);
    }

    public int getRepairCost() {
        return !notself ? super.getRepairCost() : content.getRepairCost();
    }

    public void setRepairCost(int repairCost) {
        if (!notself) super.setRepairCost(repairCost); else content.setRepairCost(repairCost);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return !notself ? super.getAttributeModifiers(slot) : content.getAttributeModifiers(slot);
    }

    public void addAttributeModifier(EntityAttribute attribute, EntityAttributeModifier modifier, @Nullable EquipmentSlot slot) {
        if (!notself) super.addAttributeModifier(attribute, modifier, slot); else content.addAttributeModifier(attribute, modifier, slot);
    }

    public Text toHoverableText() {
        return !notself ? super.toHoverableText() : content.toHoverableText();
    }

    public boolean canDestroy(Registry<Block> tagManager, CachedBlockPosition pos) {
        return !notself ? super.canDestroy(tagManager, pos) : content.canDestroy(tagManager, pos);
    }

    public boolean canPlaceOn(Registry<Block> tagManager, CachedBlockPosition pos) {
        return !notself ? super.canPlaceOn(tagManager, pos) : content.canPlaceOn(tagManager, pos);
    }

    public int getBobbingAnimationTime() {
        return !notself ? super.getBobbingAnimationTime() : content.getBobbingAnimationTime();
    }

    public void setBobbingAnimationTime(int cooldown) {
        if (!notself) super.setBobbingAnimationTime(cooldown); else content.setBobbingAnimationTime(cooldown);
    }

    /**
     * {@return the count of items in this item stack}
     */
    public int getCount() {
        return !notself ? super.getCount() : content.getCount();
    }

    public void setCount(int count) {
        if (panic || !notself) super.setCount(count); else content.setCount(count);
    }

    /**
     * Increments the count of items in this item stack.
     *
     * @param amount the amount to increment
     */
    public void increment(int amount) {
        if (!notself) super.increment(amount); else content.increment(amount);
    }

    /**
     * Decrements the count of items in this item stack.
     *
     * @param amount the amount to decrement
     */
    public void decrement(int amount) {
        if (!notself) super.decrement(amount); else content.decrement(amount);
    }

    public void usageTick(World world, LivingEntity user, int remainingUseTicks) {
        if (!notself) super.usageTick(world, user, remainingUseTicks); else content.usageTick(world, user, remainingUseTicks);
    }

    public boolean isFood() {
        return !notself ? super.isFood() : content.isFood();
    }

    public SoundEvent getDrinkSound() {
        return !notself ? super.getDrinkSound() : content.getDrinkSound();
    }

    public SoundEvent getEatSound() {
        return !notself ? super.getEatSound() : content.getEatSound();
    }

    public boolean checkUseBlacklist() {
        if (!notself || content == null) return false;
        final String key = content.getItem().getTranslationKey();
        for ( String s : ConfigManager.getConfig().blacklistedUseItems ) {
            if (Pattern.matches(s, key)) return true;
        }
        return false;
    }

}
