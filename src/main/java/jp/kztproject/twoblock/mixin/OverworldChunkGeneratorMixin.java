package jp.kztproject.twoblock.mixin;

import jp.kztproject.twoblock.VoidChunkGenerator;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class OverworldChunkGeneratorMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/DimensionOptions;chunkGenerator()Lnet/minecraft/world/gen/chunk/ChunkGenerator;"))
    private ChunkGenerator replaceChunkGenerator(DimensionOptions dimensionOptions) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        // オーバーワールドの場合のみVoidChunkGeneratorに置き換え
        if (world.getRegistryKey() == World.OVERWORLD) {
            DynamicRegistryManager registryManager = world.getRegistryManager();
            return new VoidChunkGenerator(registryManager.getOrThrow(RegistryKeys.BIOME));
        }
        
        return dimensionOptions.chunkGenerator();
    }
}