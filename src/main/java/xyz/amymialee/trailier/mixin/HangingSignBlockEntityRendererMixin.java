package xyz.amymialee.trailier.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HangingSignBlockEntityRenderer.class)
public class HangingSignBlockEntityRendererMixin {
    @Inject(method = "setAngles", at = @At("TAIL"), cancellable = true)
    private void trailier$swing(MatrixStack matrices, float rotationDegrees, BlockState state, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            matrices.translate(0, -0.9375 + 0.3125F, 0);
            matrices.translate(0, 1, 0);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) (Math.sin((player.age) / 10.0 + state.hashCode()) * 2.5)));
            matrices.translate(0, -1, 0);
            matrices.translate(0, 0.9375 - 0.3125F, 0);
        }
    }
}