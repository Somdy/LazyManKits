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
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DrawExptCardAction extends LMCustomGameAction {
    private boolean shuffleCheck;
    private boolean sorted;
    private boolean discardIncluded;
    private boolean clearHistory;
    private Predicate<AbstractCard> expt;
    private TaoKe taokeAction;
    
    public DrawExptCardAction(AbstractCreature source, int amount, Predicate<AbstractCard> expt, AbstractGameAction action) {
        this.source = source;
        this.amount = amount;
        this.expt = expt;
        this.taokeAction = new TaoKe(action);
        this.discardIncluded = true;
        this.shuffleCheck = false;
        this.sorted = false;
        this.clearHistory = true;
        actionType = ActionType.SPECIAL;
    }
    
    public DrawExptCardAction(AbstractCreature source, int amount, Predicate<AbstractCard> expt) {
        this(source, amount, expt, null);
    }
    
    public DrawExptCardAction(int amount, Predicate<AbstractCard> expt) {
        this(AbstractDungeon.player, amount, expt, null);
    }
    
    public DrawExptCardAction(int amount, Predicate<AbstractCard> expt, AbstractGameAction action) {
        this(AbstractDungeon.player, amount, expt, action);
    }
    
    public DrawExptCardAction discardPileNotIncluded() {
        this.discardIncluded = false;
        return this;
    }
    
    public DrawExptCardAction clearHistory(boolean clearHistory) {
        this.clearHistory = clearHistory;
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
        int totalCards = drawsize + discardsize;
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
                if (amount + cpr().hand.size() > BaseMod.MAX_HAND_SIZE) {
                    int delta = BaseMod.MAX_HAND_SIZE - (amount + cpr().hand.size());
                    amount += delta;
                    LMDebug.Log("Manipulated draw amount: " + amount);
                    cpr().createHandIsFullDialog();
                }
                if (amount > totalCards) {
                    int oldAmount = amount;
                    if (!discardIncluded) {
                        amount = totalCards - discardsize;
                    } else {
                        amount = totalCards;
                        addToTop(new DrawExptCardAction(source, amount, expt, taokeAction));
                        addToTop(new EmptyDeckShuffleAction());
                    }
                    LMDebug.Log("Player wants [" + oldAmount + "] draws but has only [" + amount + "] draws");
                    LMDebug.Log("Discard pile included [" + discardIncluded + "]");
                    amount = 0;
                    isDone = true;
                    return;
                }
                shuffleCheck = true;
            }
            if (amount > 0) {
                if (!sorted) {
                    moveExptCardToTop(amount);
                    sorted = true;
                }
                if (!cpr().drawPile.isEmpty()) {
                    addToTop(new DrawCardAction(amount, taokeAction, clearHistory));
                }
            }
            isDone = true;
        }
    }
    
    private void moveExptCardToTop(int times) {
        int count = 0;
        LMDebug.Log("Sort requirements：" + times);
        List<AbstractCard> tmp = new ArrayList<>();
        int start = cpr().drawPile.size() / 2;
        for (int i = cpr().drawPile.size() - 1; i >= 0; i--) {
            if (expt.test(cpr().drawPile.group.get(i))) {
                AbstractCard targetCard = cpr().drawPile.group.get(i);
                tmp.add(0, targetCard);
                count++;
                LMDebug.Log("Moving " + targetCard.name + " to top, left reqs: " + (times - count));
            }
            if (count >= times) {
                LMDebug.Log("All cards located");
                break;
            }
        }
        cpr().drawPile.group.removeAll(tmp);
        cpr().drawPile.group.addAll(tmp);
    }
    
    public static class TaoKe extends LMCustomGameAction {
        private AbstractGameAction action;
    
        private TaoKe(AbstractGameAction action) {
            if (action instanceof TaoKe)
                this.action = ((TaoKe) action).action;
            else 
                this.action = action;
        }
        
        @Override
        public void update() {
            isDone = true;
            if (action != null)
                action.update();
        }
    }
}