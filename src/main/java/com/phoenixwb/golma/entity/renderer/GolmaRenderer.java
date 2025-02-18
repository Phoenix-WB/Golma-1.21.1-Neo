package com.phoenixwb.golma.entity.renderer;

import com.phoenixwb.golma.Golma;
import com.phoenixwb.golma.entity.GolmaEntity;

import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GolmaRenderer extends MobRenderer<GolmaEntity, SnowGolemModel<GolmaEntity>> {
	private static final ResourceLocation GOLMA_LOCATION = ResourceLocation.fromNamespaceAndPath(Golma.MODID,
			"textures/entity/golma.png");

	public GolmaRenderer(EntityRendererProvider.Context context) {
		super(context, new SnowGolemModel<>(context.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
		this.addLayer(new GolmaHeadLayer(this, context.getBlockRenderDispatcher(), context.getItemRenderer()));
	}

	public ResourceLocation getTextureLocation(GolmaEntity entity) {
		return GOLMA_LOCATION;
	}
}
