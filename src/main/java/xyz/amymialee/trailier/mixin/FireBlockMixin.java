package xyz.amymialee.trailier.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    @Inject(method = "registerFlammableBlock", at = @At("HEAD"), cancellable = true)
    private void trailier$notTorching(Block block, int burnChance, int spreadChance, CallbackInfo ci) {
        if (block == Blocks.TORCHFLOWER) {
            ci.cancel();
        } else if (block == Blocks.PITCHER_PLANT) {
            ci.cancel();
        }
    }
}