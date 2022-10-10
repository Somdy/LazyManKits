package rs.lazymankits.actions.tools;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public abstract class HandCardManipulator {
    /**
     * do something to the card chosen
     * @param card the card the player chooses
     * @param index the index of the card in the list chosen, usually starts at 0
     * @return true if the card should return to hand, false if the card should not return to hand
     */
    public abstract boolean manipulate(AbstractCard card, int index);
    
    public void moveToDrawPile(AbstractCard card, boolean onTop) {
        AbstractPlayer p = AbstractDungeon.player;
        p.drawPile.moveToDeck(card, !onTop);
    }
}