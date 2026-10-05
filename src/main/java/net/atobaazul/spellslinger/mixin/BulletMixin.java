package net.atobaazul.spellslinger.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sammy.malum.client.screen.codex.BookEntry;
import com.sammy.malum.core.handlers.GeasEffectHandler;
import io.redspace.irons_artifice.entity.Bullet;
import net.atobaazul.spellslinger.geas.MarksmanGeas;
import net.atobaazul.spellslinger.registry.SpellslingerGeasEffectTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import team.lodestar.lodestone.registry.common.LodestoneAttributes;

@Mixin(Bullet.class)
public abstract class BulletMixin extends Projectile {
    @Unique
    private Vec3 startingPos;
    @Unique
    private Integer age = 0;

    protected BulletMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @WrapMethod(remap = false, method = "resolveDamage")
    private float spellslinger$resolveDamage(Operation<Float> original) {
        float marksmanMult = 1f;
        if (this.getOwner() instanceof LivingEntity entity) {
            var marksmanGeas = GeasEffectHandler.getGeasEffect(entity, SpellslingerGeasEffectTypes.PACT_OF_THE_MARKSMAN);
            double magicProficiency = entity.getAttribute(LodestoneAttributes.MAGIC_PROFICIENCY.getDelegate()).getValue();

            double dist = this.position().distanceTo(this.startingPos);
            if (dist > 20.0) {
                double actualMult = Mth.lerp(dist / 20, 1, 2 * magicProficiency);

                if (marksmanGeas instanceof MarksmanGeas) {
                    marksmanMult = (float) actualMult;
                }
            }
        }
        return original.call() * marksmanMult;
    }

    @WrapMethod(method = "tick")
    private void spellslinger$tick(Operation<Void> original) {
        this.age++;

        if (age == 1) {
            this.startingPos = this.position();
        }
        original.call();
    }
}
