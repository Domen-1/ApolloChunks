package com.domen.apollochunks.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@Mixin(ChunkMap.class)
public interface ChunkMapAccessor {
	@Invoker("onLevelChange")
	void apollochunks$onLevelChange(ChunkPos pos, IntSupplier oldLevel, int newLevel, IntConsumer setQueueLevel);
}
