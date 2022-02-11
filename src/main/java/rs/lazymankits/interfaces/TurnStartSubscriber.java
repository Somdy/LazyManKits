package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface TurnStartSubscriber extends LMSubscriberInterface {
    void receiveOnTurnStarts(AbstractCreature creature, boolean postDraw);
}