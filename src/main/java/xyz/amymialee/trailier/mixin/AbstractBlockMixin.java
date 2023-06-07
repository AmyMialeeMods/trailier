package xyz.amymialee.trailier.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void trailier$pitcherTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        AbstractBlock block = (AbstractBlock) (Object) this;
        if (block == Blocks.PITCHER_PLANT) {
            Direction direction = Direction.fromHorizontal(random.nextInt(4));
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.CAULDRON)) {
                world.setBlockState(blockPos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, LeveledCauldronBlock.MIN_LEVEL));
            } else if (blockState.isOf(Blocks.WATER_CAULDRON)) {
                int level = blockState.get(LeveledCauldronBlock.LEVEL);
                if (level < LeveledCauldronBlock.MAX_LEVEL) {
                    world.setBlockState(blockPos, blockState.with(LeveledCauldronBlock.LEVEL, level + 1));
                }
            }
        } else if (block == Blocks.TORCHFLOWER) {
            Direction direction = Direction.fromHorizontal(random.nextInt(4));
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.WHEAT)) {
                int level = blockState.get(CropBlock.AGE);
                if (level >= (CropBlock.MAX_AGE / 2)) {
                    world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(null, blockState));
                    world.setBlockState(blockPos, Blocks.TORCHFLOWER_CROP.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }
    }
}