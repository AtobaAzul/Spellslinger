package net.atobaazul.spellslinger.modifier.on_hit_handlers;

import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.modifier.PostHitEffect;
import io.redspace.irons_artifice.utils.Utils;
import net.atobaazul.spellslinger.registry.SpellslingerDataAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import team.lodestar.lodestone.helpers.DamageTypeHelper;

public class SoulshotOnHit implements PostHitEffect {
    @Override
    public void postHit(ServerLevel serverLevel, Bullet bullet, HitResult hitResult, Entity entity) {
        if (!Utils.canHarm(bullet.getOwner(), entity)) {
            return;
        }

        if (!(bullet.getOwner() instanceof LivingEntity livingOwner)) {
            return;
        }
        if (entity instanceof LivingEntity living) {
            //Instead of damaging directly, we pool the damage to hurt the entity later on the next tick.
            float incomingDamage = living.getData(SpellslingerDataAttachments.INCOMING_MAGIC_DAMAGE);
            ShotProfile profile = bullet.getProfile();
            incomingDamage = (float) (incomingDamage + 2 / Math.max(1, profile.value(ShotComponents.PROJECTILE_COUNT)));
            living.setData(SpellslingerDataAttachments.INCOMING_MAGIC_DAMAGE.get(), incomingDamage);
        }
    }
}
