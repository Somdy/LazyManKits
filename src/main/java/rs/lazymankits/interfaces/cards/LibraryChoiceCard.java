package rs.lazymankits.interfaces.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface LibraryChoiceCard {
    
    default void apply(AbstractCard card) {}
    
    default boolean practical() {
        return true;
    }
}