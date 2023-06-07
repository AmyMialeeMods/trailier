package xyz.amymialee.trailier.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.trailier.Trailier;
import xyz.amymialee.trailier.util.SnifferSaddleComponent;
import xyz.amymialee.trailier.util.SnifferSaddleHolder;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(SnifferEntity.class)
public abstract class SnifferEntityMixin extends AnimalEntity implements ItemSteerable, Saddleable, SnifferSaddleHolder {
    private static final TrackedData<Integer> BOOST_TIME = DataTracker.registerData(SnifferEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final SnifferSaddleComponent saddledComponent = new SnifferSaddleComponent(this.dataTracker, BOOST_TIME);

    protected SnifferEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onTrackedDataSet", at = @At("TAIL"))
    private void trailier$trackedSaddle(TrackedData<?> data, CallbackInfo ci) {
        if (BOOST_TIME.equals(data) && this.getWorld().isClient) {
            this.saddledComponent.boost();
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void trailier$trackBoost(EntityType<? extends AnimalEntity> entityType, World world, CallbackInfo ci) {
        this.dataTracker.startTracking(BOOST_TIME, 0);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void trailier$getOn(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getMainHandStack().isEmpty() || player.getMainHandStack().isOf(Trailier.TORCHFLOWER_SEED_ON_A_STICK) || player.getMainHandStack().isOf(Items.SADDLE)) {
            if (!this.getWorld().isClient && this.getPassengerList().size() < 3 && !this.isBaby()) {
                player.setYaw(this.getYaw());
                player.setPitch(this.getPitch());
                player.startRiding(this);
            }
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient));
        }
    }

    @Unique @Override
    public boolean consumeOnAStickItem() {
        return this.saddledComponent.boost(this.getRandom());
    }

    @Unique @Override
    public SnifferSaddleComponent getSnifferSaddleComponent() {
        return this.saddledComponent;
    }

    @Unique @Override
    public boolean canBeSaddled() {
        return false;
    }

    @Unique @Override
    public void saddle(@Nullable SoundCategory sound) {}

    @Unique @Override
    public boolean isSaddled() {
        return true;
    }
}