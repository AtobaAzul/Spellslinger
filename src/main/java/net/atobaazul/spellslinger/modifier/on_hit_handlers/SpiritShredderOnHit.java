package net.atobaazul.spellslinger.modifier.on_hit_handlers;

import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.modifier.PostHitEffect;
import io.redspace.irons_artifice.utils.Utils;
import net.atobaazul.spellslinger.registry.SpellslingerMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;

public class SpiritShredderOnHit implements PostHitEffect {
    @Override
    public void postHit(ServerLevel serverLevel, Bullet bullet, HitResult hitResult, Entity entity) {
        if (!Utils.canHarm(bullet.getOwner(), entity)) {
            return;
        }

        if (entity instanceof LivingEntity living) {
            MobEffectInstance effect = living.getEffect(SpellslingerMobEffects.SPIRIT_SHRED);

            if (effect != null) {
                int level = effect.getAmplifier();
                living.removeEffect(SpellslingerMobEffects.SPIRIT_SHRED);
                living.addEffect(new MobEffectInstance(SpellslingerMobEffects.SPIRIT_SHRED, 15 * 20, Math.min(level + 1, 5)));
            } else {
                living.addEffect(new MobEffectInstance(SpellslingerMobEffects.SPIRIT_SHRED, 15 * 20));
            }
        }
    }
}
