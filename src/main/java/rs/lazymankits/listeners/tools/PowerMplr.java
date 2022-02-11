package rs.lazymankits.listeners.tools;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

@FunctionalInterface
public interface PowerMplr {
    /**
     * @param power the power that's being applying to
     * @param target the creature who the power will be applied to
     * @param source the creauture who applies the power to the target creature
     * @return the power whether is manipulated. If the amount of power is 0 or below, the action will be canceled
     */
    AbstractPower manipulate(AbstractPower power, AbstractCreature target, AbstractCreature source);
}