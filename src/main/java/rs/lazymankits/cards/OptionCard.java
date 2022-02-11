package rs.lazymankits.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.abstracts.LMCustomCard;

import java.util.function.Consumer;

public class OptionCard extends LMCustomCard {
    private static int optionID;
    private Consumer<AbstractCard> onChooseThis;
    private int multiplier;
    
    public OptionCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, 
                      CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        onChooseThis = null;
        multiplier = 1;
    }
    
    @NotNull
    public static OptionCard create(String name, String description, String img, Consumer<AbstractCard> onChooseThis) {
        return create(name, description, img, CardColor.COLORLESS, CardRarity.SPECIAL, onChooseThis);
    }
    
    @NotNull
    public static OptionCard create(String name, String description, String img, CardColor color, CardRarity rarity,
                                    Consumer<AbstractCard> onChooseThis) {
        return create(name, description, img, -2, CardType.SKILL, color, rarity, onChooseThis);
    }
    
    @NotNull
    public static OptionCard create(String name, String description, String img, int cost, CardType type, CardColor color, CardRarity rarity,
                                    Consumer<AbstractCard> onChooseThis) {
        return create(name, description, img, cost, type, color, rarity, null, 0, 0, 0, 0, onChooseThis);
    }
    
    @NotNull
    public static OptionCard create(String name, String description, String img, int cost, CardType type, CardColor color, CardRarity rarity,
                                    AbstractCard preview, int damage, int block, int magics, int mult, Consumer<AbstractCard> onChooseThis) {
        OptionCard opt = new OptionCard(autoID(name), name, img, cost, description, type, color, rarity, CardTarget.NONE);
        opt.cardsToPreview = preview;
        opt.setDamageValue(damage, true);
        opt.setBlockValue(block, true);
        opt.setMagicValue(magics, true);
        opt.multiplier = Math.max(mult, 1);
        opt.onChooseThis = onChooseThis;
        opt.initializeTitle();
        opt.initializeDescription();
        return opt;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
    
    @NotNull
    private static String autoID(String name) {
        int r = optionID;
        optionID++;
        return name + " : Template_" + r;
    }

    @Override
    public void onChoseThisOption() {
        if (onChooseThis != null) {
            for (int i = 0; i < multiplier; i++)
                onChooseThis.accept(this);
        }
    }

    @Override
    public void upgrade() {}

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {}
}