package com.domen.apollochunks.mixin;

import net.minecraft.server.level.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkHolder.class)
public interface ChunkHolderAccessor {
	@Invoker("setQueueLevel")
	void apollochunks$setQueueLevel(int queueLevel);
}
