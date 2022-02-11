package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnGainBlockSubscriber extends LMSubscriberInterface {
    float receiveOnGainBlock(AbstractCreature who, float amoun);
}