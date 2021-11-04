package vazkii.akashictomeoftools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import vazkii.akashictomeoftools.client.TomeScreen;

import java.util.List;
import java.util.Optional;

public class TomeItem extends Item {

	public TomeItem(Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getStackInHand(handIn);
		if(worldIn.isClient){
			ItemStack tome = playerIn.getStackInHand(handIn);
			if (!(tome instanceof ItemStackWrap)) {
				if (tome.isOf(AkashicTome.TOME_ITEM)) {
					playerIn.setStackInHand(handIn, new ItemStackWrap(Optional.of(tome.writeNbt(new NbtCompound()))));
					tome = playerIn.getStackInHand(handIn);
					if (!(tome instanceof ItemStackWrap)) {
						return new TypedActionResult<>(ActionResult.FAIL, stack);}
				}
				else return new TypedActionResult<>(ActionResult.FAIL, stack);}
			MinecraftClient.getInstance().setScreen(new TomeScreen((ItemStackWrap) tome, handIn, playerIn));
			ClientPlayNetworking.send(AkashicTome.AkashicChannel, new PacketByteBuf(PacketByteBufs.create().writeInt(-2)));
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		if (!(stack instanceof ItemStackWrap)) tooltip.add(new LiteralText("§l§c!!!BRICKED!!!"));
	}
}
