package xyz.amymialee.trailier.mixin;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.CraftingDecoratedPotRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CraftingDecoratedPotRecipe.class)
public abstract class CraftingDecoratedPotRecipeMixin {
    @Shadow public abstract boolean fits(int width, int height);
    @Unique private static boolean sherdsRetained = false;

    @Inject(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z", at = @At("HEAD"), cancellable = true)
    public void trailier$centered(RecipeInputInventory inventory, World world, CallbackInfoReturnable<Boolean> cir) {
        if (!sherdsRetained) {
            Registries.ITEM.getEntryList(ItemTags.DECORATED_POT_SHERDS).ifPresent(list -> {
                for (RegistryEntry<Item> entry : list) {
                    entry.value().recipeRemainder = entry.value();
                }
            });
            sherdsRetained = true;
        }
        if (!this.fits(inventory.getWidth(), inventory.getHeight())) {
            cir.setReturnValue(false);
        }
        int sherds = 0;
        for(int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            switch (i) {
                case 1, 3, 5, 7 -> {
                    if (itemStack.isIn(ItemTags.DECORATED_POT_INGREDIENTS)) {
                        sherds++;
                    }
                }
                case 4 -> {
                    if (!itemStack.isOf(Items.DECORATED_POT)) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
                default -> {
                    if (!itemStack.isOf(Items.AIR)) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
        cir.setReturnValue(sherds > 0);
    }

    @Inject(method = "craft(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void trailier$craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager, CallbackInfoReturnable<ItemStack> cir) {
        List<Item> sherds = List.of(
                recipeInputInventory.getStack(1).getItem(),
                recipeInputInventory.getStack(3).getItem(),
                recipeInputInventory.getStack(5).getItem(),
                recipeInputInventory.getStack(7).getItem()
        );
        ItemStack itemStack = Items.DECORATED_POT.getDefaultStack().copy();
        NbtCompound compound = BlockItem.getBlockEntityNbt(recipeInputInventory.getStack(4));
        if (compound == null) {
            compound = new NbtCompound();
        } else {
            compound = compound.copy();
        }
        NbtList oldList = compound.getList("sherds", 8);
        for (int i = 0; i < 4; i++) {
            while (oldList.size() <= i) {
                oldList.add(NbtString.of(Registries.ITEM.getId(Items.BRICK).toString()));
            }
            if (sherds.get(i) != Items.AIR) {
                oldList.set(i, NbtString.of(Registries.ITEM.getId(sherds.get(i)).toString()));
            }
        }
        compound.put("sherds", oldList);
        BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.DECORATED_POT, compound);
        cir.setReturnValue(itemStack);
    }
}