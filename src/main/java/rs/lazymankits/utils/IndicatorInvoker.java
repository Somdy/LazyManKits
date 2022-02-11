package rs.lazymankits.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IndicatorInvoker {
    default void active(Vector2 start, Consumer<AbstractCreature> dowhat) {
        IndicatorMode mode = new IndicatorMode(this);
        mode.active(start, dowhat);
        registerMode(mode);
    }

    default void active(Vector2 start, Consumer<AbstractCreature> dowhat, Predicate<AbstractCreature> predicator) {
        IndicatorMode mode = new IndicatorMode(this);
        mode.active(start, dowhat, predicator);
        registerMode(mode);
    }

    default void active(Vector2 start, Consumer<AbstractCreature> dowhat, Predicate<AbstractCreature> predicator, Color pointerColor) {
        IndicatorMode mode = new IndicatorMode(this);
        mode.active(start, dowhat, predicator, pointerColor);
        registerMode(mode);
    }

    default void registerMode(IndicatorMode mode) {
        IndicatorMode.register(mode);
    }
}