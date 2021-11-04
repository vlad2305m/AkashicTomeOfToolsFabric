package vazkii.akashictomeoftools.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@Config(name = "akashic_tome")
@Config.Gui.Background("minecraft:textures/block/bookshelf.png")
public class AkashicTomeOfToolsConfig implements ConfigData {

    @Comment("""
            Filter priorities:
            !isTome > whiteID > unstackable > !blackMod > whiteName
            """)
    public String hi = "Hi!";

    @Comment("Put a tome in a tome? Like folders?..")
    public boolean allowAkashicTome = false;

    @Comment("Whitelisted id's (regex)")
    public List<String> whitelistedItems = List.of("item.minecraft.written_book");

    @Comment("Only allow unstackable")
    public boolean requireUnstackable = false;

    @Comment("Mods to exclude")
    public List<String> blacklistedMods = List.of();

    @Comment("Whitelisted names (regex) (lowerCase)")
    public List<String> whitelistedNames = List.of(
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

    @Comment("Enable all items")
    public boolean bypassWhitelist = true;

    @Comment("Use bundle-style tooltip instead")
    public boolean bundleTooltip = true;

    @Comment("Blacklisted id's (Use method) (regex)")
    public List<String> blacklistedUseItems = List.of(
            "item.minecraft..*_bucket",
            "item.minecraft.potion",
            "item.minecraft.writable_book",
            "item.minecraft.beetroot_soup",
            "item.minecraft.honey_bottle",
            "item.minecraft.mushroom_stew",
            "item.minecraft.rabbit_stew",
            "item.minecraft.suspicious_stew");

}