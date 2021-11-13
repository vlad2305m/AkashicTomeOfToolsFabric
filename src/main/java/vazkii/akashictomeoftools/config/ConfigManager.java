package vazkii.akashictomeoftools.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ConfigManager {
    private static ConfigHolder<AkashicTomeOfToolsConfig> holder;
    public static final Consumer<AkashicTomeOfToolsConfig> DEFAULT = (i) -> {
        i.hi = "Erase to reset";
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
                "item.minecraft.I fixed them for you");
        i.whitelistedItems = List.of("item.minecraft.written_book");
        i.blacklistedMods = List.of();
    };

    public static void registerAutoConfig() {
        if (holder != null) {
            throw new IllegalStateException("Configuration already registered");
        }

        holder = AutoConfig.register(AkashicTomeOfToolsConfig.class, JanksonConfigSerializer::new);
        if (holder.getConfig().hi == null || Objects.equals(holder.getConfig().hi, "")) DEFAULT.accept(holder.getConfig());
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
