package rs.lazymankits.interfaces.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.apache.commons.lang3.NotImplementedException;

public interface CardProofPower extends CardPlayablePower {
    /**
     * triggers when player tries to play a card at the owner of power
     * @param card the card player tries to use
     * @param p the player
     * @param m the target of the card, usually refers to the owner of power
     * @return true if the card can be used
     * @apiNote {@link CardPlayablePower} is more flexible
     */
    boolean canPlayerUseCard(AbstractCard card, AbstractPlayer p, AbstractMonster m);
    
    @Override
    default boolean canUseCard(AbstractCard card, AbstractPlayer p, AbstractMonster target) {
        if (this instanceof AbstractPower) {
            AbstractCreature owner = ((AbstractPower) this).owner;
            if (owner == target) {
                return canPlayerUseCard(card, p, target);
            }
            return true;
        }
        throw new NotImplementedException(this + " NOT A POWER");
    }
}