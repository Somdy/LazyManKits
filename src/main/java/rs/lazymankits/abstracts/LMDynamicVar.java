package rs.lazymankits.abstracts;

import com.badlogic.gdx.graphics.Color;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.interfaces.LMDynVarImpler;

public abstract class LMDynamicVar {
    
    public abstract String key();
    public abstract VarType type();
    public abstract int baseVaule(LMDynVarImpler impler);
    public abstract int dynamicValue(LMDynVarImpler impler);
    
    public boolean isModified(@NotNull LMDynVarImpler impler) {
        return impler.isVarModified(this);
    }
    
    public Color normalColor(@NotNull LMDynVarImpler impler) {
        return impler.normalVarColor();
    }
    
    public Color increasedColor(@NotNull LMDynVarImpler impler) {
        return impler.increasedVarColor();
    }
    
    public Color decreasedColor(@NotNull LMDynVarImpler impler) {
        return impler.decreasedVarColor();
    }
    
    public enum VarType {
        Relic
    }
}