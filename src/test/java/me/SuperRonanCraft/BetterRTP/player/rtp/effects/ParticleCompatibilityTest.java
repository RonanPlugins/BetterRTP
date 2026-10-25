package me.SuperRonanCraft.BetterRTP.player.rtp.effects;

import org.bukkit.Particle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParticleCompatibilityTest {
    @Test
    void legacyAndModernNamesResolveToTheSameParticle() {
        assertSame(Particle.EXPLOSION_NORMAL, RTPEffect_Particles.resolveParticle("poof"));
        assertSame(Particle.EXPLOSION_NORMAL, RTPEffect_Particles.resolveParticle(" EXPLOSION_NORMAL "));
        assertSame(Particle.EXPLOSION_NORMAL, RTPEffect_Particles.resolveParticle("minecraft:poof"));
        assertSame(Particle.CRIT, RTPEffect_Particles.resolveParticle("crit"));
        assertSame(Particle.FIREWORKS_SPARK, RTPEffect_Particles.resolveParticle("firework"));
    }

    @Test
    void unsupportedParticleDoesNotBreakConfiguration() {
        assertNull(RTPEffect_Particles.resolveParticle("not_a_particle"));
        assertNull(RTPEffect_Particles.resolveParticle(null));
    }
}

