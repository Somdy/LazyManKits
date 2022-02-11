package rs.lazymankits.listeners;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.lazymankits.LManager;
import rs.lazymankits.listeners.tools.TurnEvent;
import rs.lazymankits.listeners.tools.TurnStatus;
import rs.lazymankits.utils.LMSK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TurnEventListener {
    private static Map<AbstractCreature, Integer> crtTurnCounter;
    private static Map<AbstractCreature, Predicate<AbstractCreature>> timeFrozen;
    private static Map<Integer, TurnStatus> turnStartStatus;
    private static Map<Integer, TurnStatus> turnEndingStatus;
    
    public static List<TurnEvent> PlayerEndTurnPreDiscard;
    
    public static final int[] LastTurn;
    public static final int[] CurrTurn;

    static {
        LastTurn = new int[] {0};
        CurrTurn = new int[] {0};
    }

    public static void Initialize() {
        if (LManager.EverythingReady()) return;
        crtTurnCounter = new HashMap<>();
        timeFrozen = new HashMap<>();
        turnStartStatus = new HashMap<>();
        turnEndingStatus = new HashMap<>();
        
        PlayerEndTurnPreDiscard = new ArrayList<>();
    }

    public static void LoadAtBattleStarts(@NotNull AbstractRoom r) {
        clearMaps();
        clearLists();
        for (AbstractMonster m : r.monsters.monsters) {
            if (!m.isDeadOrEscaped()) {
                crtTurnCounter.put(m, 0);
            }
        }
        if (LMSK.Player() != null && !LMSK.Player().isDeadOrEscaped()) {
            crtTurnCounter.put(LMSK.Player(), 0);
        }
    }
    
    private static void clearMaps() {
        crtTurnCounter.clear();
        timeFrozen.clear();
        turnStartStatus.clear();
        turnEndingStatus.clear();
    }
    
    private static void clearLists() {
        PlayerEndTurnPreDiscard.clear();
    }

    public static void TriggerEndOfTurnEvents(AbstractCreature creature) {
        checkIfShouldIncrsCreaturnTurnCounters(creature);
        LastTurn[0] = GameActionManager.turn;
        TurnStatus status = new TurnStatus(LMSK.Player());
        for (AbstractMonster m : LMSK.GetAllExptMstr(m -> true)) {
            status.append(m);
        }
        turnEndingStatus.put(LastTurn[0], status);
    }

    public static void TriggerStartOfTurnEvents(@NotNull AbstractCreature creature) {
        if (creature.isPlayer) {
            CurrTurn[0] = GameActionManager.turn;
            TurnStatus status = new TurnStatus(creature);
            for (AbstractMonster m : LMSK.GetAllExptMstr(m -> true)) {
                status.append(m);
            }
            turnStartStatus.put(CurrTurn[0], status);
        }
    }

    public static int GetPassedTurnsOf(AbstractCreature creature) {
        return crtTurnCounter.containsKey(creature) ? crtTurnCounter.get(creature) : LastTurn[0];
    }

    private static void checkIfShouldIncrsCreaturnTurnCounters(@NotNull AbstractCreature creature) {
        if (creature.isDeadOrEscaped() || IsTimeFrozen(creature)) return;
        if (!crtTurnCounter.containsKey(creature)) {
            crtTurnCounter.put(creature, 1);
            return;
        }
        crtTurnCounter.replace(creature, crtTurnCounter.get(creature) + 1);
    }
    
    @Nullable
    public static TurnStatus GetTurnStartStatus(int turn) {
        if (turnStartStatus != null && turnStartStatus.containsKey(turn)) {
            return turnStartStatus.get(turn);
        }
        return null;
    }
    
    @Nullable
    public static TurnStatus GetTurnEndingStatus(int turn) {
        if (turnEndingStatus != null && turnEndingStatus.containsKey(turn)) {
            return turnEndingStatus.get(turn);
        }
        return null;
    }

    public static boolean IsTimeFrozen(AbstractCreature creature) {
        return timeFrozen.containsKey(creature) && timeFrozen.get(creature).test(creature);
    }

    public static void SetCreatureTimeFrozen(boolean forcedReplaced, AbstractCreature creature, Predicate<AbstractCreature> frozen) {
        if (timeFrozen.containsKey(creature) && forcedReplaced)
            timeFrozen.put(creature, frozen);
        else
            timeFrozen.putIfAbsent(creature, frozen);
    }
    
    public static void AddNewEndTurnPreDiscardEvent(TurnEvent event) {
        PlayerEndTurnPreDiscard.add(event);
    }
    
    public static void TriggerEndTurnPreDiscard() {
        for (TurnEvent event : PlayerEndTurnPreDiscard) {
            if (event.canCast()) {
                event.execute();
            } else {
                event.decrsDelay();
            }
            event.decrsTurns();
        }
        PlayerEndTurnPreDiscard.removeIf(e -> {
            if (e.shouldRemove()) {
                e.tackleOnRemove();
                return true;
            }
            return false;
        });
    }
}