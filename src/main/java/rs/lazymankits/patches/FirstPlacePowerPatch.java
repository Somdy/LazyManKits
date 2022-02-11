package rs.lazymankits.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import rs.lazymankits.interfaces.powers.FirstPlacePower;

import java.util.ArrayList;

public class FirstPlacePowerPatch {
    @SpirePatch(clz = AbstractCreature.class, method = "addPower")
    public static class PlaceItAtFirstSlot {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("add") && m.getClassName().equals(ArrayList.class.getName())) {
                        m.replace("{if (powerToApply instanceof " + FirstPlacePower.class.getName() +
                                " && ((" + FirstPlacePower.class.getName() + ") powerToApply).placeFirstOfAll()) " +
                                "{this.powers.add(0, $$);} else {$_ = $proceed($$);} }");
                    }
                }
            };
        }
    }
}