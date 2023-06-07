//package xyz.amymialee.trailier.recipe;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSyntaxException;
//import net.fabricmc.fabric.api.block.v1.FabricBlockState;
//import net.minecraft.advancement.Advancement;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.data.server.recipe.RecipeJsonProvider;
//import net.minecraft.inventory.Inventory;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.network.PacketByteBuf;
//import net.minecraft.recipe.AbstractCookingRecipe;
//import net.minecraft.recipe.Ingredient;
//import net.minecraft.recipe.Recipe;
//import net.minecraft.recipe.RecipeSerializer;
//import net.minecraft.recipe.RecipeType;
//import net.minecraft.recipe.book.CookingRecipeCategory;
//import net.minecraft.registry.DynamicRegistryManager;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.Registry;
//import net.minecraft.registry.RegistryKeys;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.world.World;
//import org.jetbrains.annotations.Nullable;
//
//public class BrushingRecipe implements Recipe<Inventory> {
//    protected final Identifier id;
//    protected final String group;
//    protected final BlockState input;
//    protected final BlockState output;
//
//    public BrushingRecipe(Identifier id, String group, BlockState input, BlockState output) {
//        this.id = id;
//        this.group = group;
//        this.input = input;
//        this.output = output;
//    }
//
//    @Override
//    public ItemStack createIcon() {
//        return new ItemStack(Items.BRUSH);
//    }
//
//    @Override
//    public Identifier getId() {
//        return this.id;
//    }
//
//    @Override
//    public String getGroup() {
//        return this.group;
//    }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return BrushingRecipeSerializer.INSTANCE;
//    }
//
//    @Override
//    public RecipeType<?> getType() {
//        return BrushingRecipeType.INSTANCE;
//    }
//
//    @Override
//    public boolean matches(Inventory inventory, World world) {
//        return false;
//    }
//    @Override
//    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
//        return ItemStack.EMPTY;
//    }
//    @Override
//    public boolean fits(int width, int height) {
//        return true;
//    }
//    @Override
//    public ItemStack getOutput(DynamicRegistryManager registryManager) {
//        return ItemStack.EMPTY;
//    }
//
//    public static class BrushingRecipeType implements RecipeType<BrushingRecipe> {
//        public static final BrushingRecipeType INSTANCE = new BrushingRecipeType();
//        private BrushingRecipeType() {}
//    }
//
//    private static class BrushingRecipeJsonFormat {
//        String input;
//        String output;
//    }
//
//    public static class BrushingRecipeSerializer implements RecipeSerializer<BrushingRecipe> {
//        public static final BrushingRecipeSerializer INSTANCE = new BrushingRecipeSerializer();
//
//        @Override
//        public BrushingRecipe read(Identifier id, JsonObject json) {
//            BrushingRecipeJsonFormat jsonRecipe = new Gson().fromJson(json, BrushingRecipeJsonFormat.class);
//            if (jsonRecipe.input == null) {
//                throw new JsonSyntaxException("The input is missing!");
//            } else if (jsonRecipe.output == null) {
//                throw new JsonSyntaxException("The output is missing!");
//            }
//            Block inputBlock = Registries.BLOCK.get(Identifier.tryParse(jsonRecipe.input));
//            return new BrushingRecipe(id, input, jsonRecipe.inputCount, output1, output2, jsonRecipe.cookTime, jsonRecipe.maintainNbt);
//        }
//
//        @Override
//        public void write(PacketByteBuf packetData, BrushingRecipe recipe) {
//            recipe.input.write(packetData);
//            packetData.writeInt(recipe.inputCount);
//            packetData.writeItemStack(recipe.output1);
//            packetData.writeItemStack(recipe.output2);
//            packetData.writeInt(recipe.cookTime);
//            packetData.writeBoolean(recipe.maintainNbt);
//        }
//
//        @Override
//        public BrushingRecipe read(Identifier id, PacketByteBuf packetData) {
//            Ingredient input = Ingredient.fromPacket(packetData);
//            int inputCount = packetData.readInt();
//            ItemStack output1 = packetData.readItemStack();
//            ItemStack output2 = packetData.readItemStack();
//            int cookTime = packetData.readInt();
//            boolean maintainNbt = packetData.readBoolean();
//            return new BrushingRecipe(id, input, inputCount, output1, output2, cookTime, maintainNbt);
//        }
//    }
//
//    public static class BrushingRecipeJsonProvider implements RecipeJsonProvider {
//        private final Identifier recipeId;
//        private final String group;
//        private final Ingredient ingredient;
//        private final int inputCount;
//        private final Item result1;
//        private final int resultCount1;
//        private final Item result2;
//        private final int resultCount2;
//        private final int cookTime;
//        private final boolean maintainNbt;
//        private final Advancement.Task builder;
//        private final Identifier advancementId;
//        private final RecipeSerializer<? extends BrushingRecipe> serializer;
//
//        public BrushingRecipeJsonProvider(Identifier recipeId, String group, Ingredient ingredient, int inputCount, Item result1, int resultCount1, Item result2, int resultCount2, int cookTime, boolean maintainNbt, Advancement.Task builder, Identifier advancementId, RecipeSerializer<? extends BrushingRecipe> serializer) {
//            this.recipeId = recipeId;
//            this.group = group;
//            this.ingredient = ingredient;
//            this.inputCount = inputCount;
//            this.result1 = result1;
//            this.resultCount1 = resultCount1;
//            this.result2 = result2;
//            this.resultCount2 = resultCount2;
//            this.cookTime = cookTime;
//            this.maintainNbt = maintainNbt;
//            this.builder = builder;
//            this.advancementId = advancementId;
//            this.serializer = serializer;
//        }
//
//        @Override
//        public void serialize(JsonObject json) {
//            if (!this.group.isEmpty()) {
//                json.addProperty("group", this.group);
//            }
//            json.add("ingredient", this.ingredient.toJson());
//            json.addProperty("inputCount", this.inputCount);
//            json.addProperty("result1", Registry.ITEM.getId(this.result1).toString());
//            json.addProperty("resultCount1", this.resultCount1);
//            json.addProperty("result2", Registry.ITEM.getId(this.result2).toString());
//            json.addProperty("resultCount2", this.resultCount2);
//            json.addProperty("cookTime", this.cookTime);
//            json.addProperty("maintainNbt", this.maintainNbt);
//        }
//
//        @Override
//        public RecipeSerializer<?> getSerializer() {
//            return this.serializer;
//        }
//
//        @Override
//        public Identifier getRecipeId() {
//            return this.recipeId;
//        }
//
//        @Override
//        @Nullable
//        public JsonObject toAdvancementJson() {
//            return this.builder.toJson();
//        }
//
//        @Override
//        @Nullable
//        public Identifier getAdvancementId() {
//            return this.advancementId;
//        }
//    }
//}