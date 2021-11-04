package vazkii.akashictomeoftools.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import java.util.List;
import java.util.function.Consumer;

public class ConfigManager {
    private static ConfigHolder<AkashicTomeOfToolsConfig> holder;
    public static final Consumer<AkashicTomeOfToolsConfig> DEFAULT = (i) -> {
        i.whitelistedNames = List.of(
                "book.*",
                ".*tome.*",
                ".*lexicon",
                ".*nomicon",
                ".*manual.*",
                ".*knowledge.*",
                ".*pedia",
                ".*compendium.*",
                ".*guide.*",
                ".*codex.*",
                ".*journal.*");
        i.blacklistedUseItems = List.of(
                "item.minecraft..*_bucket",
                "item.minecraft.potion",
                "item.minecraft.writable_book",
                "item.minecraft.beetroot_soup",
                "item.minecraft.honey_bottle",
                "item.minecraft.mushroom_stew",
                "item.minecraft.rabbit_stew",
                "item.minecraft.suspicious_stew");
        i.whitelistedItems = List.of("item.minecraft.written_book");
        i.blacklistedMods = List.of();
    };

    public static void registerAutoConfig() {
        if (holder != null) {
            throw new IllegalStateException("Configuration already registered");
        }

        holder = AutoConfig.register(AkashicTomeOfToolsConfig.class, JanksonConfigSerializer::new);
        if (holder.getConfig().whitelistedItems == null) DEFAULT.accept(holder.getConfig());
        holder.save();
    }

    public static AkashicTomeOfToolsConfig getConfig() {
        if (holder == null) {
            return new AkashicTomeOfToolsConfig();
        }

        return holder.getConfig();
    }

    public static void load() {
        if (holder == null) {
            registerAutoConfig();
        }

        holder.load();
    }

    public static void save() {
        if (holder == null) {
            registerAutoConfig();
        }

        holder.save();
    }
}
