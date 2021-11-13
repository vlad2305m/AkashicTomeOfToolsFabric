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
    public String hi = "";

    @Comment("Put a tome in a tome? Like folders?..")
    public boolean allowAkashicTome = true;

    @Comment("Whitelisted id's (regex)")
    public List<String> whitelistedItems = null;

    @Comment("Only allow unstackable")
    public boolean requireUnstackable = false;

    @Comment("Mods to exclude")
    public List<String> blacklistedMods = null;

    @Comment("Whitelisted names (regex) (lowerCase)")
    public List<String> whitelistedNames = null;

    @Comment("Enable all items")
    public boolean bypassWhitelist = true;

    @Comment("Use bundle-style tooltip instead")
    public boolean bundleTooltip = true;

    @Comment("Blacklisted id's (Use method) (regex)")
    public List<String> blacklistedUseItems = null;

}