package rs.lazymankits.actions.utility;

import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleXCostActionBuilder extends LMCustomGameAction {
    private boolean freeToPlayOnce;
    private int energyOnUse;
    private boolean upgraded;
    private Queue<BiFunction<Integer, Integer, Integer>> extraEffects;
    private int[] effects;
    private Queue<BiFunction<Integer, Integer, Integer>> upgradeModifiers;
    private Map<Integer, Function<Integer, Integer>> customModifiers;
    private Queue<Consumer<Integer>> actions;
    private boolean energyUsed;
    private boolean built;

    public SimpleXCostActionBuilder(boolean freeToPlayOnce, int energyOnUse, boolean upgraded) {
        this.freeToPlayOnce = freeToPlayOnce;
        this.energyOnUse = energyOnUse;
        this.upgraded = upgraded;
        this.energyUsed = false;
        this.built = false;
        extraEffects = new LinkedList<>();
        upgradeModifiers = new LinkedList<>();
        customModifiers = new HashMap<>();
        actions = new LinkedList<>();
    }

    public SimpleXCostActionBuilder addEffect(BiFunction<Integer, Integer, Integer> effect) {
        extraEffects.offer(effect);
        return this;
    }

    public SimpleXCostActionBuilder addAction(Consumer<Integer> action) {
        actions.offer(action);
        return this;
    }

    public SimpleXCostActionBuilder addUpgradeModifier(BiFunction<Integer, Integer, Integer> modifier) {
        upgradeModifiers.offer(modifier);
        return this;
    }

    public SimpleXCostActionBuilder addCustomModifers(int keyNum, Function<Integer, Integer> modifier) {
        customModifiers.put(keyNum, modifier);
        return this;
    }

    public SimpleXCostActionBuilder build() {
        effects = new int[extraEffects.size()];
        built = true;
        return this;
    }

    @Override
    public void update() {
        if (!built) {
            LMDebug.Log(this, "Not finally built yet, not allowed any effects");
            isDone = true;
            return;
        }
        int originEffect = EnergyPanel.totalCount;
        if (energyOnUse != -1)
            originEffect = energyOnUse;
        if (!extraEffects.isEmpty()) {
            for (int i = 0; i < extraEffects.size(); i++) {
                if (extraEffects.peek() != null)
                    effects[i] = extraEffects.poll().apply(i, originEffect);
            }
        }
        if (cpr().hasRelic(ChemicalX.ID)) {
            for (int i = 0; i < effects.length; i++)
                effects[i] += 2;
            cpr().getRelic(ChemicalX.ID).flash();
        }
        if (upgraded && !upgradeModifiers.isEmpty()) {
            for (int i = 0; i < upgradeModifiers.size(); i++) {
                if (upgradeModifiers.peek() != null)
                    effects[i] = upgradeModifiers.poll().apply(i, effects[i]);
            }
        }
        if (!customModifiers.isEmpty()) {
            for (Map.Entry<Integer, Function<Integer, Integer>> entry : customModifiers.entrySet()) {
                if (entry.getKey() < effects.length)
                    effects[entry.getKey()] = entry.getValue().apply(effects[entry.getKey()]);
            }
        }
        for (int i = 0; i < effects.length; i++) {
            if (effects[i] > 0 && actions.peek() != null) {
                actions.poll().accept(effects[i]);
                if (!freeToPlayOnce && !energyUsed) {
                    cpr().energy.use(EnergyPanel.totalCount);
                    energyUsed = true;
                }
            }
        }
        executeWhenJobsDone();
        isDone = true;
    }
}