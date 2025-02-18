package com.phoenixwb.golma.block.blockentity;

import com.phoenixwb.golma.Golma;
import com.phoenixwb.golma.block.BlockInit;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityInit {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister
			.create(Registries.BLOCK_ENTITY_TYPE, Golma.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ServingPlateBlockEntity>> SERVING_PLATE = BLOCK_ENTITY_TYPES
			.register("serving_plate", () -> BlockEntityType.Builder
					.of(ServingPlateBlockEntity::new, BlockInit.SERVING_PLATE.get()).build(null));
}
