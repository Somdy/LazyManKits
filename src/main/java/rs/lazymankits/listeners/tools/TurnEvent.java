package rs.lazymankits.listeners.tools;

import rs.lazymankits.LMDebug;

import java.util.UUID;
import java.util.function.Predicate;

public class TurnEvent {
    private Runnable action;
    private Runnable onRemove;
    private Predicate<TurnEvent> castable;
    private Predicate<TurnEvent> remove;
    public final UUID uuid;
    public int delay;
    public int primaryDelay;
    public int times; // how many times this event can execute before it's removed
    public int turns; // how many turns this event will last no matter it executes at least once or not

    public TurnEvent(Runnable action, int turns, Predicate<TurnEvent> castable, Predicate<TurnEvent> whenToRemove) {
        this.action = action;
        this.onRemove = null;
        this.turns = turns;
        this.primaryDelay = delay = 0;
        times = 1;
        this.castable = castable;
        this.remove = whenToRemove;
        this.uuid = UUID.randomUUID();
    }

    public TurnEvent(Runnable action, int turns) {
        this(action, turns, event -> event.delay <= 0, e -> e.turns <= 0);
    }

    public TurnEvent(Runnable action) {
        this(action, 0, event -> event.delay <= 0, e -> e.turns <= 0);
    }
    
    public TurnEvent() {
        this(null, 0, event -> event.delay <= 0, e -> e.turns <= 0);
    }
    
    public TurnEvent setTimes(int times) {
        this.times = times;
        return this;
    }

    public TurnEvent setAction(Runnable action) {
        this.action = action;
        return this;
    }

    public TurnEvent setOnRemoveAction(Runnable action) {
        this.onRemove = action;
        return this;
    }

    public TurnEvent setDelay(int delay, boolean sameAsPrimary) {
        this.delay = delay;
        if (sameAsPrimary)
            this.setPrimaryDelay(delay);
        if (this.delay > turns) {
            LMDebug.Log("Event [" + uuid.toString() +  "] has more delay [" + this.delay + "] than its turns [" + turns + "]");
        }
        return this;
    }

    public TurnEvent setRemoveConditions(Predicate<TurnEvent> whenToRemove) {
        this.remove = whenToRemove;
        return this;
    }

    public void setPrimaryDelay(int delay) {
        this.primaryDelay = delay;
    }

    public void resetDelay() {
        this.delay = this.primaryDelay;
    }

    public void decrsDelay() {
        delay--;
    }
    
    public void decrsTurns() {
        turns--;
    }

    public Runnable getAction() {
        return action;
    }

    public UUID uuid() {
        return uuid;
    }

    public final void execute() {
        LMDebug.deLog(this, "executing turn event: " + uuid);
        action.run();
        times--;
    }

    public boolean canCast() {
        return castable.test(this);
    }

    public boolean shouldRemove() {
        return remove.test(this);
    }

    public void tackleOnRemove() {
        if (onRemove != null)
            onRemove.run();
    }
}