package rs.lazymankits.abstracts;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import rs.lazymankits.LManager;
import rs.lazymankits.actions.CustomDmgInfo;
import rs.lazymankits.actions.DamageSource;
import rs.lazymankits.utils.LMGameGeneralUtils;

public abstract class LMCustomGameAction extends AbstractGameAction implements LMGameGeneralUtils {
    protected AbstractGameAction followUpAction;
    protected float nextStage;
    public CustomDmgInfo info;

    public LMCustomGameAction setFollowupAction(AbstractGameAction action) {
        followUpAction = action;
        return this;
    }
    
    protected void toNextStage() {
        duration -= Gdx.graphics.getDeltaTime();
        nextStage = duration;
    }

    @Override
    protected void tickDuration() {
        super.tickDuration();
        if (isDone) {
            executeWhenJobsDone();
        }
    }

    protected void executeFollowUpAction() {
        if (followUpAction != null)
            addToTop(followUpAction);
    }

    protected void executeWhenJobsDone() {
        executeFollowUpAction();
        nextStage = 0F;
        //LManager.CleanAfterJobsDone();
    }

    protected CustomDmgInfo crtDmgInfo(DamageSource source, int base, DamageInfo.DamageType type) {
        return new CustomDmgInfo(source, base, type);
    }

    protected AbstractPlayer cpr() {
        return AbstractDungeon.player;
    }
}