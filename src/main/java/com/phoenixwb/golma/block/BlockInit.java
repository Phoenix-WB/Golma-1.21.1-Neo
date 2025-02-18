package com.phoenixwb.golma.block;

import com.phoenixwb.golma.Golma;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockInit {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Golma.MODID);

	public static final DeferredBlock<ServingPlateBlock> SERVING_PLATE = BLOCKS.register("serving_plate",
			() -> new ServingPlateBlock(BlockBehaviour.Properties.of().strength(1.5F, 3.0F).sound(SoundType.METAL)
					.noOcclusion().pushReaction(PushReaction.DESTROY)));
}
