package xyz.amymialee.trailier.mixin;

import net.minecraft.entity.passive.SnifferBrain;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.trailier.Trailier;

import java.util.ArrayList;
import java.util.List;

@Mixin(SnifferBrain.class)
public class SnifferBrainMixin {
    @Inject(method = "getTemptItems", at = @At("RETURN"), cancellable = true)
    private static void trailier$sniffSeeds(CallbackInfoReturnable<Ingredient> cir) {
        Ingredient ingredient = cir.getReturnValue();
        List<ItemStack> items = new ArrayList<>(List.of(ingredient.getMatchingStacks()));
        items.add(new ItemStack(Trailier.TORCHFLOWER_SEED_ON_A_STICK));
        Ingredient more = Ingredient.ofStacks(items.stream());
        cir.setReturnValue(more);
    }
}