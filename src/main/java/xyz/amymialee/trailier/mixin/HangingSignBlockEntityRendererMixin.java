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
    @Inject(method = "setAngles", at = @At("HEAD"), cancellable = true)
    private void trailier$swing(MatrixStack matrices, float rotationDegrees, BlockState state, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) (Math.sin(player.age / 10.0) * 10.0)));
        }
    }

//    @Inject(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), cancellable = true)
//    private void trailier$renderSignBetter(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
//
//    }
//
//    @WrapOperation(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPartData;addChild(Ljava/lang/String;Lnet/minecraft/client/model/ModelPartBuilder;Lnet/minecraft/client/model/ModelTransform;)Lnet/minecraft/client/model/ModelPartData;", ordinal = 1))
//    private static ModelPartData trailier$noSignPlank(ModelPartData data, String name, ModelPartBuilder builder, ModelTransform rotationData, Operation<ModelPartData> operation) {
//        return data;
//    }

//    @Mixin(HangingSignBlockEntityRenderer.HangingSignModel.class)
//    private static class HangingSignModelMixin {
//        @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;getChild(Ljava/lang/String;)Lnet/minecraft/client/model/ModelPart;"))
//        private ModelPart trailier$noPlank(ModelPart part, String string, Operation<ModelPart> operation) {
//            return null;
//        }
//
//        @WrapOperation(method = "updateVisibleParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;visible:Z"))
//    }
}