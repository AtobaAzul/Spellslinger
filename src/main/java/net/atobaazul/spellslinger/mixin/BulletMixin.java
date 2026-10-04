package net.atobaazul.spellslinger.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sammy.malum.core.handlers.GeasEffectHandler;
import io.redspace.irons_artifice.advancement.ShotRecord;
import io.redspace.irons_artifice.entity.Bullet;
import io.redspace.irons_artifice.gun.ShotProfile;
import net.atobaazul.spellslinger.Spellslinger;
import net.atobaazul.spellslinger.geas.MarksmanGeas;
import net.atobaazul.spellslinger.registry.SpellslingerGeasEffectTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;

@Mixin(Bullet.class)
public abstract class BulletMixin extends Projectile {
    protected BulletMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private Integer age = 0;

    @WrapMethod(remap = false, method = "resolveDamage")
    private float spellslinger$resolveDamage(Operation<Float> original) {
        Spellslinger.LOGGER.info("bullet age: {}", age);
        float marksmanMult = 1;
        if (this.getOwner() instanceof LivingEntity entity) {
            Spellslinger.LOGGER.info("owner: {}", entity);
            var marksmanGeas = GeasEffectHandler.getGeasEffect(entity, SpellslingerGeasEffectTypes.PACT_OF_THE_MARKSMAN);
            double magicProficiency = entity.getAttribute(LodestoneAttributes.MAGIC_PROFICIENCY.getDelegate()).getValue()-1;
            Spellslinger.LOGGER.info("geas: {}", marksmanGeas);
            Spellslinger.LOGGER.info("magicProfficiency: {}", magicProficiency);

            if (marksmanGeas instanceof MarksmanGeas) {
                marksmanMult = (float) (marksmanMult + (age*(0.5+magicProficiency)));
            }

            Spellslinger.LOGGER.info("mult: {}", marksmanMult);
        }
        return original.call()*marksmanMult;
    }

    @WrapMethod(method="tick")
    private void spellslinger$tick(Operation<Void> original) {
        this.age++;
        original.call();
    }
}
