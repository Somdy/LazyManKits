package rs.lazymankits.interfaces.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface CardTauntPower {
    /**
     * triggers when player tries to play a card at any monster except the owner of power
     * @param card the card player tries to use
     * @param p the player
     * @param m the target of the card, usually refers to the monster player tries to play at
     * @return true if the card can be used
     * @apiNote to prevent the card can be used on the owner, use {@code CardProofPower} instead of this
     * @see CardProofPower
     */
    boolean canPlayerUseCardAtOthers(AbstractCard card, AbstractPlayer p, AbstractMonster m);
}