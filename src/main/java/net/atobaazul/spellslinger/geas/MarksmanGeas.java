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

public class MarksmanGeas extends GeasEffect {
    public MarksmanGeas() {
        super(SpellslingerGeasEffectTypes.PACT_OF_THE_MARKSMAN.get());
    }

    @Override
    public void addTooltipComponents(LivingEntity entity, Consumer<Component> tooltipAcceptor, TooltipFlag tooltipFlag) {
        tooltipAcceptor.accept(Component.translatable("spellslinger.effect.geas.marskman").withStyle(ChatFormatting.BLUE));
        tooltipAcceptor.accept(Component.translatable("spellslinger.effect.geas.marskman_debuff").withStyle(ChatFormatting.RED));
        super.addTooltipComponents(entity, tooltipAcceptor, tooltipFlag);
    }
}
