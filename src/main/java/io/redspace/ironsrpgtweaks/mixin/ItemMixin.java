package io.redspace.ironsrpgtweaks.mixin;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin({Item.class, PotionItem.class})
public class ItemMixin {
    @Inject(method = "getUseDuration", at = @At(value = "RETURN"), cancellable = true)
    public void getUseDuration(ItemStack pStack, CallbackInfoReturnable<Integer> cir) {
        var d = ConfigHelper.Hunger.useDurationMultiplier((Item) (Object) this);
        if (d != 1) {
            cir.setReturnValue((int) (cir.getReturnValue() * d));
        }
    }
}
