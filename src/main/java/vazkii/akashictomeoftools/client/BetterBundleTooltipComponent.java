package vazkii.akashictomeoftools.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class BetterBundleTooltipComponent extends BundleTooltipComponent {
    public BetterBundleTooltipComponent(BundleTooltipData data) {
        super(data);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        int columns = this.getColumns();
        int rows = this.getRows();
        int i = 1;

        for(int l = 0; l < rows; ++l) {
            for(int m = 0; m < columns; ++m) {
                int n = x + m * 18 + 1;
                int o = y + l * 20 + 1;
                this.drawSlot(n, o, i++, false, context, textRenderer);
            }
        }

        this.drawOutline(x, y, columns, rows, context);
    }

    @Override
    public int getColumns() {
        return Math.min(super.occupancy, super.inventory.size()-1);
    }

    @Override
    public int getRows() {
        return (int)Math.ceil(((double)this.inventory.size()-1d) / (double)this.getColumns());
    }

}
