package rs.lazymankits.actions.utility;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.actions.CustomDmgInfo;

import java.util.function.Consumer;

public class DamageCallbackBuilder extends LMCustomGameAction {
    private Consumer<AbstractCreature> callback;
    
    public DamageCallbackBuilder(AbstractCreature target, CustomDmgInfo info, AttackEffect effect, Consumer<AbstractCreature> dowhat) {
        this.target = target;
        this.info = info;
        this.attackEffect = effect;
        this.callback = dowhat;
        duration = startDuration = Settings.ACTION_DUR_XFAST;
        actionType = ActionType.DAMAGE;
    }
    
    @Override
    public void update() {
        if (this.duration == startDuration) {

            if (info.type != DamageInfo.DamageType.THORNS && info.owner != null && (
                    info.owner.isDying || info.owner.halfDead)) {
                this.isDone = true;
                return;
            }

            if (info.owner == null) {
                LMDebug.Log(this, "dealing no source damage to " + target.name);
            }

            AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, attackEffect));
        }
        tickDuration();
        if (isDone) {
            if (attackEffect == AbstractGameAction.AttackEffect.POISON) {
                target.tint.color.set(Color.CHARTREUSE.cpy());
                target.tint.changeColor(Color.WHITE.cpy());
            } else if (attackEffect == AbstractGameAction.AttackEffect.FIRE) {
                target.tint.color.set(Color.RED);
                target.tint.changeColor(Color.WHITE.cpy());
            }
            if (!target.isDeadOrEscaped()) {
                target.damage(info);
                callback.accept(target);
            }
            if ((AbstractDungeon.getCurrRoom()).monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
    }
}