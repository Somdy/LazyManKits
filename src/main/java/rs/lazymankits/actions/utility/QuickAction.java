package rs.lazymankits.actions.utility;

import rs.lazymankits.abstracts.LMCustomGameAction;

public class QuickAction extends LMCustomGameAction {
    private Runnable action;

    public QuickAction(Runnable action, ActionType type, float dur) {
        this.action = action;
        if (type != null) this.actionType = type;
        duration = startDuration = dur;
    }

    public QuickAction(ActionType type) {
        this(null, type, 0F);
    }
    
    public QuickAction(Runnable action) {
        this(action, null, 0F);
    }
    
    public QuickAction(float dur) {
        this(null, null, dur);
    }

    public QuickAction() {
        this(null, null, 0F);
    }

    public QuickAction setAction(Runnable action) {
        this.action = action;
        return this;
    }

    public QuickAction setDuration(float dur) {
        duration = startDuration = dur;
        return this;
    }

    @Override
    public void update() {
        isDone = true;
        if (action == null) {
            return;
        }
        action.run();
    }
}