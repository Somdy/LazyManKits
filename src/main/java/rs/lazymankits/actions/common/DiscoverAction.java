package rs.lazymankits.actions.common;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.utils.LMSK;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DiscoverAction extends LMCustomGameAction {
    private ArrayList<AbstractCard> cards;
    private Consumer<AbstractCard> dowhat;
    private Predicate<AbstractCard> predicate;
    private boolean autoGenerate;
    private boolean keepAll;
    
    public DiscoverAction(List<? extends AbstractCard> cards, int amount, Consumer<AbstractCard> dowhat) {
        this.cards = new ArrayList<>(cards);
        this.amount = amount;
        this.dowhat = dowhat;
        this.predicate = null;
        this.autoGenerate = false;
        this.keepAll = false;
        actionType = ActionType.CARD_MANIPULATION;
        startDuration = duration = Settings.ACTION_DUR_XFAST;
    }
    
    public DiscoverAction(List<? extends AbstractCard> cards, Consumer<AbstractCard> dowhat) {
        this(cards, 3, dowhat);
    }
    
    public DiscoverAction(List<? extends AbstractCard> cards) {
        this(cards, 3, null);
    }
    
    public DiscoverAction(int amount, Predicate<AbstractCard> predicate, Consumer<AbstractCard> dowhat) {
        this(new ArrayList<>(), amount, dowhat);
        this.predicate = predicate;
        this.autoGenerate = true;
    }
    
    public DiscoverAction(Predicate<AbstractCard> predicate, Consumer<AbstractCard> dowhat) {
        this(3, predicate, dowhat);
    }

    public DiscoverAction(Predicate<AbstractCard> predicate) {
        this(3, predicate, null);
    }
    
    public DiscoverAction keepAll(boolean keepAll) {
        this.keepAll = keepAll;
        return this;
    }
    
    @Override
    public void update() {
        if (duration == startDuration) {
            if (predicate != null && autoGenerate) {
                cards = new ArrayList<>();
                cards.addAll(Genereate(amount, predicate));
                autoGenerate = false;
            }
            if (cards == null || cards.isEmpty()) {
                isDone = true;
                return;
            }
            if (keepAll) {
                int remains = BaseMod.MAX_HAND_SIZE - cpr().hand.size();
                int count = 0;
                for (AbstractCard card : cards) {
                    AbstractCard copy = card.makeStatEquivalentCopy();
                    if (dowhat != null)
                        dowhat.accept(copy);
                    if (count <= remains) {
                        effectToList(new ShowCardAndAddToHandEffect(copy, Settings.WIDTH / 2F, Settings.HEIGHT / 2F));
                        count++;
                        continue;
                    }
                    effectToList(new ShowCardAndAddToDiscardEffect(copy, Settings.WIDTH / 2F, Settings.HEIGHT / 2F));
                }
                isDone = true;
                return;
            } else {
                AbstractDungeon.cardRewardScreen.customCombatOpen(cards, CardRewardScreen.TEXT[1], false);
            }
            tickDuration();
        }
        if (AbstractDungeon.cardRewardScreen.discoveryCard != null) {
            AbstractCard chosen = AbstractDungeon.cardRewardScreen.discoveryCard.makeStatEquivalentCopy();
            if (cpr().hasPower(MasterRealityPower.POWER_ID)) {
                chosen.upgrade();
            }
            if (dowhat != null)
                dowhat.accept(chosen);
            if (cpr().hand.size() < BaseMod.MAX_HAND_SIZE) {
                effectToList(new ShowCardAndAddToHandEffect(chosen, Settings.WIDTH / 2F, Settings.HEIGHT / 2F));
            } else {
                effectToList(new ShowCardAndAddToDiscardEffect(chosen, Settings.WIDTH / 2F, Settings.HEIGHT / 2F));
            }
            AbstractDungeon.cardRewardScreen.discoveryCard = null;
            isDone = true;
            executeWhenJobsDone();
        }
    }
    
    @NotNull
    @Deprecated
    public static List<AbstractCard> Genereate(int amount, Predicate<AbstractCard> expt) {
        return Generate(amount, expt);
    }
    
    @NotNull
    public static List<AbstractCard> Generate(int amount, Predicate<AbstractCard> expt) {
        List<AbstractCard> tmp = new ArrayList<>();
        while (tmp.size() < amount) {
            Optional<AbstractCard> opt = LMSK.ReturnTrulyRndCardInCombat(expt);
            opt.ifPresent(card -> {
                if (tmp.stream().noneMatch(c -> c.cardID.equals(card.cardID)))
                    tmp.add(card);
            });
        }
        return tmp;
    }
    
    @NotNull
    public static List<AbstractCard> Generate(int amount, List<? extends AbstractCard> range, Predicate<AbstractCard> predicate) {
        List<AbstractCard> tmp = new ArrayList<>();
        while (tmp.size() < amount) {
            Optional<? extends AbstractCard> opt = LMSK.GetRandom(range, LMSK.CardRandomRng());
            opt.ifPresent(card -> {
                if (tmp.stream().noneMatch(c -> c.cardID.equals(card.cardID)))
                    tmp.add(card.makeStatEquivalentCopy());
            });
        }
        return tmp;
    }
}