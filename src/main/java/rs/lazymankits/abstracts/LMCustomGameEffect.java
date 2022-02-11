package rs.lazymankits.abstracts;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public abstract class LMCustomGameEffect extends AbstractGameEffect {
    protected AbstractCreature source;
    protected AbstractCreature target;
}