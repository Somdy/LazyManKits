package rs.lazymankits.listeners.tools;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;

@FunctionalInterface
public interface PlayCardEvent {
    void onPlayCard(AbstractCard card, AbstractCreature target, int energyOnUse);
}