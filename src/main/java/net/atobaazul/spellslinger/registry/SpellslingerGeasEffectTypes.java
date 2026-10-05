package net.atobaazul.spellslinger.registry;

import com.sammy.malum.core.systems.geas.GeasEffectType;
import com.sammy.malum.core.systems.registry.DeferredGeasTypes;
import com.sammy.malum.core.systems.registry.GeasHolder;
import net.atobaazul.spellslinger.Spellslinger;
import net.atobaazul.spellslinger.geas.MarksmanGeas;
import net.atobaazul.spellslinger.geas.SpellslingerGeas;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import static com.sammy.malum.registry.common.magic.MalumSpiritTypes.*;

public class SpellslingerGeasEffectTypes {
    public static final DeferredGeasTypes GEAS_TYPES = DeferredGeasTypes.create(Spellslinger.MODID);

    public static final GeasHolder<GeasEffectType> PACT_OF_THE_SPELLSLINGER = GEAS_TYPES.register("pact_of_the_spellslinger", () -> new GeasEffectType(SpellslingerGeas::new, INFERNAL_SPIRIT, ARCANE_SPIRIT, ELDRITCH_SPIRIT));
    public static final GeasHolder<GeasEffectType> PACT_OF_THE_MARKSMAN = GEAS_TYPES.register("pact_of_the_marksman", () -> new GeasEffectType(MarksmanGeas::new, AERIAL_SPIRIT, INFERNAL_SPIRIT));
}
