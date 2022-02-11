package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;
import rs.lazymankits.interfaces.utilities.DrawCardAmountModifier;
import rs.lazymankits.listeners.DrawCardListener;
import rs.lazymankits.utils.LMSK;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PlayerDrawCardHook {
    public static boolean EndTurnDraw = false;
    
    @SpirePatch(clz = DrawCardAction.class, method = "update")
    public static class DrawCardHook {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(DrawCardAction _inst) {
            DrawCardListener.OnCardDrawn(LMSK.Player().drawPile.getTopCard());
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "draw");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
    
    @SpirePatch(clz = DrawCardAction.class, method = "update")
    public static class ModifyDrawCardAmountPatch {
        @SpirePrefixPatch
        public static SpireReturn Prefix(AbstractGameAction _inst) throws Exception {
            float tmpDur;
            if (Settings.FAST_MODE) {
                tmpDur = Settings.ACTION_DUR_XFAST;
            } else {
                tmpDur = Settings.ACTION_DUR_FASTER;
            }
            Field realDur = AbstractGameAction.class.getDeclaredField("duration");
            realDur.setAccessible(true);
            float duration = (Float) realDur.get(_inst);
            if (duration != tmpDur)
                return SpireReturn.Continue();
            List<DrawCardAmountModifier> modifiers = new ArrayList<>();
            for (AbstractPower p : LMSK.Player().powers) {
                if (p instanceof DrawCardAmountModifier && !modifiers.contains(p))
                    modifiers.add((DrawCardAmountModifier) p);
            }
            for (AbstractRelic r : LMSK.Player().relics) {
                if (r instanceof DrawCardAmountModifier && !modifiers.contains(r))
                    modifiers.add((DrawCardAmountModifier) r);
            }
            List<AbstractCard> cards = LMSK.GetALLUnexhaustedCards();
            for (AbstractCard card : cards) {
                if (card instanceof DrawCardAmountModifier && !modifiers.contains(card))
                    modifiers.add((DrawCardAmountModifier) card);
            }
            Field clearHsty = _inst.getClass().getDeclaredField("clearDrawHistory");
            clearHsty.setAccessible(true);
            boolean clear = (Boolean) clearHsty.get(_inst);
            if (clear && !modifiers.isEmpty()) {
                int delta = _inst.amount;
                for (DrawCardAmountModifier mod : modifiers)
                    delta = mod.modifyDrawAmount(_inst.source, delta, EndTurnDraw);
                _inst.amount = delta;
                Method tickDuration = AbstractGameAction.class.getDeclaredMethod("tickDuration");
                tickDuration.setAccessible(true);
                tickDuration.invoke(_inst);
                return SpireReturn.Return();
            }
            return SpireReturn.Continue();
        }
    }
    
    @SpirePatch(clz = DrawCardAction.class, method = SpirePatch.CONSTRUCTOR, 
            paramtypez = {AbstractCreature.class, int.class, boolean.class})
    public static class DrawCardConstructorHook {
        public static void Prefix(DrawCardAction _inst, AbstractCreature source, int amount, boolean endTurnDraw) {
            EndTurnDraw = endTurnDraw;
        }
    }
}