package xyz.amymialee.trailier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import xyz.amymialee.trailier.blocks.TorchsparkBlock;
import xyz.amymialee.trailier.items.SnifferStickItem;

public class Trailier implements ModInitializer {
    public static final String MOD_ID = "trailier";
    private static final Identifier CHISELED_BOOKSHELF_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/chiseled_bookshelf");
    private static final Identifier DECORATED_POT_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/decorated_pot");
    private static final Identifier PITCHER_CROP_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/pitcher_crop");
    private static final Identifier SUSPICIOUS_GRAVEL_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/suspicious_gravel");
    private static final Identifier SUSPICIOUS_SAND_LOOT_TABLE_ID = new Identifier("minecraft", "blocks/suspicious_sand");
    public static final Block TORCHSPARK = Registry.register(Registries.BLOCK, id("torchspark"), new TorchsparkBlock(FabricBlockSettings.create().replaceable().noCollision().breakInstantly().luminance(state -> 15).sounds(BlockSoundGroup.GRASS).pistonBehavior(PistonBehavior.DESTROY), ParticleTypes.FLAME));
    public static final Item TORCHFLOWER_SEED_ON_A_STICK = Registry.register(Registries.ITEM, id("torchflower_seed_on_a_stick"), new SnifferStickItem(new FabricItemSettings().maxDamage(256)));

    @Override
    public void onInitialize() {
        LootTableEvents.REPLACE.register((resourceManager, lootManager, identifier, lootTable, lootTableSource) -> {
            if (CHISELED_BOOKSHELF_LOOT_TABLE_ID.equals(identifier)) {
                return LootTable.builder().pool(LootPool.builder().conditionally(SurvivesExplosionLootCondition.INSTANCE).with(ItemEntry.builder(Items.CHISELED_BOOKSHELF))).build();
            }
            if (DECORATED_POT_LOOT_TABLE_ID.equals(identifier)) {
                return LootTable.builder().pool(LootPool.builder().conditionally(SurvivesExplosionLootCondition.INSTANCE).with(ItemEntry.builder(Items.DECORATED_POT)
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("sherds", "BlockEntityTag.sherds")))).build();
            }
            if (PITCHER_CROP_LOOT_TABLE_ID.equals(identifier)) {
                return LootTable.builder().pool(LootPool.builder().conditionally(SurvivesExplosionLootCondition.INSTANCE).with(AlternativeEntry.builder(PitcherCropBlock.HALF.getValues(), cropHalf -> {
                    BlockStatePropertyLootCondition.Builder half = BlockStatePropertyLootCondition.builder(Blocks.PITCHER_CROP).properties(StatePredicate.Builder.create().exactMatch(TallPlantBlock.HALF, cropHalf));
                    BlockStatePropertyLootCondition.Builder age = BlockStatePropertyLootCondition.builder(Blocks.PITCHER_CROP).properties(StatePredicate.Builder.create().exactMatch(PitcherCropBlock.AGE, 4));
                    return cropHalf == DoubleBlockHalf.UPPER ?
                            ItemEntry.builder(Items.PITCHER_PLANT).conditionally(age).conditionally(half).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F))) :
                            ItemEntry.builder(Items.PITCHER_POD).conditionally(half).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1.0F)));
                }))).build();
            }
            if (SUSPICIOUS_GRAVEL_LOOT_TABLE_ID.equals(identifier)) {
                return LootTable.builder().pool(LootPool.builder().conditionally(SurvivesExplosionLootCondition.INSTANCE).with(ItemEntry.builder(Items.SUSPICIOUS_GRAVEL)
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("item", "BlockEntityTag.item"))
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("LootTable", "BlockEntityTag.LootTable"))
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")))).build();
            }
            if (SUSPICIOUS_SAND_LOOT_TABLE_ID.equals(identifier)) {
                return LootTable.builder().pool(LootPool.builder().conditionally(SurvivesExplosionLootCondition.INSTANCE).with(ItemEntry.builder(Items.SUSPICIOUS_SAND)
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("item", "BlockEntityTag.item"))
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("LootTable", "BlockEntityTag.LootTable"))
                        .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY).withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed")))).build();
            }
            return lootTable;
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> content.addAfter(Items.WARPED_FUNGUS_ON_A_STICK, TORCHFLOWER_SEED_ON_A_STICK));
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}