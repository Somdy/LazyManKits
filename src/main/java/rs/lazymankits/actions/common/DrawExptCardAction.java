package rs.lazymankits.actions.common;

import basemod.BaseMod;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.NoDrawPower;
import rs.lazymankits.abstracts.LMCustomGameAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DrawExptCardAction extends LMCustomGameAction {
    private boolean shuffleCheck;
    private boolean sorted;
    private boolean discardIncluded;
    private Predicate<AbstractCard> expt;
    
    public DrawExptCardAction(AbstractCreature source, int amount, Predicate<AbstractCard> expt, AbstractGameAction action) {
        this.source = source;
        this.amount = amount;
        this.expt = expt;
        this.followUpAction = action;
        this.discardIncluded = true;
        this.shuffleCheck = false;
        this.sorted = false;
        actionType = ActionType.SPECIAL;
        duration = startDuration = Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST;
    }
    
    public DrawExptCardAction(AbstractCreature source, int amount, Predicate<AbstractCard> expt) {
        this(source, amount, expt, null);
    }
    
    public DrawExptCardAction(int amount, Predicate<AbstractCard> expt) {
        this(AbstractDungeon.player, amount, expt, null);
    }
    
    public DrawExptCardAction discardPileNotIncluded() {
        this.discardIncluded = false;
        return this;
    }

    @Override
    public void update() {
        if (cpr().hasPower(NoDrawPower.POWER_ID)) {
            cpr().getPower(NoDrawPower.POWER_ID).flash();
            isDone = true;
            executeFollowUpAction();
            return;
        }
        if (amount < 0 || expt == null) {
            isDone = true;
            executeFollowUpAction();
            return;
        }
        int drawsize = countSpecificCards(cpr().drawPile, expt);
        int discardsize = discardIncluded ? countSpecificCards(cpr().discardPile, expt) : 0;
        if (!SoulGroup.isActive()) {
            if (drawsize + discardsize == 0) {
                isDone = true;
                executeFollowUpAction();
                return;
            }
            if (cpr().hand.size() >= BaseMod.MAX_HAND_SIZE) {
                cpr().createHandIsFullDialog();
                isDone = true;
                executeFollowUpAction();
                return;
            }
            if (!shuffleCheck) {
                int delta;
                if (amount + cpr().hand.size() > BaseMod.MAX_HAND_SIZE) {
                    delta = BaseMod.MAX_HAND_SIZE - (amount + cpr().hand.size());
                    amount += delta;
                    cpr().createHandIsFullDialog();
                }
                if (amount > drawsize) {
                    if (!discardIncluded) {
                        amount = drawsize;
                    }
                    else {
                        delta = amount - drawsize;
                        addToTop(new DrawExptCardAction(delta, expt).setFollowupAction(followUpAction));
                        addToTop(new EmptyDeckShuffleAction());
                    }
                    if (drawsize > 0) {
                        addToTop(new DrawExptCardAction(drawsize, expt).setFollowupAction(followUpAction));
                    }
                    amount = 0;
                    isDone = true;
                    return;
                }
                shuffleCheck = true;
            }
            duration -= Gdx.graphics.getDeltaTime();
            if (amount > 0 && duration < 0F) {
                if (!sorted) {
                    moveExptCardToTop(amount);
                    sorted = true;
                }
                if (!cpr().drawPile.isEmpty()) {
                    addToTop(new DrawCardAction(amount, followUpAction));
                }
                isDone = true;
            }
        }
    }
    
    private void moveExptCardToTop(int times) {
        boolean located = false;
        int count = 0;
        List<AbstractCard> tmp = new ArrayList<>();
        int start = cpr().drawPile.size() / 2;
        for (int i = cpr().drawPile.size() - 1; i >= cpr().drawPile.size() - start; i--) {
            if (expt.test(cpr().drawPile.group.get(i))) {
                tmp.add(0, cpr().drawPile.group.get(i));
                //LMDebug.Log("Moving " + cpr().drawPile.group.get(i).name + " to top");
                count++;
            }
            if (count >= times) {
                located = true;
                break;
            }
        }
        if (!located) {
            for (int i = start; i >= 0; i--) {
                if (expt.test(cpr().drawPile.group.get(i))) {
                    tmp.add(0, cpr().drawPile.group.get(i));
                    //LMDebug.Log("Moving " + cpr().drawPile.group.get(i).name + " to top");
                    break;
                }
            }
        }
        cpr().drawPile.group.removeAll(tmp);
        cpr().drawPile.group.addAll(tmp);
    }
}