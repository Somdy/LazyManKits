package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

public interface OnCardPlayedSubscriber extends LMSubscriberInterface {
    void receiveOnCardPlayed(AbstractCard card, UseCardAction action);
}