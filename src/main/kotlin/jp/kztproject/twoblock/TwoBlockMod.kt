package jp.kztproject.twoblock

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import org.slf4j.LoggerFactory

object TwoBlockMod : ModInitializer {
    const val MOD_ID = "two-block-mod"
    private val logger = LoggerFactory.getLogger(MOD_ID)
    
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

	override fun onInitialize() {
		logger.info("Loading Two Block Mod...")
		
		// プレイヤーがワールドに参加した時の処理
		ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
			val player = handler.player
			setupOneBlockWorld(player)
		}
		
		// サーバーが起動した時にワールド設定
		ServerLifecycleEvents.SERVER_STARTED.register { server ->
			setupWorldEnvironment(server)
		}
		
		// ワンブロックアイテムの使用処理
		UseItemCallback.EVENT.register { player, world, hand ->
			val stack = player.getStackInHand(hand)
			if (stack.item == Items.STICK && !world.isClient) {
				val pos = player.blockPos.add(0, 1, 0)
				
				if (world.isAir(pos)) {
					val randomBlock = availableBlocks.random()
					world.setBlockState(pos, randomBlock.defaultState)
					
					world.playSound(
						null,
						pos,
						SoundEvents.BLOCK_STONE_PLACE,
						SoundCategory.BLOCKS,
						1.0f,
						1.0f
					)
					
					if (!player.abilities.creativeMode) {
						stack.decrement(1)
					}
					
					return@register ActionResult.SUCCESS
				}
			}
			ActionResult.PASS
		}
		
		logger.info("Two Block Mod loaded successfully!")
	}
	
	private fun setupOneBlockWorld(player: ServerPlayerEntity) {
		val world = player.world
		val spawnPos = BlockPos(0, 64, 0)
		
		// スポーン周辺をクリア（半径10ブロック）
		for (x in -10..10) {
			for (z in -10..10) {
				for (y in 50..80) {
					val pos = BlockPos(x, y, z)
					if (pos != spawnPos) {
						world.setBlockState(pos, Blocks.AIR.defaultState)
					}
				}
			}
		}
		
		// スポーン位置にグラスブロックを設置
		world.setBlockState(spawnPos, Blocks.GRASS_BLOCK.defaultState)
		
		// プレイヤーを安全な位置にテレポート
		player.teleport(0.5, 65.0, 0.5, false)
		
		// ワンブロックアイテム（棒）を与える
		val stick = Items.STICK.defaultStack
		stick.count = 64
		player.giveItemStack(stick)
		
		logger.info("One Block world setup complete for player: ${player.name.string}")
	}
	
	private fun setupWorldEnvironment(server: net.minecraft.server.MinecraftServer) {
		val overworld = server.overworld
		logger.info("Setting up One Block environment...")
		
		// ワールドスポーンを設定
		overworld.setSpawnPos(BlockPos(0, 65, 0), 0.0f)
	}
}