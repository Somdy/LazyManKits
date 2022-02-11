package rs.lazymankits.interfaces.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface CardProofPower {
    /**
     * triggers when player tries to play a card at the owner of power
     * @param card the card player tries to use
     * @param p the player
     * @param m the target of the card, usually refers to the owner of power
     * @return true if the card can be used
     */
    boolean canPlayerUseCard(AbstractCard card, AbstractPlayer p, AbstractMonster m);
}