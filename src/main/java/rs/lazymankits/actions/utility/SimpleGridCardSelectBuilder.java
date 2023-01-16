package rs.lazymankits.actions.utility;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.actions.tools.GridCardManipulator;
import rs.lazymankits.annotations.Inencapsulated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class SimpleGridCardSelectBuilder extends LMCustomGameAction {
    private String msg;
    private GridCardManipulator cm;
    @Deprecated
    private boolean shouldMatchAll;
    private CardGroup[] cardGroups;
    private CardGroup tmpGroup;
    private Predicate<AbstractCard> predicate;
    private boolean anyNumber;
    private boolean canCancel;
    private boolean forUpgrade;
    private boolean forTransform;
    private boolean forPurge;
    private boolean displayInOrder;
    private boolean gridOpened;

    @SafeVarargs
    @Deprecated
    public SimpleGridCardSelectBuilder(String msg, GridCardManipulator cm, boolean shouldMatchAll, boolean anyNumber, boolean canCancel,
                                       boolean forUpgrade, boolean forTransform, boolean forPurge, Predicate<AbstractCard>... predicate) {
        this.msg = msg;
        this.cm = cm;
        this.shouldMatchAll = shouldMatchAll;
        this.anyNumber = anyNumber;
        this.canCancel = canCancel;
        this.forUpgrade = forUpgrade;
        this.forTransform = forTransform;
        this.forPurge = forPurge;
        this.predicate = predicate[0];
        if (predicate.length > 1) {
            for (int i = 1; i < predicate.length; i++) {
                this.predicate = this.predicate.or(predicate[i]);
            }
        }
        tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        displayInOrder = false;
        gridOpened = false;
        actionType = ActionType.CARD_MANIPULATION;
        duration = startDuration = Settings.ACTION_DUR_XFAST;
    }
    
    public SimpleGridCardSelectBuilder(String msg, GridCardManipulator cm, boolean anyNumber, boolean canCancel,
                                       boolean forUpgrade, boolean forTransform, boolean forPurge, Predicate<AbstractCard>... predicate) {
        this.msg = msg;
        this.cm = cm;
        this.anyNumber = anyNumber;
        this.canCancel = canCancel;
        this.forUpgrade = forUpgrade;
        this.forTransform = forTransform;
        this.forPurge = forPurge;
        this.predicate = predicate[0];
        if (predicate.length > 1) {
            for (int i = 1; i < predicate.length; i++) {
                this.predicate = this.predicate.or(predicate[i]);
            }
        }
        tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        displayInOrder = false;
        gridOpened = false;
        actionType = ActionType.CARD_MANIPULATION;
        duration = startDuration = Settings.ACTION_DUR_XFAST;
    }

    @SafeVarargs
    @Inencapsulated
    public SimpleGridCardSelectBuilder(String msg, GridCardManipulator cm, boolean shouldMatchAll, boolean anyNumber, boolean canCancel,
                                       Predicate<AbstractCard>... predicate) {
        this(msg, cm, anyNumber, canCancel, false, false, false, predicate);
    }

    @SafeVarargs
    @Inencapsulated
    public SimpleGridCardSelectBuilder(GridCardManipulator cm, Predicate<AbstractCard>... predicate) {
        this(null, cm, false, false, false, false, false, predicate);
    }

    @SafeVarargs
    @Inencapsulated
    public SimpleGridCardSelectBuilder(Predicate<AbstractCard>... predicate) {
        this(null, null, false, false, false, false, false, predicate);
    }

    public SimpleGridCardSelectBuilder setCardGroup(CardGroup... cardGroups) {
        this.cardGroups = cardGroups;
        return this;
    }

    public SimpleGridCardSelectBuilder setDisplayInOrder(boolean displayInOrder) {
        this.displayInOrder = displayInOrder;
        return this;
    }

    public SimpleGridCardSelectBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public SimpleGridCardSelectBuilder setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public SimpleGridCardSelectBuilder setManipulator(GridCardManipulator cm) {
        this.cm = cm;
        return this;
    }

    public SimpleGridCardSelectBuilder setAnyNumber(boolean anyNumber) {
        this.anyNumber = anyNumber;
        return this;
    }

    public SimpleGridCardSelectBuilder setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
        return this;
    }

    @Deprecated
    public SimpleGridCardSelectBuilder setShouldMatchAll(boolean shouldMatchAll) {
        this.shouldMatchAll = shouldMatchAll;
        return this;
    }

    public SimpleGridCardSelectBuilder setForUpgrade(boolean forUpgrade) {
        this.forUpgrade = forUpgrade;
        return this;
    }

    public SimpleGridCardSelectBuilder setForTransform(boolean forTransform) {
        this.forTransform = forTransform;
        return this;
    }

    public SimpleGridCardSelectBuilder setForPurge(boolean forPurge) {
        this.forPurge = forPurge;
        return this;
    }

    @Override
    public void update() {
        if (!gridOpened) {
            gridOpened = true;
            if (cardGroups == null) {
                isDone = true;
                return;
            }
            tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (int i = 0; i < cardGroups.length; i++) {
                if (cardGroups[i].isEmpty()) continue;
                LMDebug.Log("Adding cards in group " + i + " to candidates list");
                for (AbstractCard card : cardGroups[i].group) {
                    if (displayInOrder) tmpGroup.addToBottom(card);
                    else tmpGroup.addToRandomSpot(card);
                }
            }
            LMDebug.Log("Judging if the cards match any condition from [" + tmpGroup.size() + "] cards");
//            List<AbstractCard> removeList = new ArrayList<>();
            tmpGroup.group.removeIf(c -> !predicate.test(c));
            if (tmpGroup.isEmpty()) {
                LMDebug.Log("No cards matched???");
                isDone = true;
                return;
            }
            if (anyNumber) {
                AbstractDungeon.gridSelectScreen.open(tmpGroup, amount, true, msg);
            } else {
                AbstractDungeon.gridSelectScreen.open(tmpGroup, amount, msg, forUpgrade, forTransform, canCancel, forPurge);
            }
        }
        if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && gridOpened) {
            int index = 0;
            for (AbstractCard card : tmpGroup.group) {
                if (AbstractDungeon.gridSelectScreen.selectedCards.contains(card)) {
                    if (cm.manipulate(card, index, tmpGroup)) {
                        //Nothing to do here so far...
                    }
                    index++;
                }
                cpr().hand.refreshHandLayout();
                cpr().hand.applyPowers();
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            cpr().hand.refreshHandLayout();
            isDone = true;
            tmpGroup.clear();
        }
    }
}