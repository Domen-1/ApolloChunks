package com.domen.apollochunks.mixin;

import com.domen.apollochunks.chunk.FakePlayerTicket;
import net.minecraft.server.level.TicketType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TicketType.class)
public abstract class TicketTypeMixin {
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void apollochunks$registerFakePlayerTicket(CallbackInfo ci) {
		FakePlayerTicket.register();
	}
}
