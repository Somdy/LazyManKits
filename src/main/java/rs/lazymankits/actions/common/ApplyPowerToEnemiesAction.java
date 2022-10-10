package rs.lazymankits.actions.common;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.enums.ApplyPowerParam;

import java.util.Arrays;
import java.util.Collection;

public class ApplyPowerToEnemiesAction extends LMCustomGameAction {
    private final Class<? extends AbstractPower> pwrCls;
    private Object[] params;
    private Class<?>[] paramTypes;
    private Collection<? extends AbstractMonster> targets;
    
    public ApplyPowerToEnemiesAction(AbstractCreature source, Class<? extends AbstractPower> pwrCls, Object... params) {
        this.source = source;
        this.pwrCls = pwrCls;
        initParams(params);
        targets = getAllLivingMstrs();
        actionType = ActionType.POWER;
        duration = startDuration = Settings.ACTION_DUR_FAST;
    }
    
    private void initParams(@NotNull Object... params) {
        this.params = new Object[params.length];
        paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof ApplyPowerParam) {
                Object param = params[i];
                if (param == ApplyPowerParam.THE_SOURCE) {
                    this.params[i] = source;
                    paramTypes[i] = source.getClass();
                } else if (param == ApplyPowerParam.ANY_SOURCE) {
                    this.params[i] = source;
                    paramTypes[i] = ApplyPowerParam.ANY_SOURCE.getParamType();
                } else if (param == ApplyPowerParam.ANY_OWNER) {
                    this.params[i] = params[i];
                    paramTypes[i] = ApplyPowerParam.ANY_OWNER.getParamType();
                } else {
                    this.params[i] = params[i];
                    paramTypes[i] = ApplyPowerParam.MONSTER_OWNER.getParamType();
                }
            } else {
                this.params[i] = params[i];
                paramTypes[i] = getPrimitiveType(params[i]);
            }
        }
    }
    
    private Class<?> getPrimitiveType(@NotNull Object obj) {
        switch (obj.getClass().getSimpleName()) {
            case "Integer":
                return int.class;
            case "Long":
                return long.class;
            case "Float":
                return float.class;
            case "Double":
                return double.class;
            case "Boolean":
                return boolean.class;
            default:
                return obj.getClass();
        }
    }
    
    public ApplyPowerToEnemiesAction setTargets(Collection<? extends AbstractMonster> targets) {
        this.targets = targets;
        return this;
    }
    
    @Override
    public void update() {
        if (pwrCls == null) {
            isDone = true;
            return;
        }
        if (!targets.isEmpty()) {
            for (AbstractMonster m : targets) {
                Object[] powerParams = createParams(m);
                try {
                    AbstractPower power = constructPower(powerParams);
                    addToTop(new ApplyPowerAction(m, source, power));
                } catch (Exception e) {
                    LMDebug.Log("Failed to apply power to [" + m.name + "]: " + e.getMessage());
                }
            }
        }
        isDone = true;
    }
    
    @NotNull
    private Object[] createParams(AbstractMonster target) {
        Object[] powerParams = Arrays.copyOf(params, params.length);
        for (int i = 0; i < params.length; i++) {
            if (params[i] == ApplyPowerParam.ANY_OWNER || params[i] == ApplyPowerParam.MONSTER_OWNER) {
                powerParams[i] = target;
                break;
            }
        }
        return powerParams;
    }
    
    @NotNull
    private AbstractPower constructPower(Object... params) throws Exception {
        return pwrCls.getConstructor(paramTypes).newInstance(params);
    }
}