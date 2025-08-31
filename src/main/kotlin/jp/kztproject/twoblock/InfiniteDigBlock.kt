package jp.kztproject.twoblock

import net.minecraft.block.Blocks
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.slf4j.LoggerFactory

object InfiniteDigBlock {
    private val logger = LoggerFactory.getLogger("InfiniteDigBlock")
    
    // 無限掘りブロックとして使用するブロック
    val BLOCK = Blocks.GRASS_BLOCK
    
    // 無限掘りブロックから生成される可能性のあるブロック
    private val availableBlocks = listOf(
        Blocks.STONE,
        Blocks.COBBLESTONE,
        Blocks.OAK_LOG,
        Blocks.DIRT,
        Blocks.GRASS_BLOCK,
        Blocks.SAND,
        Blocks.GRAVEL,
        Blocks.COAL_ORE,
        Blocks.IRON_ORE,
        Blocks.GOLD_ORE,
        Blocks.DIAMOND_ORE,
        Blocks.EMERALD_ORE,
        Blocks.OBSIDIAN,
        Blocks.NETHERRACK,
        Blocks.END_STONE
    )
    
    /**
     * 指定された位置が無限掘りブロックかどうかを判定
     */
    fun isInfiniteDigBlock(world: World, pos: BlockPos): Boolean {
        return world.getBlockState(pos).block == BLOCK
    }
    
    /**
     * 無限掘りブロックが破壊された時の処理
     * 同じ位置にランダムなブロックを配置する
     */
    fun onBlockBroken(world: World, pos: BlockPos) {
        logger.info("onBlockBroken called at position: $pos")
        
        // ワールドがクライアントサイドの場合は何もしない
        if (world.isClient) {
            logger.info("Skipping block replacement on client side")
            return
        }
        
        // 常にGRASS_BLOCKに戻す（無限掘り機能を維持するため）
        logger.info("Replacing with GRASS_BLOCK to maintain infinite dig functionality")
        
        // 新しいブロックを配置
        val success = world.setBlockState(pos, BLOCK.defaultState)
        logger.info("Block placement success: $success")
        
        // 破壊音を再生
        world.playSound(
            null,
            pos,
            SoundEvents.BLOCK_STONE_BREAK,
            SoundCategory.BLOCKS,
            1.0f,
            1.0f
        )
        
        logger.info("Infinite dig block replaced with GRASS_BLOCK at $pos")
    }
    
    /**
     * ワールドの指定位置に無限掘りブロックを配置
     */
    fun placeAt(world: World, pos: BlockPos) {
        world.setBlockState(pos, BLOCK.defaultState)
        logger.info("Infinite dig block placed at: $pos")
    }
}