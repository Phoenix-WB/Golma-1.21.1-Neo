package com.phoenixwb.golma.block.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.phoenixwb.golma.block.ServingPlateBlock;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ServingPlateRenderer implements BlockEntityRenderer<ServingPlateBlockEntity> {
	private final ItemRenderer itemRenderer;

	public ServingPlateRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(ServingPlateBlockEntity blockEntity, float partialTick, PoseStack poseStack,
			MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		ItemStack stack = blockEntity.getItem();
		if (stack != ItemStack.EMPTY) {
	        Direction direction = blockEntity.getBlockState().getValue(ServingPlateBlock.FACING);
            poseStack.translate(0.5F, 0.125F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-direction.getOpposite().toYRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(0.45F, 0.45F, 0.45F);
			itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
					poseStack, bufferSource, blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong());
		}
		poseStack.popPose();
	}
}
