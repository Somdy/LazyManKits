package rs.lazymankits.interfaces.powers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface CardPlayablePower {
    boolean canUseCard(AbstractCard card, AbstractPlayer p, AbstractMonster m);
}
