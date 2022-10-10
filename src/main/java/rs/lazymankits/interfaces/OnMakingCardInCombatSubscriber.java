package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

public interface OnMakingCardInCombatSubscriber extends LMSubscriberInterface {
    void receiveOnMakingCardInCombat(AbstractCard card, CardGroup destination);
}