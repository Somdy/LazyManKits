package rs.lazymankits.actions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class DamageSource {
    private AbstractCreature source;
    private AbstractCard cardFrom;
    private AbstractRelic relicFrom;
    private float leechFactor;

    public DamageSource(AbstractCreature source, AbstractCard cardFrom, AbstractRelic relicFrom) {
        this.source = source;
        this.cardFrom = cardFrom;
        this.relicFrom = relicFrom;
    }

    public DamageSource(AbstractCreature source, AbstractCard from) {
        this(source, from, null);
    }

    public DamageSource(AbstractCreature source, AbstractRelic from) {
        this(source, null, from);
    }

    public DamageSource(AbstractCreature source) {
        this(source, null, null);
    }

    public AbstractCreature getSource() {
        return source;
    }

    public AbstractCard getCardFrom() {
        return cardFrom;
    }

    public AbstractRelic getRelicFrom() {
        return relicFrom;
    }

    public DamageSource setSource(AbstractCreature source) {
        this.source = source;
        return this;
    }

    public DamageSource setCardFrom(AbstractCard cardFrom) {
        this.cardFrom = cardFrom;
        return this;
    }

    public DamageSource setRelicFrom(AbstractRelic relicFrom) {
        this.relicFrom = relicFrom;
        return this;
    }
    
    public DamageSource setLeechFactor(float leechFactor) {
        this.leechFactor = leechFactor;
        return this;
    }
    
    public boolean isLeech() {
        return leechFactor > 0F;
    }
    
    public float leechFactor() {
        return leechFactor;
    }
}