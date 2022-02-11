package rs.lazymankits.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import rs.lazymankits.abstracts.LMCustomCard;
import rs.lazymankits.abstracts.LMLibrary;
import rs.lazymankits.interfaces.cards.LibraryChoiceCard;
import rs.lazymankits.interfaces.cards.UseCardEffect;

import java.util.function.Consumer;

public class LMLibraryChoiceCard extends LMCustomCard implements LibraryChoiceCard {
    public static final String DEFAULT_CHOICE_ID = "LM_LIB_CHOICE";
    private static int AutoID = 1;
    private UseCardEffect use;
    private Runnable onDiscarded;
    private Runnable onDrawn;
    private Consumer<AbstractCard> onOtherCardPlayed;
    
    public LMLibraryChoiceCard(String id, String name, String img, int cost, String rawDescription, CardType type, 
                               CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
    }
    
    public LMLibraryChoiceCard(String name, String img, int cost, String rawDescription, CardType type, CardColor color, 
                               CardRarity rarity, CardTarget target) {
        this(DEFAULT_CHOICE_ID, name, img, cost, rawDescription, type, color, rarity, target);
    }
    
    public LMLibraryChoiceCard(String name, int cost, String rawDescription, LMLibrary.ChoiceDef def) {
        this(DEFAULT_CHOICE_ID, name, def.getImg(), cost, rawDescription, def.getType(), def.getColor(), def.getRarity(), 
                def.getTarget());
    }
    
    public LMLibraryChoiceCard build() {
        if (cardID.equals(DEFAULT_CHOICE_ID)) {
            cardID += "#" + AutoID;
            AutoID++;
        }
        return this;
    }
    
    public static void setAutoID(int autoID) {
        AutoID = autoID;
    }
    
    public LMLibraryChoiceCard setUse(UseCardEffect use) {
        this.use = use;
        return this;
    }
    
    public LMLibraryChoiceCard setOnDiscarded(Runnable onDiscarded) {
        this.onDiscarded = onDiscarded;
        return this;
    }
    
    public LMLibraryChoiceCard setOnDrawn(Runnable onDrawn) {
        this.onDrawn = onDrawn;
        return this;
    }
    
    public LMLibraryChoiceCard setOnOtherCardPlayed(Consumer<AbstractCard> onOtherCardPlayed) {
        this.onOtherCardPlayed = onOtherCardPlayed;
        return this;
    }
    
    @Override
    public boolean canUpgrade() {
        return false;
    }
    
    @Override
    public void upgrade() {}
    
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (use != null)
            use.use(p, m);
    }
    
    @Override
    public void triggerOnManualDiscard() {
        if (onDiscarded != null)
            onDiscarded.run();
    }
    
    @Override
    public void triggerWhenDrawn() {
        if (onDrawn != null)
            onDrawn.run();
    }
    
    @Override
    public void triggerOnOtherCardPlayed(AbstractCard c) {
        if (onOtherCardPlayed != null)
            onOtherCardPlayed.accept(c);
    }
}
