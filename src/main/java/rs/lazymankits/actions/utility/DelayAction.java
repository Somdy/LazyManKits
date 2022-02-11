package rs.lazymankits.actions.utility;

import com.badlogic.gdx.Gdx;
import rs.lazymankits.abstracts.LMCustomGameAction;

public class DelayAction extends LMCustomGameAction {
    public Runnable action;

    public DelayAction(Runnable action, float dur) {
        this.action = action;
        this.duration = dur;
    }

    public DelayAction(Runnable action) {
        this(action, 0F);
    }

    @Override
    public void update() {
        duration -= Gdx.graphics.getDeltaTime();
        if (duration <= 0F) {
            isDone = true;
            action.run();
        }
    }
}