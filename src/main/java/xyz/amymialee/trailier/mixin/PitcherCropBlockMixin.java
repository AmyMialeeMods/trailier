package xyz.amymialee.trailier.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PitcherCropBlock.class)
public abstract class PitcherCropBlockMixin extends TallPlantBlock {
    public PitcherCropBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"), cancellable = true)
    public void trailier$persistentPot(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (state.get(TallPlantBlock.HALF) == DoubleBlockHalf.LOWER) {
            if (!world.getBlockState(pos.up()).isOf(this)) {
                int age = state.get(PitcherCropBlock.AGE);
                cir.setReturnValue(state.with(PitcherCropBlock.AGE, Math.min(age, 2)));
            } else {
                cir.setReturnValue(state);
            }
        } else {
            cir.setReturnValue(state.canPlaceAt(world, pos) ? state : Blocks.AIR.getDefaultState());
        }
    }

    @Inject(method = "canPlaceAt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void trailier$canPlacePot(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (PitcherCropBlock.isUpperHalf(state)) {
            cir.setReturnValue(super.canPlaceAt(state, world, pos));
        } else {
            cir.setReturnValue(this.canPlantOnTop(world.getBlockState(pos.down()), world, pos.down()) && PitcherCropBlock.canPlaceAt(world, pos));
        }
    }
}