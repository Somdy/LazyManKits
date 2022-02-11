package rs.lazymankits.actions.tools;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

public abstract class GridCardManipulator {
    /**
     * do something to the card chosen
     * @param card the card player chooses
     * @param index the index of the card in the list chosen, usually starts at 0
     * @param group the group where the card is
     * @return useless in grid card selection currently, whatever you want
     */
    public abstract boolean manipulate(AbstractCard card, int index, CardGroup group);
}