package rs.lazymankits.listeners.tools;

@FunctionalInterface
public interface MainDrawCardActionMplr {
    int manipulate(int amount, boolean endTurnDraw);
}