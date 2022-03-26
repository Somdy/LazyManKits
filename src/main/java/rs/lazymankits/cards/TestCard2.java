package rs.lazymankits.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import rs.lazymankits.LMDebug;
import rs.lazymankits.actions.common.DrawExptCardAction;
import rs.lazymankits.data.LMXCardDataReader;

public class TestCard2 extends LMXDataCustomCard {
    public static CardStrings cardStrings;

    public TestCard2() {
        super(LMXCardDataReader.LMKExample, 2, "SharedAssets/images/cards/wild.png", CardColor.COLORLESS);
        cardStrings = CardCrawlGame.languagePack.getCardStrings(cardID);
        rawDescription = cardStrings.DESCRIPTION;
        name = cardStrings.NAME;
        initializeDescription();
        initializeTitle();
    }

    @Override
    public void upgrade() {
        
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawExptCardAction(3, c -> c.type == CardType.POWER, new AbstractGameAction() {
            @Override
            public void update() {
                isDone = true;
                for (AbstractCard card : DrawCardAction.drawnCards) {
                    card.setCostForTurn(0);
                }
            }
        }));
    }
}