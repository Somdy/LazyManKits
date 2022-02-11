package rs.lazymankits.actions.common;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.abstracts.LMCustomPower;

import java.util.Optional;

public class StackPowerAmountAction extends LMCustomGameAction {
    private AbstractPower power;
    private int extraAmount;

    public StackPowerAmountAction(AbstractCreature target, AbstractPower power, int amount, int extraAmount) {
        this.target = target;
        this.power = power;
        this.amount = amount;
        this.extraAmount = extraAmount;
        actionType = ActionType.SPECIAL;
    }

    public StackPowerAmountAction(AbstractCreature target, AbstractPower power, int amount) {
        this(target, power, amount, 0);
    }

    @Override
    public void update() {
        isDone = true;
        if (target.isDeadOrEscaped() || power == null || amount <= 0)
            return;
        Optional<AbstractPower> targetPower = target.powers.stream().filter(p -> p == power).findFirst();
        targetPower.ifPresent(p -> {
            p.stackPower(amount);
            if (p instanceof LMCustomPower && extraAmount != 0)
                ((LMCustomPower) p).stackExtraAmount(extraAmount);
            p.updateDescription();
        });
        AbstractDungeon.onModifyPower();
    }
}