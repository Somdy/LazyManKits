package rs.lazymankits.interfaces.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LibraryTokenCard {
    void accept(AbstractCard choice);
    List<AbstractCard> choices();
    
    default void build() {
        applyPowers();
        if (this instanceof AbstractCard) {
            adjustTarget((AbstractCard) this);
        } else {
            throw new NotImplementedException("NOT IMPLEMENTED ON A CARD");
        }
    }
    
    default void adjustTarget(@NotNull AbstractCard card) {
        AbstractCard.CardTarget first = sortTarget();
        card.target = first;
    }
    
    default AbstractCard.CardTarget sortTarget() {
        if (choices().stream().anyMatch(c -> c.target == AbstractCard.CardTarget.SELF_AND_ENEMY))
            return AbstractCard.CardTarget.SELF_AND_ENEMY;
        if (choices().stream().anyMatch(c -> c.target == AbstractCard.CardTarget.ENEMY))
            return AbstractCard.CardTarget.ENEMY;
        if (choices().stream().anyMatch(c -> c.target == AbstractCard.CardTarget.ALL))
            return AbstractCard.CardTarget.ALL;
        if (choices().stream().anyMatch(c -> c.target == AbstractCard.CardTarget.ALL_ENEMY))
            return AbstractCard.CardTarget.ALL_ENEMY;
        if (choices().stream().anyMatch(c -> c.target == AbstractCard.CardTarget.SELF))
            return AbstractCard.CardTarget.SELF;
        return AbstractCard.CardTarget.NONE;
    }
    
    default void update() {
        updateCardLogic();
    }
    
    default void updateCardLogic() {
        for (AbstractCard choice : choices()) {
            choice.update();
        }
    }
    
    default void applyPowers() {
        for (AbstractCard choice : choices()) {
            choice.applyPowers();
        }
        initializeDescription();
    }
    
    default void calculateCardDamage(AbstractMonster mo) {
        for (AbstractCard choice : choices()) {
            choice.calculateCardDamage(mo);
        }
        initializeDescription();
    }
    
    default void initializeDescription() {
        for (AbstractCard choice : choices()) {
            choice.initializeDescription();
        }
        updateModdedDescription();
    }

    void updateModdedDescription();
    
    default void use(AbstractPlayer p, AbstractMonster m) {
        for (AbstractCard choice : choices()) {
            choice.use(p, m);
        }
    }
}