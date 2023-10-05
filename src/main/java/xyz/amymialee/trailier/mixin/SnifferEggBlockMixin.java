package xyz.amymialee.trailier.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnifferEggBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnifferEggBlock.class)
public class SnifferEggBlockMixin extends Block {
    public SnifferEggBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "scheduledTick", at = @At("HEAD"))
    private void trailier$hatchCancel(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!SnifferEggBlock.isAboveHatchBooster(world, pos)) {
            world.scheduleBlockTick(pos, this, 24000);
            ci.cancel();
        }
    }
}