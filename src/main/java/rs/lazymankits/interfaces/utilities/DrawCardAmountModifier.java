package rs.lazymankits.interfaces.utilities;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface DrawCardAmountModifier {
    /**
     * Modify draw card amount whenever a DrawCardAction is called. Remember that the return value will be added to original amount
     * @param s the source creature of the action
     * @param drawAmt the origin draw amount that can be modified by other modifiers ahead
     * @param endTurnDraw if this draw is turn-starting draw
     * @return the increment or decrement
     */
    int modifyDrawAmount(AbstractCreature s, int drawAmt, boolean endTurnDraw);
}