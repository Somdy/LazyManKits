package rs.lazymankits.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.abstracts.LMCustomCard;
import rs.lazymankits.abstracts.LMLibrary;
import rs.lazymankits.interfaces.cards.LibraryChoiceCard;

public class LMLibraryCostCard extends LMCustomCard implements LibraryChoiceCard {
    
    public LMLibraryCostCard(String name, String img, int cost, CardColor color, CardRarity rarity) {
        super(LMLibrary.LibraryCard.DEFAULT_TOKEN_ID + "#cost_" + cost, name, img, cost, "", 
                CardType.SKILL, color, rarity, CardTarget.NONE);
    }
    
    public LMLibraryCostCard(String name, String img, int cost) {
        this(name, img, cost, CardColor.COLORLESS, CardRarity.SPECIAL);
    }
    
    @Override
    public void apply(@NotNull AbstractCard card) {
        card.modifyCostForCombat(-9);
        card.modifyCostForCombat(cost);
    }
    
    @Override
    public boolean practical() {
        return false;
    }
    
    @Override
    public boolean canUpgrade() {
        return false;
    }
    
    @Override
    public void upgrade() {}
    
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}
}