package rs.lazymankits.actions.utility;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.actions.tools.HandCardManipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SimpleHandCardSelectBuilder extends LMCustomGameAction {
    private String msg;
    private HandCardManipulator cm;
    private HandCardManipulator cmForRemoveList;
    private boolean shouldMatchAll;
    private boolean anyNumber;
    private boolean canPickZero;
    private boolean forUpgrade;
    private Predicate<AbstractCard>[] predicators;
    private List<AbstractCard> removeList;
    private boolean cardsReturned;
    private AbstractGameAction followUpAction;

    @SafeVarargs
    public SimpleHandCardSelectBuilder(String msg, HandCardManipulator cm, boolean shouldMatchAll, boolean anyNumber,
                                       boolean canPickZero, @NotNull Predicate<AbstractCard>... predicators) {
        this.msg = msg;
        this.cm = cm;
        this.cmForRemoveList = null;
        this.shouldMatchAll = shouldMatchAll;
        this.anyNumber = anyNumber;
        this.canPickZero = canPickZero;
        this.forUpgrade = false;
        this.predicators = predicators;
        removeList = new ArrayList<>();
        cardsReturned = false;
        followUpAction = null;
        actionType = ActionType.CARD_MANIPULATION;
        duration = startDuration = Settings.ACTION_DUR_FAST;
    }

    @SafeVarargs
    public SimpleHandCardSelectBuilder(String msg, HandCardManipulator cm, boolean shouldMatchAll, @NotNull Predicate<AbstractCard>... predicators) {
        this(msg, cm, shouldMatchAll, false, false, predicators);
    }

    @SafeVarargs
    public SimpleHandCardSelectBuilder(HandCardManipulator cm, @NotNull Predicate<AbstractCard>... predicators) {
        this(null, cm, false, false, false, predicators);
    }

    @SafeVarargs
    public SimpleHandCardSelectBuilder(@NotNull Predicate<AbstractCard>... predicators) {
        this(null, null, false, false, false, predicators);
    }

    public SimpleHandCardSelectBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public SimpleHandCardSelectBuilder setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public SimpleHandCardSelectBuilder setManipulator(HandCardManipulator cm) {
        this.cm = cm;
        return this;
    }

    public SimpleHandCardSelectBuilder setManipulatorForRemoveList(HandCardManipulator cm) {
        this.cmForRemoveList = cm;
        return this;
    }

    public SimpleHandCardSelectBuilder setAnyNumber(boolean anyNumber) {
        this.anyNumber = anyNumber;
        return this;
    }

    public SimpleHandCardSelectBuilder setCanPickZero(boolean canPickZero) {
        this.canPickZero = canPickZero;
        return this;
    }

    public SimpleHandCardSelectBuilder setForUpgrade(boolean forUpgrade) {
        this.forUpgrade = forUpgrade;
        return this;
    }

    public SimpleHandCardSelectBuilder setShouldMatchAll(boolean shouldMatchAll) {
        this.shouldMatchAll = shouldMatchAll;
        return this;
    }

    public SimpleHandCardSelectBuilder setFollowUpAction(AbstractGameAction followUpAction) {
        this.followUpAction = followUpAction;
        return this;
    }

    @Override
    public void update() {
        if (duration == startDuration) {
            AbstractPlayer p = AbstractDungeon.player;
            if (p.hand.isEmpty()) {
                isDone = true;
                return;
            }
            removeList.addAll(p.hand.group);
            List<AbstractCard> notToRemove = new ArrayList<>();
            for (AbstractCard card : removeList) {
                int unmatches = 0;
                boolean anyMatched = false;
                for (Predicate<AbstractCard> predicator : predicators) {
                    if (!predicator.test(card))
                        unmatches++;
                    else if (predicator.test(card))
                        anyMatched = true;
                }
                if (shouldMatchAll && unmatches <= 0)
                    notToRemove.add(card);
                else if (anyMatched)
                    notToRemove.add(card);
            }
            if (notToRemove.isEmpty()) {
                LMDebug.Log("No cards matched???");
                isDone = true;
                return;
            }
            removeList.removeAll(notToRemove);
            if (removeList.size() == p.hand.group.size()) {
                LMDebug.Log("No cards match the conditions.");
                isDone = true;
                return;
            }
            if (p.hand.group.size() - removeList.size() <= amount && !anyNumber) {
                int index = 0;
                List<AbstractCard> tmp = new ArrayList<>();
                for (AbstractCard card : p.hand.group) {
                    if (notToRemove.contains(card)) {
                        if (cm.manipulate(card, index)) {
                            LMDebug.Log("Returning the selected card to hand: " + card.name);
                        } else {
                            tmp.add(card);
                        }
                        index++;
                    }
                }
                p.hand.group.removeAll(tmp);
                isDone = true;
                return;
            }
            p.hand.group.removeAll(removeList);
            if (p.hand.group.size() > 1) {
                LMDebug.Log("Opening hand card select screen...");
                AbstractDungeon.handCardSelectScreen.open(msg, amount, anyNumber, canPickZero, false, forUpgrade);
                tickDuration();
            } else if (p.hand.group.size() == 1) {
                LMDebug.Log("Only one card left, manipulating to " + p.hand.getTopCard());
                if (cm.manipulate(p.hand.getTopCard(), 0)) {
                    LMDebug.Log("Returning the selected card to hand: " + p.hand.getTopCard().name);
                    AbstractDungeon.player.hand.addToTop(p.hand.getTopCard());
                }
                returnCards();
                isDone = true;
                return;
            }
        }
        if (!AbstractDungeon.handCardSelectScreen.selectedCards.isEmpty()) {
            int index = 0;
            for (AbstractCard card : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                if (cm.manipulate(card, index)) {
                    LMDebug.Log("Returning the selected card to hand: " + card.name);
                    AbstractDungeon.player.hand.addToTop(card);
                }
                index++;
            }
            returnCards();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
        }
        tickDuration();
        if (isDone) {
            if (cmForRemoveList != null && !removeList.isEmpty()) {
                int index = 0;
                for (AbstractCard card : removeList) {
                    if (cmForRemoveList.manipulate(card, index)) {
                        LMDebug.Log("Returning the selected card to hand: " + card.name);
                        AbstractDungeon.player.hand.addToTop(card);
                    }
                    index++;
                }
            }
            if (!cardsReturned)
                returnCards();
            removeList.clear();
            if (followUpAction != null) {
                addToTop(followUpAction);
            }
        }
    }

    private void returnCards() {
        if (!removeList.isEmpty()) {
            for (AbstractCard card : removeList) {
                AbstractDungeon.player.hand.addToTop(card);
            }
            AbstractDungeon.player.hand.refreshHandLayout();
            cardsReturned = true;
        }
    }
}