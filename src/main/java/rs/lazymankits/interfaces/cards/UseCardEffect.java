package rs.lazymankits.interfaces.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

@FunctionalInterface
public interface UseCardEffect {
    void use(AbstractPlayer p, AbstractMonster m);
}