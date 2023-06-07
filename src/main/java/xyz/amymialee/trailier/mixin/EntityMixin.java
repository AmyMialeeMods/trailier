package xyz.amymialee.trailier.mixin;

import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "updatePassengerForDismount", at = @At("HEAD"), cancellable = true)
    private void trailier$getOff(LivingEntity passenger, CallbackInfoReturnable<Vec3d> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof SnifferEntity sniffer) {
            Direction direction = sniffer.getMovementDirection();
            if (direction.getAxis() != Direction.Axis.Y) {
                int[][] offsets = Dismounting.getDismountOffsets(direction);
                BlockPos blockPos = sniffer.getBlockPos();
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                for (EntityPose entityPose : passenger.getPoses()) {
                    Box box = passenger.getBoundingBox(entityPose);
                    for (int[] offset : offsets) {
                        mutable.set(blockPos.getX() + offset[0], blockPos.getY(), blockPos.getZ() + offset[1]);
                        double d = sniffer.getWorld().getDismountHeight(mutable);
                        if (Dismounting.canDismountInBlock(d)) {
                            Vec3d vec3d = Vec3d.ofCenter(mutable, d);
                            if (Dismounting.canPlaceEntityAt(self.getWorld(), passenger, box.offset(vec3d))) {
                                passenger.setPose(entityPose);
                                cir.setReturnValue(vec3d);
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "updatePassengerPosition(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/Entity$PositionUpdater;)V", at = @At("HEAD"), cancellable = true)
    protected void trailier$seating(Entity passenger, Entity.PositionUpdater positionUpdater, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self instanceof SnifferEntity sniffer) {
            float g = (float) (sniffer.getMountedHeightOffset() + passenger.getHeightOffset());
            Vec3d vec3d = new Vec3d(0.0, 0.0, 0.0);
            int index = sniffer.getPassengerList().indexOf(passenger);
            int size = sniffer.getPassengerList().size();
            if (index == 0) {
                if (size > 2) {
                    vec3d = new Vec3d(0.0, 0.0, 0.85F).rotateY(-sniffer.bodyYaw * (float) (Math.PI / 180.0));
                } else if (size > 1) {
                    vec3d = new Vec3d(0.0, 0.0, 0.4F).rotateY(-sniffer.bodyYaw * (float) (Math.PI / 180.0));
                }
            } else if (index == 1) {
                if (size > 2) {
                    vec3d = new Vec3d(0.0, 0.0, -0.85F).rotateY(-sniffer.bodyYaw * (float) (Math.PI / 180.0));
                } else {
                    vec3d = new Vec3d(0.0, 0.0, -0.4F).rotateY(-sniffer.bodyYaw * (float) (Math.PI / 180.0));
                }
            }
            positionUpdater.accept(passenger, sniffer.getX() + vec3d.x, sniffer.getY() + (double) g, sniffer.getZ() + vec3d.z);
            ci.cancel();
        }
    }
}