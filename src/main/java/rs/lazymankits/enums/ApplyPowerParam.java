package rs.lazymankits.enums;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public enum ApplyPowerParam {
    ANY_SOURCE(AbstractCreature.class), ANY_OWNER(AbstractCreature.class),
    THE_SOURCE(null), MONSTER_OWNER(AbstractMonster.class);
    
    private final Class<?> paramType;
    
    ApplyPowerParam(Class<?> paramType) {
        this.paramType = paramType;
    }
    
    public Class<?> getParamType() {
        return paramType;
    }
}