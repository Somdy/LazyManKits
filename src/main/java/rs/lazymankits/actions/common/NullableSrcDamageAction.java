package rs.lazymankits.actions.common;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.actions.CustomDmgInfo;

public class NullableSrcDamageAction extends LMCustomGameAction {
    private int goldAmount;
    private static final float DURATION = 0.1F;
    private static final float POST_ATTACK_WAIT_DUR = 0.1F;
    private boolean skipWait;
    private boolean muteSfx;
    private boolean useBiteEffect;
    private Color biteColor;

    public NullableSrcDamageAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect, boolean useBiteEffect) {
        this.goldAmount = 0;
        this.skipWait = false;
        this.muteSfx = false;
        this.info = info;
        setValues(target, info);
        this.actionType = AbstractGameAction.ActionType.DAMAGE;
        this.attackEffect = effect;
        this.useBiteEffect = useBiteEffect;
        biteColor = Color.RED.cpy();
        this.duration = 0.1F;
    }

    public NullableSrcDamageAction(AbstractCreature target, CustomDmgInfo info) { this(target, info, AttackEffect.NONE, false); }

    public NullableSrcDamageAction(AbstractCreature target, CustomDmgInfo info, boolean superFast) {
        this(target, info, AttackEffect.NONE, false);
        this.skipWait = superFast;
    }

    public NullableSrcDamageAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect) {
        this(target, info, effect, false);
        this.skipWait = false;
        this.muteSfx = false;
    }

    public NullableSrcDamageAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect, boolean useBiteEffect, boolean superFast) {
        this(target, info, effect, useBiteEffect);
        this.skipWait = superFast;
    }

    public NullableSrcDamageAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect, boolean useBiteEffect, boolean superFast, boolean muteSfx) {
        this(target, info, effect, useBiteEffect);
        this.skipWait = superFast;
        this.muteSfx = muteSfx;
    }

    public NullableSrcDamageAction setBiteColor(Color color) {
        this.biteColor = color;
        return this;
    }

    @Override
    public void update() {
        if (this.duration == DURATION) {

            if (info.type != DamageInfo.DamageType.THORNS && info.owner != null && (
                    info.owner.isDying || info.owner.halfDead)) {
                this.isDone = true;
                return;
            }

            if (info.owner == null) {
                LMDebug.Log(this, "dealing no source damage to " + target.name);
            }

            AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, attackEffect, muteSfx));

            if (this.goldAmount != 0 && info.owner != null) {
                stealGold();
            }
        }

        this.tickDuration();
        if (this.isDone) {
            if (attackEffect == AbstractGameAction.AttackEffect.POISON) {
                target.tint.color.set(Color.CHARTREUSE.cpy());
                target.tint.changeColor(Color.WHITE.cpy());
            } else if (attackEffect == AbstractGameAction.AttackEffect.FIRE) {
                target.tint.color.set(Color.RED);
                target.tint.changeColor(Color.WHITE.cpy());
            }
            if (info.source.isLeech()) {
                addToTop(new LeechDamageAction(target, info, AttackEffect.NONE, useBiteEffect).setBiteColor(biteColor));
                return;
            }

            if (!target.isDeadOrEscaped()) {
                target.damage(info);
            }

            if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
            if (!this.skipWait && !Settings.FAST_MODE) {
                addToTop(new WaitAction(POST_ATTACK_WAIT_DUR));
            }
        }
    }

    private void stealGold() {
        if (this.target.gold == 0) {
            return;
        }

        CardCrawlGame.sound.play("GOLD_JINGLE");
        if (this.target.gold < this.goldAmount) {
            this.goldAmount = this.target.gold;
        }

        this.target.gold -= this.goldAmount;
        for (int i = 0; i < this.goldAmount; i++) {
            if (source.isPlayer) {
                AbstractDungeon.effectList.add(new GainPennyEffect(target.hb.cX, target.hb.cY));
            } else {
                AbstractDungeon.effectList.add(new GainPennyEffect(source, target.hb.cX, target.hb.cY, source.hb.cX, source.hb.cY, false));
            }
        }
    }
}