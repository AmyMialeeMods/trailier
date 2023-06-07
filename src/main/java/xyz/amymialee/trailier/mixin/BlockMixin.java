package xyz.amymialee.trailier.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "hasRandomTicks", at = @At("RETURN"), cancellable = true)
    private void trailier$pitcherInit(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            if (state.isOf(Blocks.PITCHER_PLANT) || state.isOf(Blocks.TORCHFLOWER)) {
                cir.setReturnValue(true);
            }
        }
    }
}