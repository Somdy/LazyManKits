package rs.lazymankits.interfaces.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import rs.lazymankits.interfaces.LMSubscriberInterface;

public interface ModifyBlockModifier extends LMSubscriberInterface {
    float modifyBlock(float blockamt, AbstractCard card);
}