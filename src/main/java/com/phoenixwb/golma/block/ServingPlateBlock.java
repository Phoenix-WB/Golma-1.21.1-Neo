package com.phoenixwb.golma.block;

import com.mojang.serialization.MapCodec;
import com.phoenixwb.golma.block.blockentity.ServingPlateBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ServingPlateBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
	public static final MapCodec<ServingPlateBlock> CODEC = simpleCodec(ServingPlateBlock::new);
	public static final VoxelShape SHAPE = Shapes.box(0.25, 0, 0.25, 0.75, 0.125, 0.75);
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public ServingPlateBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH)
				.setValue(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide() && !state.getValue(WATERLOGGED)
				&& level.getBlockEntity(pos) instanceof ServingPlateBlockEntity blockEntity) {
			ItemStack servedStack = blockEntity.getItem();
			if (servedStack.isEmpty()) {
				blockEntity.placeItem(stack.copy(), player);
				if (!player.isCreative()) {
					stack.setCount(0);
				}
				return ItemInteractionResult.SUCCESS;
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
			BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ServingPlateBlockEntity blockEntity) {
			ItemStack servedStack = blockEntity.getItem();
			if (!servedStack.isEmpty()) {
				player.addItem(servedStack.copy());
				blockEntity.takeItem(player);
			}
		}
		return InteractionResult.CONSUME;
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof ServingPlateBlockEntity blockEntity) {
				if (level instanceof ServerLevel) {
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
							((ServingPlateBlockEntity) blockEntity).getItem());
				}

				super.onRemove(state, level, pos, newState, movedByPiston);
				level.updateNeighbourForOutputSignal(pos, this);
			} else {
				super.onRemove(state, level, pos, newState, movedByPiston);
			}
		}
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof ServingPlateBlockEntity blockEntity) {
			return Math.max(0, 15 * (64 - blockEntity.getItem().getCount()) / 64);
		}
		return 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!state.getValue(WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
			if (level instanceof ServerLevel serverLevel) {
				if (serverLevel.getBlockEntity(pos) instanceof ServingPlateBlockEntity blockEntity) {
					Containers.dropItemStack(serverLevel, pos.getX(), pos.getY(), pos.getZ(),
							((ServingPlateBlockEntity) blockEntity).getItem());
					serverLevel.updateNeighbourForOutputSignal(pos, this);
				}
			}
			
			level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true)), 3);
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
			BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.DOWN && !state.canSurvive(level, currentPos)) {
			return Blocks.AIR.defaultBlockState();
		} else if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return Block.canSupportCenter(level, pos.below(), Direction.UP);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ServingPlateBlockEntity(pos, state);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}
}
