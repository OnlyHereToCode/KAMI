package me.zeroeightsix.kami.mixin.client;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author 086
 */
@Mixin(EntityFishHook.class)
public class MixinEntityFishHook {

    @Shadow public Entity caughtEntity;

    @Inject(method = "bringInHookedEntity", at = @At("HEAD"), cancellable = true)
    protected void bringInHookedEntity(CallbackInfo info) {
        if (caughtEntity == Minecraft.getMinecraft().player && ModuleManager.isModuleEnabled("Velocity")) info.cancel();
    }

}
