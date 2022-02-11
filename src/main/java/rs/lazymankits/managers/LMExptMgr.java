package rs.lazymankits.managers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.MinionPower;

import java.util.function.Predicate;

public final class LMExptMgr {
    public static final Predicate<AbstractCreature> FATAL_JUGDE = c -> (c.currentHealth <= 0 || c.isDying)
            && !c.halfDead && !c.hasPower(MinionPower.POWER_ID);
    
    public static final Predicate<AbstractCreature> ALL_DAMAGE_BLOCKED = c -> c.lastDamageTaken <= 0;
    
    public static final Predicate<AbstractCreature> ALL_DAMAGE_UNBLOCKED = c -> c.lastDamageTaken > 0;
}