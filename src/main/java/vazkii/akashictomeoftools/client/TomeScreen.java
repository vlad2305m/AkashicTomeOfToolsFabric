package vazkii.akashictomeoftools.client;

import java.util.*;
import java.util.stream.Stream;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import vazkii.akashictomeoftools.AkashicTome;
import vazkii.akashictomeoftools.ItemStackWrap;
import vazkii.akashictomeoftools.config.ConfigManager;

@Environment(EnvType.CLIENT)
public class TomeScreen extends Screen {

	private static final Identifier BOOK_TEXTURE = new Identifier("akashictomeoftools:textures/models/book.png");
	private final BookModel BOOK_MODEL = new BookModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.BOOK));
	final ItemStackWrap tome;
	final Hand hand;
	final PlayerEntity player;

	public TomeScreen(ItemStackWrap tome, Hand hand, PlayerEntity player) {
		super(Text.literal(""));
		this.tome = tome;
		this.hand = hand;
		this.player = player;
	}

	private Integer selectedItem = null;

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(button == 0 && this.selectedItem != null) {
			tome.morph(selectedItem);
			ClientPlayNetworking.send(AkashicTome.AkashicChannel, new PacketByteBuf(PacketByteBufs.create().writeInt(selectedItem)));
			player.setStackInHand(hand, tome);
			super.close();
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (this.client == null) return;
		RenderSystem.disableDepthTest();
		int k = (int) this.client.getWindow().getScaleFactor();
		RenderSystem.viewport((this.width - 320) / 2 * k, (this.height - 240) / 2 * k, 320 * k, 240 * k);
		Matrix4f matrix4f = Matrix4f.translate(0F, -0.9F, 0.0F);
		matrix4f.multiply(Matrix4f.viewboxMatrix(90.0D, 1.3333334F, 9.0F, 80.0F));
		RenderSystem.backupProjectionMatrix();
		RenderSystem.setProjectionMatrix(matrix4f);



		matrixStack.push();
		MatrixStack.Entry matrixstack$entry = matrixStack.peek();
		matrixstack$entry.getPositionMatrix().loadIdentity();
		matrixstack$entry.getNormalMatrix().loadIdentity();
		matrixStack.translate(0.0D, 3.3F, 1984.0D);
		float scale = 20F;
		matrixStack.scale(scale, scale, scale);
		matrixStack.multiply(new Quaternion(Vec3f.POSITIVE_Z, 180.0F, true));
		matrixStack.multiply(new Quaternion(Vec3f.POSITIVE_X, 50.0F, true));
		matrixStack.multiply(new Quaternion(Vec3f.POSITIVE_Y, 4F * 90F - 90F, true));

		BOOK_MODEL.setPageAngles(0.0F, 1F, 0F, 1F);
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
		VertexConsumer vertexConsumer = immediate.getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
		this.BOOK_MODEL.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		immediate.draw();
		matrixStack.pop();
		RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
		RenderSystem.restoreProjectionMatrix();
		DiffuseLighting.enableGuiDepthLighting();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);



		List<ItemStack> stacks = new ArrayList<>();

		if (tome.hasNbt() && Objects.requireNonNull(tome.getNbt()).contains(ItemStackWrap.ITEMS_KEY)) {
			NbtCompound data = tome.getNbt();
			NbtList nbtList = data.getList(ItemStackWrap.ITEMS_KEY, 10);

			for (int s = 0; s < nbtList.size(); s++) {
				NbtCompound cmp = nbtList.getCompound(s);
				if (cmp != null) {
					ItemStack modStack = ItemStack.fromNbt(cmp);
					stacks.add(modStack);
				}
			}
		}

		int centerX = this.client.getWindow().getScaledWidth() / 2;
		int centerY = this.client.getWindow().getScaledHeight() / 2;

		int amountPerRow = 6;
		int rows = (stacks.size()-1) / amountPerRow + 1;
		int iconSize = 20;

		int startX = centerX - (amountPerRow * iconSize) / 2;
		int startY = centerY - (rows * iconSize) + 45;

		int padding = 4;
		int extra = 2;
		fill(matrixStack, startX - padding, startY - padding, startX + iconSize * amountPerRow + padding, startY + iconSize * rows + padding, 0x22000000);
		fill(matrixStack, startX - padding - extra, startY - padding - extra, startX + iconSize * amountPerRow + padding + extra, startY + iconSize * rows + padding + extra, 0x22000000);


		ItemStack tooltipStack = ItemStack.EMPTY;

		selectedItem = null;
		if (!stacks.isEmpty()) {
			for (int i = 0; i < stacks.size(); i++) {
				int x = startX + (i % amountPerRow) * iconSize;
				int y = startY + (i / amountPerRow) * iconSize;
				ItemStack stack = stacks.get(i);

				if (mouseX > x && mouseY > y && mouseX <= (x + 16) && mouseY <= (y + 16)) {
					tooltipStack = stack;
					selectedItem = i;
					y -= 2;
				}

				drawItem(stack, x, y, String.valueOf(stack.getCount()));
			}
		}

		if (!tooltipStack.isEmpty()) {
			String tempDefinedMod = new Identifier(tooltipStack.getItem().getTranslationKey()).getNamespace();
			String mod = "§7§o" + tempDefinedMod;

			List<Text> tooltipList = tooltipStack.getTooltip(null, () -> false);
			tooltipList.add(Text.literal(mod));
			Optional<TooltipData> tooltipData = tooltipStack.getTooltipData();
			if (ConfigManager.getConfig().bundleTooltipShulkers && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) && tooltipStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock)
				tooltipData = getShulkerTooltipData(tooltipStack);
			RenderSystem.depthFunc(GL11.GL_ALWAYS);
			this.renderTooltip(matrixStack, tooltipList, tooltipData, mouseX, mouseY);
		}
		matrixStack.push();
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	public Optional<TooltipData> getShulkerTooltipData(ItemStack tooltipStack) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				DefaultedList<ItemStack> defaultedList = DefaultedList.of();
				defaultedList.add(ItemStack.EMPTY);
				NbtCompound nbtCompound = tooltipStack.getNbt();
				if (nbtCompound != null) {
					NbtList nbtList = nbtCompound.getCompound("BlockEntityTag").getList("Items", 10);
					Stream<NbtElement> stream = nbtList.stream();
					Objects.requireNonNull(defaultedList);
					Objects.requireNonNull(NbtCompound.class);
					stream.map(NbtCompound.class::cast).map(ItemStack::fromNbt).forEach(defaultedList::add);
					return Optional.of(new BundleTooltipData(defaultedList, 9));
				}
		}
		return Optional.empty();
	}

	private void drawItem(ItemStack stack, int x, int y, String amountText) {
		this.setZOffset(-200);
		this.itemRenderer.zOffset = -200.0F;
		this.itemRenderer.renderInGuiWithOverrides(stack, x, y);
		if (Objects.equals(amountText, "1")) amountText = "";
		this.itemRenderer.renderGuiItemOverlay(this.textRenderer, stack, x, y, amountText);
		this.setZOffset(0);
		this.itemRenderer.zOffset = 0.0F;
	}

}
