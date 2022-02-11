package rs.lazymankits.actions.common;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.MinionPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.actions.CustomDmgInfo;
import rs.lazymankits.actions.utility.DamageCallbackBuilder;
import rs.lazymankits.managers.LMExptMgr;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DamageAndWhenFatalAction extends LMCustomGameAction {
    private AttackEffect effect;
    private Consumer<AbstractCreature> dowhat;
    private Predicate<AbstractCreature> fatalJudge;

    public DamageAndWhenFatalAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect, Consumer<AbstractCreature> dowhat,
                                Predicate<AbstractCreature> fatalJudge) {
        this.target = target;
        this.info = info;
        this.effect = effect;
        this.dowhat = dowhat;
        this.fatalJudge = fatalJudge;
        actionType = ActionType.DAMAGE;
        duration = startDuration = Settings.ACTION_DUR_XFAST;
    }

    public DamageAndWhenFatalAction(AbstractCreature target, CustomDmgInfo info, AttackEffect effect, Consumer<AbstractCreature> dowhat) {
        this(target, info, effect, dowhat, LMExptMgr.FATAL_JUGDE);
    }

    public DamageAndWhenFatalAction(AbstractCreature target, CustomDmgInfo info, Consumer<AbstractCreature> dowhat) {
        this(target, info, AttackEffect.NONE, dowhat, LMExptMgr.FATAL_JUGDE);
    }

    @Override
    public void update() {
        addToTop(new DamageCallbackBuilder(target, info, effect, crt -> {
            if (fatalJudge.test(crt))
                dowhat.accept(crt);
        }));
        isDone = true;
    }
}