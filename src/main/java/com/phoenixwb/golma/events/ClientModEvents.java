package com.phoenixwb.golma.events;

import com.phoenixwb.golma.Golma;
import com.phoenixwb.golma.block.blockentity.BlockEntityInit;
import com.phoenixwb.golma.block.blockentity.ServingPlateRenderer;
import com.phoenixwb.golma.entity.EntityInit;
import com.phoenixwb.golma.entity.renderer.GolmaRenderer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Golma.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class ClientModEvents {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		EntityRenderers.register(EntityInit.GOLMA.get(), GolmaRenderer::new);
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BlockEntityInit.SERVING_PLATE.get(), ServingPlateRenderer::new);
	}
}
