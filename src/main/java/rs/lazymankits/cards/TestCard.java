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
        rawDescription = "伤害： !D! NL 格挡 ： !B! NL 特殊值： !M! .";
        name = cardStrings.NAME;
        setDamageValue(0, true);
        setBlockValue(0, true);
        setMagicValue(0, true);
        initializeDescription();
        initializeTitle();
    }

    @Override
    public void upgrade() {
        possibleBranches().get(chosenBranch()).upgrade();
    }
    
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (!upgraded) {
            // 未升级时的效果
        }
        switch (chosenBranch()) {
            case 0:
                // 默认分支的效果
                break;
            case 1:
                // 分支——1的效果
                break;
            case 2:
                // 分支——2的效果
                break;
            case 3:
                // 分支——3的效果
                break;
        }
    }

    @Override
    public List<UpgradeBranch> possibleBranches() {
        return new ArrayList<UpgradeBranch>() {{
            add(() -> {
                if (!upgraded) {
                    upgradeName("默认升级");
                    rawDescription = "默认升级 NL 伤害： !D! NL 格挡 ： !B! NL 特殊值： !M! .";
                    upgradeDamage(1);
                    upgradeBlock(1);
                    upgradeMagicNumber(1);
                    initializeDescription();
                }
            });
            add(() -> {
                if (!upgraded) {
                    upgradeName("分支——1");
                    rawDescription = "伤害： !D! NL 格挡 ： !B! NL 特殊值： !M! .";
                    upgradeDamage(0);
                    upgradeBlock(0);
                    upgradeMagicNumber(2);
                    initializeDescription();
                }
            });
            add(() -> {
                if (!upgraded) {
                    upgradeName("分支——2");
                    rawDescription = "伤害： !D! NL 格挡 ： !B! NL 特殊值： !M! .";
                    upgradeDamage(2);
                    upgradeBlock(2);
                    upgradeMagicNumber(0);
                    initializeDescription();
                }
            });
            add(() -> {
                if (!upgraded) {
                    upgradeName("分支——3");
                    rawDescription = "伤害： !D! NL 格挡 ： !B! NL 特殊值： !M! .";
                    upgradeDamage(3);
                    upgradeBlock(3);
                    upgradeMagicNumber(3);
                    initializeDescription();
                }
            });
        }};
    }
    
    protected void upgradeName(String name) {
        timesUpgraded++;
        upgraded = true;
        this.name = name;
        this.initializeTitle();
    }
}