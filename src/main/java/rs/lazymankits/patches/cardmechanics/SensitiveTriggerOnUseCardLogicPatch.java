package rs.lazymankits.patches.cardmechanics;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;
import rs.lazymankits.interfaces.cards.SensitiveTriggerOnUseCard;

@SuppressWarnings("unused")
public class SensitiveTriggerOnUseCardLogicPatch {
    @SpirePatch(clz = UseCardAction.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, AbstractCreature.class})
    public static class UseCardConstructorPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor(){
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (isMethodCalledOnUseCard(m)) {
                        m.replace("{if(" + SensitiveTriggerOnUseCardLogicPatch.class.getName()
                                + ".AllowSensitiveGear($1,$0,\"" + m.getMethodName() + "\")){$_=$proceed($$);}}");
                    }
                }
                private boolean isMethodCalledOnUseCard(MethodCall m) {
                    String methodName = m.getMethodName();
                    return methodName.equals("onUseCard") || methodName.equals("triggerOnCardPlayed");
                }
            };
        }
    }
    @SpirePatch(clz = UseCardAction.class, method = "update")
    public static class UseCardUpdatePatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor(){
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (isMethodCalledAfterUseCard(m)) {
                        m.replace("{if(" + SensitiveTriggerOnUseCardLogicPatch.class.getName()
                                + ".AllowSensitiveGear($1,$0,\"" + m.getMethodName() + "\")){$_=$proceed($$);}}");
                    }
                }
                private boolean isMethodCalledAfterUseCard(MethodCall m) {
                    String methodName = m.getMethodName();
                    return methodName.equals("onAfterUseCard");
                }
            };
        }
    }
    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class GameActionManagerPatch {
        @SpireRawPatch
        public static void Raw(CtBehavior ctBehavior) throws Exception {
            ClassPool pool = ctBehavior.getDeclaringClass().getClassPool();
            CtClass gam = pool.get(GameActionManager.class.getName());
            CtMethod method = gam.getDeclaredMethod("getNextAction");
            method.instrument(new ExprEditor(){
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (isMethodCalledOnPlayCard(m)) {
                        m.replace("{if(" + SensitiveTriggerOnUseCardLogicPatch.class.getName()
                                + ".AllowSensitiveGear($1,$0,\"" + m.getMethodName() + "\")){$_=$proceed($$);}}");
                    }
                }
                
                @Override
                public void edit(FieldAccess f) throws CannotCompileException {
                    if (f.getFieldName().equals("cardsPlayedThisTurn") && f.getLineNumber() < 320
                            && f.getClassName().equals(AbstractPlayer.class.getName())) {
                        f.replace("{if(" + SensitiveTriggerOnUseCardLogicPatch.class.getName()
                                + ".AllowSensitiveTurnRecord(this.cardQueue.get(0))){$_=$proceed($$);}}");
                    }
                }
            });
            method.instrument(new ExprEditor(){
                private final int[] lines = new int[2];
                @Override
                public void edit(FieldAccess f) {
                    if (f.getFieldName().equals("cardsPlayedThisTurn")
                            && f.getClassName().equals(GameActionManager.class.getName())) {
                        lines[0] = f.getLineNumber();
                    }
                    if (f.getFieldName().equals("cardsPlayedThisCombat")
                            && f.getClassName().equals(GameActionManager.class.getName())) {
                        lines[1] = f.getLineNumber();
                    }
                }
                
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    int line = m.getLineNumber();
                    if (m.getMethodName().equals("add") && (line == lines[0] || line == lines[1])) {
                        if (line == lines[0]) {
                            m.replace("{if(" + SensitiveTriggerOnUseCardLogicPatch.class.getName()
                                    + ".AllowSensitiveTurnRecord($1))" + "{$_=$proceed($$);}else{" +
                                    SensitiveTriggerOnUseCardLogicPatch.class.getName() + ".DoSelfRecord($1,1);}}");
                        }
                        if (line == lines[1]) {
                            m.replace("{if(" + SensitiveTriggerOnUseCardLogicPatch.class.getName()
                                    + ".AllowSensitiveCombatRecord($1)){$_=$proceed($$);}else{" +
                                    SensitiveTriggerOnUseCardLogicPatch.class.getName() + ".DoSelfRecord($1,0);}}");
                        }
                    }
                }
            });
        }
        private static String getLocalVarName(int index) {
            return index > 0 ? "_param_" + index : "lmk_thisIsMyAbstractCard";
        }
        private static boolean isMethodCalledOnPlayCard(MethodCall m) {
            String methodName = m.getMethodName();
            return methodName.equals("onPlayCard");
        }
    }
    @SpirePatch(clz = CardGroup.class, method = "triggerOnOtherCardPlayed")
    public static class CardGroupOnCardPlayedPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor(){
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (isMethodCalledOnCardPlayed(m)) {
                        m.replace("{if(" + SensitiveTriggerOnUseCardLogicPatch.class.getName()
                                + ".AllowSensitiveGear($1,$0,\"" + m.getMethodName() + "\")){$_=$proceed($$);}}");
                    }
                }
                private boolean isMethodCalledOnCardPlayed(MethodCall m) {
                    String methodName = m.getMethodName();
                    return methodName.equals("triggerOnOtherCardPlayed") || methodName.equals("onAfterCardPlayed");
                }
            };
        }
    }
    
    public static boolean AllowSensitiveGear(AbstractCard card, Object o, String methodName) {
        if (card instanceof SensitiveTriggerOnUseCard) {
            return !((SensitiveTriggerOnUseCard) card).isSensitive() || ((SensitiveTriggerOnUseCard) card).canTriggerOnGear(o, methodName);
        }
        return !card.dontTriggerOnUseCard;
    }
    
    public static boolean AllowSensitiveCombatRecord(Object card) {
        if (card instanceof SensitiveTriggerOnUseCard) {
            return !((SensitiveTriggerOnUseCard) card).isSensitive() || ((SensitiveTriggerOnUseCard) card).countInCombatHistory();
        }
        return true;
    }
    
    public static boolean AllowSensitiveTurnRecord(Object card) {
        if (card instanceof SensitiveTriggerOnUseCard) {
            return !((SensitiveTriggerOnUseCard) card).isSensitive() || ((SensitiveTriggerOnUseCard) card).countInTurnHistory();
        }
        return true;
    }
    
    public static void DoSelfRecord(Object card, int which) {
        if (card instanceof SensitiveTriggerOnUseCard) {
            if (which == 0)
                ((SensitiveTriggerOnUseCard) card).doSelfCombatRecord();
            else 
                ((SensitiveTriggerOnUseCard) card).doSelfTurnRecord();
        }
    }
}