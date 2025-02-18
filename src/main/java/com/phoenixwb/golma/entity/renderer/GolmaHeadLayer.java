package com.phoenixwb.golma.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.phoenixwb.golma.entity.GolmaEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GolmaHeadLayer extends RenderLayer<GolmaEntity, SnowGolemModel<GolmaEntity>> {
	private final BlockRenderDispatcher blockRenderer;
	private final ItemRenderer itemRenderer;

	public GolmaHeadLayer(RenderLayerParent<GolmaEntity, SnowGolemModel<GolmaEntity>> renderer,
			BlockRenderDispatcher blockRenderer, ItemRenderer itemRenderer) {
		super(renderer);
		this.blockRenderer = blockRenderer;
		this.itemRenderer = itemRenderer;
	}

	@SuppressWarnings("deprecation")
	public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, GolmaEntity livingEntity,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
			float headPitch) {
		boolean flag = Minecraft.getInstance().shouldEntityAppearGlowing(livingEntity) && livingEntity.isInvisible();
		if (!livingEntity.isInvisible() || flag) {
			poseStack.pushPose();
			this.getParentModel().getHead().translateAndRotate(poseStack);
			poseStack.translate(0.0F, -0.34375F, 0.0F);
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			poseStack.scale(0.625F, -0.625F, -0.625F);
			ItemStack itemstack = new ItemStack(Blocks.JACK_O_LANTERN);
			if (flag) {
				BlockState blockstate = Blocks.JACK_O_LANTERN.defaultBlockState();
				BakedModel bakedmodel = this.blockRenderer.getBlockModel(blockstate);
				int i = LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F);
				poseStack.translate(-0.5F, -0.5F, -0.5F);
				this.blockRenderer.getModelRenderer().renderModel(poseStack.last(),
						buffer.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), blockstate, bakedmodel,
						0.0F, 0.0F, 0.0F, packedLight, i);
			} else {
				this.itemRenderer.renderStatic(livingEntity, itemstack, ItemDisplayContext.HEAD, false, poseStack,
						buffer, livingEntity.level(), packedLight,
						LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F), livingEntity.getId());
			}

			poseStack.popPose();
		}
	}
}
