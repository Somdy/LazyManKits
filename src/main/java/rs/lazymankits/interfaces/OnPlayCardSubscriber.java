package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnPlayCardSubscriber extends LMSubscriberInterface {
    void receiveOnPlayCard(AbstractCard card, AbstractCreature target, int energyOnUse);
}