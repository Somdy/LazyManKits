package rs.lazymankits.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import rs.lazymankits.LMDebug;
import rs.lazymankits.LManager;
import rs.lazymankits.actions.common.DrawExptCardAction;
import rs.lazymankits.data.LMXCardDataReader;
import rs.lazymankits.interfaces.cards.BranchableUpgradeCard;
import rs.lazymankits.interfaces.cards.UpgradeBranch;

import java.util.ArrayList;
import java.util.List;

public class TestCard extends LMXDataCustomCard implements BranchableUpgradeCard {
    public static CardStrings cardStrings;
    
    public TestCard() {
        super(LMXCardDataReader.LMKExample, 1, "SharedAssets/images/cards/wild.png", CardColor.COLORLESS);
        cardStrings = CardCrawlGame.languagePack.getCardStrings(cardID);
        rawDescription = cardStrings.DESCRIPTION;
        name = cardStrings.NAME;
        initializeDescription();
        initializeTitle();
    }

    @Override
    public void upgrade() {
        possibleBranches().get(chosenBranch()).upgrade();
    }
    
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawExptCardAction(2, c -> isCardTypeOf(c, CardType.SKILL)));
    }

    @Override
    public List<UpgradeBranch> possibleBranches() {
        return new ArrayList<UpgradeBranch>() {{
            add(() -> {
                upgradeName();
                rawDescription = cardStrings.EXTENDED_DESCRIPTION[1];
                initializeDescription();
            });
            add(() -> {
                upgradeName();
                rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
                initializeDescription();
            });
            add(() -> {
                upgradeName();
                rawDescription = cardStrings.EXTENDED_DESCRIPTION[2];
                initializeDescription();
            });
        }};
    }

    @Override
    public int defaultBranch() {
        return 0;
    }
}