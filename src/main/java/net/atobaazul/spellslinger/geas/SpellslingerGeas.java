package net.atobaazul.spellslinger.geas;

import com.google.common.collect.Multimap;
import com.sammy.malum.core.systems.geas.GeasEffect;
import com.sammy.malum.registry.common.MalumAttributes;
import net.atobaazul.spellslinger.registry.SpellslingerGeasEffectTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.TooltipFlag;

import java.util.function.Consumer;

public class SpellslingerGeas extends GeasEffect {
    public SpellslingerGeas() {
        super(SpellslingerGeasEffectTypes.PACT_OF_THE_SPELLSLINGER.get());
    }

    @Override
    public void addTooltipComponents(LivingEntity entity, Consumer<Component> tooltipAcceptor, TooltipFlag tooltipFlag) {
        tooltipAcceptor.accept(Component.translatable("spellslinger.effect.geas.spellslinger").withStyle(ChatFormatting.BLUE));
        super.addTooltipComponents(entity, tooltipAcceptor, tooltipFlag);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> createAttributeModifiers(LivingEntity entity, Multimap<Holder<Attribute>, AttributeModifier> modifiers) {
        addAttributeModifier(modifiers, MalumAttributes.CHARGE_RECOVERY_RATE, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(modifiers, Attributes.ATTACK_DAMAGE, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        return modifiers;
    }
}
