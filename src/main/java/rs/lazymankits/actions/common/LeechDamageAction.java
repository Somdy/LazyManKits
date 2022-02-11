package rs.lazymankits.actions.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.BiteEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.actions.CustomDmgInfo;

public class LeechDamageAction extends LMCustomGameAction {
    private AttackEffect effect;
    private boolean useBiteEffect;
    private Color biteColor;

    public LeechDamageAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect, boolean useBiteEffect) {
        this.target = target;
        this.source = info.owner;
        this.info = info;
        this.effect = effect;
        this.useBiteEffect = useBiteEffect;
        biteColor = Color.RED.cpy();
        actionType = ActionType.DAMAGE;
        duration = startDuration = Settings.ACTION_DUR_FAST;
    }

    public LeechDamageAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect) {
        this(target, info, effect, true);
    }

    public LeechDamageAction setBiteColor(Color color) {
        this.biteColor = color;
        return this;
    }

    @Override
    public void update() {
        if (duration == startDuration) {
            if (useBiteEffect)
                effectToList(new BiteEffect(target.hb.cX, target.hb.cY - scale(40F), biteColor));
            effectToList(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, effect));
            target.damage(info);
            if (target.lastDamageTaken > 0) {
                int healAmt = MathUtils.floor(target.lastDamageTaken * info.source.leechFactor());
                addToTop(new HealAction(source, source, healAmt));
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        tickDuration();
    }
}