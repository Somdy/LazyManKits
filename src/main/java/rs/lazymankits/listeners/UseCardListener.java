package rs.lazymankits.listeners;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kotlin.jvm.functions.Function3;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.LManager;
import rs.lazymankits.interfaces.TripleUniMap;
import rs.lazymankits.listeners.tools.CardPlayedEvent;
import rs.lazymankits.listeners.tools.PlayCardEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UseCardListener {
    private static List<CustomUnplayableLogic> unplayableList = new ArrayList<>();
    private static List<CustomPlayCardEvent> playCardEventsList = new ArrayList<>();
    private static List<CustomCardPlayedEvent> cardPlayedEventsList = new ArrayList<>();
    public static List<AbstractCard> CardsPlayedLastTurn = new ArrayList<>();

    public static void Initialize() {
        if (LManager.EverythingReady()) return;
        unplayableList = new ArrayList<>();
        playCardEventsList = new ArrayList<>();
        cardPlayedEventsList = new ArrayList<>();
        CardsPlayedLastTurn = new ArrayList<>();
    }

    public static boolean CopyCardsPlayedLastTurn(@NotNull ArrayList<AbstractCard> cards) {
        if (cards.isEmpty()) return false;
        CardsPlayedLastTurn.clear();
        for (AbstractCard card : cards) {
            if (!CardsPlayedLastTurn.contains(card))
                CardsPlayedLastTurn.add(card);
        }
        return true;
    }

    public static void AddNewPlayCardEvent(String ID, boolean forcedReplace, int turnsLeft, PlayCardEvent event) {
        putPlayCardEventWithAbsenceCheck(ID, forcedReplace, turnsLeft, event);
    }

    private static void putPlayCardEventWithAbsenceCheck(String ID, boolean forcedReplace, int turnsLeft, PlayCardEvent event) {
        CustomPlayCardEvent playevent = null;
        for (CustomPlayCardEvent pe : playCardEventsList) {
            if (pe.identify(ID)) {
                playevent = pe;
                break;
            }
        }
        if (playevent == null) {
            playCardEventsList.add(new CustomPlayCardEvent(ID, event, turnsLeft));
        } else if (forcedReplace) {
            boolean success = Collections.replaceAll(playCardEventsList, playevent, new CustomPlayCardEvent(ID, event, turnsLeft));
            if (!success)
                Log("Failed to replace play card event: " + ID);
        }
    }

    public static void OnPlayingCard(AbstractCard card, AbstractCreature target, int energyOnUse) {
        if (!playCardEventsList.isEmpty()) {
            playCardEventsList.stream()
                    .filter(e -> e.turnsLeft > 0)
                    .forEach(e -> e.event.onPlayCard(card, target, energyOnUse));
        }
    }

    public static void AddNewCardPlayedEvent(String ID, boolean forcedReplace, int turnsLeft, CardPlayedEvent event) {
        CustomCardPlayedEvent playedevent = null;
        for (CustomCardPlayedEvent pe : cardPlayedEventsList) {
            if (pe.identify(ID)) {
                playedevent = pe;
                break;
            }
        }
        if (playedevent == null) {
            cardPlayedEventsList.add(new CustomCardPlayedEvent(ID, event, turnsLeft));
        } else if (forcedReplace) {
            boolean success = Collections.replaceAll(cardPlayedEventsList, playedevent, new CustomCardPlayedEvent(ID, event, turnsLeft));
            if (!success)
                Log("Failed to replace card played event: " + ID);
        }
    }

    public static void OnCardPlayed(AbstractCard card, UseCardAction action) {
        if (!cardPlayedEventsList.isEmpty()) {
            cardPlayedEventsList.stream()
                    .filter(e -> e.turnsLeft > 0)
                    .forEach(e -> e.event.onCardPlayed(card, action));
        }
    }

    public static boolean RemovePlayCardEvent(String ID) {
        return playCardEventsList.removeIf(e -> e.ID.equals(ID));
    }

    public static boolean RemoveCardPlayedEvent(String ID) {
        return cardPlayedEventsList.removeIf(e -> e.ID.equals(ID));
    }

    public static void AddCustomUnplayableCard(AbstractCard card, int turns, Function3<AbstractCard,
            AbstractPlayer, AbstractMonster, Boolean> predicator, boolean forcedReplace) {
        putLogicWithAbsenceCheck(card, turns, predicator, forcedReplace);
    }

    private static void putLogicWithAbsenceCheck(AbstractCard card, int turns, Function3<AbstractCard,
            AbstractPlayer, AbstractMonster, Boolean> predicator, boolean forcedReplace) {
        CustomUnplayableLogic logic = null;
        for (CustomUnplayableLogic lg : unplayableList) {
            if (lg.identify(card)) {
                logic = lg;
                break;
            }
        }
        if (logic == null) {
            unplayableList.add(new CustomUnplayableLogic(card, turns, predicator));
        } else if (forcedReplace) {
            boolean success = Collections.replaceAll(unplayableList, logic, new CustomUnplayableLogic(card, turns, predicator));
            if (!success)
                Log("Failed to replace unplayable logic of card: " + card.name);
        }
    }

    public static boolean CanCardPlay(AbstractCard card, AbstractPlayer p, AbstractMonster m) {
        if (unplayableList.stream().noneMatch(e -> e.identify(card))) {
            Log(card.name + " has no custom unplayable logic");
            return true;
        }
        CustomUnplayableLogic logic = null;
        for (CustomUnplayableLogic lg : unplayableList) {
            if (lg.identify(card)) {
                logic = lg;
                break;
            }
        }
        if (logic == null) {
            Log("Unable to find " + card.name + "'s custom unplayable logic");
            return true;
        }
        int turnsLeft = logic.accept(card);
        if (turnsLeft > 0) {
            boolean canPlay = logic.find(card).invoke(card, p, m);
            Log("Detected " + card.name + "'s custom unplayable logic: " + canPlay);
            return canPlay;
        } else {
            Log(card.name + " should've been removed from unplayable list since " + (-turnsLeft) + " ago.");
            unplayableList.removeIf(e -> e.identify(card));
            return true;
        }
    }

    public static void UpdatePostTurnStart() {
        if (!unplayableList.isEmpty()) {
            unplayableList.forEach(CustomUnplayableLogic::reduceTurns);
            unplayableList.removeIf(e -> e.turns <= 0);
        }
        if (!playCardEventsList.isEmpty()) {
            playCardEventsList.forEach(CustomPlayCardEvent::reduceTurns);
            playCardEventsList.removeIf(e -> e.turnsLeft <= 0);
        }
        if (!cardPlayedEventsList.isEmpty()) {
            cardPlayedEventsList.forEach(CustomCardPlayedEvent::reduceTurns);
            cardPlayedEventsList.removeIf(e -> e.turnsLeft <= 0);
        }
    }
    
    public static void ClearPostBattle() {
        unplayableList.clear();
        playCardEventsList.clear();
        cardPlayedEventsList.clear();
    }

    public static boolean ContainsCardPlayedEvent(String ID) {
        return cardPlayedEventsList.stream().anyMatch(e -> e.identify(ID));
    }

    public static boolean ContainsPlayCardEvent(String ID) {
        return playCardEventsList.stream().anyMatch(e -> e.identify(ID));
    }

    public static boolean ContainsUnplayableCard(AbstractCard card) {
        return unplayableList.stream().anyMatch(e -> e.identify(card));
    }

    private static void Log(Object what) {
        LMDebug.Log(UseCardListener.class, what);
    }

    private static class CustomUnplayableLogic implements TripleUniMap<AbstractCard, Integer,
            Function3<AbstractCard, AbstractPlayer, AbstractMonster, Boolean>> {
        AbstractCard card;
        int turns;
        Function3<AbstractCard, AbstractPlayer, AbstractMonster, Boolean> predicator;

        public CustomUnplayableLogic(AbstractCard card, int turns, Function3<AbstractCard, AbstractPlayer, AbstractMonster, Boolean> predicator) {
            this.card = card;
            this.turns = turns;
            this.predicator = predicator;
        }

        public void reduceTurns() {
            turns--;
        }

        @Override
        public Integer accept(AbstractCard card) {
            if (identify(card))
                return turns;
            return 0;
        }

        @Override
        public Function3<AbstractCard, AbstractPlayer, AbstractMonster, Boolean> find(AbstractCard card) {
            if (identify(card))
                return predicator;
            return null;
        }

        @Override
        public boolean identify(AbstractCard card) {
            return this.card == card;
        }
    }

    private static class CustomPlayCardEvent implements TripleUniMap<String, PlayCardEvent, Integer> {
        String ID;
        PlayCardEvent event;
        int turnsLeft;

        public CustomPlayCardEvent(String ID, PlayCardEvent event, Integer turnsLeft) {
            this.ID = ID;
            this.event = event;
            this.turnsLeft = turnsLeft;
        }

        public void reduceTurns() {
            turnsLeft--;
        }

        @Override
        public PlayCardEvent accept(String s) {
            return identify(s) ? event : null;
        }

        @Override
        public Integer find(String s) {
            return identify(s) ? turnsLeft : -1;
        }

        @Override
        public boolean identify(String s) {
            return ID.equals(s);
        }
    }

    private static class CustomCardPlayedEvent implements TripleUniMap<String, CardPlayedEvent, Integer> {
        String ID;
        CardPlayedEvent event;
        int turnsLeft;

        public CustomCardPlayedEvent(String ID, CardPlayedEvent event, Integer turnsLeft) {
            this.ID = ID;
            this.event = event;
            this.turnsLeft = turnsLeft;
        }

        public void reduceTurns() {
            turnsLeft--;
        }

        @Override
        public CardPlayedEvent accept(String s) {
            return identify(s) ? event : null;
        }

        @Override
        public Integer find(String s) {
            return identify(s) ? turnsLeft : -1;
        }

        @Override
        public boolean identify(String s) {
            return ID.equals(s);
        }
    }
}