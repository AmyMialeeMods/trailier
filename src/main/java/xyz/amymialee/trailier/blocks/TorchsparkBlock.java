package xyz.amymialee.trailier.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TorchsparkBlock extends Block {
    protected static final VoxelShape BOUNDING_SHAPE = Block.createCuboidShape(5.5, 5.5, 5.5, 9.5, 9.5, 9.5);
    protected final ParticleEffect particle;

    public TorchsparkBlock(AbstractBlock.Settings settings, ParticleEffect particle) {
        super(settings);
        this.particle = particle;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BOUNDING_SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = pos.getX() + 0.5 + random.nextGaussian() * 0.1;
        double e = pos.getY() + 0.5 + random.nextGaussian() * 0.1;
        double f = pos.getZ() + 0.5 + random.nextGaussian() * 0.1;
        world.addParticle(this.particle, d, e, f, 0.0, 0.0, 0.0);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Items.TORCHFLOWER);
    }
}