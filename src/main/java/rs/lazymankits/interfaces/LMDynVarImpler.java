package rs.lazymankits.interfaces;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import rs.lazymankits.abstracts.LMDynamicVar;

public interface LMDynVarImpler {
    boolean isVarModified(LMDynamicVar var);
    
    default Color normalVarColor() {
        return Settings.CREAM_COLOR;
    }
    
    default Color increasedVarColor() {
        return Settings.GREEN_TEXT_COLOR;
    }
    
    default Color decreasedVarColor() {
        return Settings.RED_TEXT_COLOR;
    }
}