package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardAtBottomOfDeckAction;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import javassist.CtBehavior;
import rs.lazymankits.interfaces.LMSubscriber;
import rs.lazymankits.utils.LMSK;

public class OnMakingCardHook {
    @SpirePatch2(
            clz = ShowCardAndAddToDiscardEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class}
    )
    public static class ShowCardAndAddToDiscardEffectPatch1 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractCard ___card) {
            LMSubscriber.PublishOnMakingCard(___card, LMSK.Player().discardPile);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(
            clz = ShowCardAndAddToDiscardEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class}
    )
    public static class ShowCardAndAddToDiscardEffectPatch2 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractCard ___card) {
            LMSubscriber.PublishOnMakingCard(___card, LMSK.Player().discardPile);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(
            clz = ShowCardAndAddToDrawPileEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class}
    )
    public static class ShowCardAndAddToDrawPileEffectPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractCard ___card) {
            LMSubscriber.PublishOnMakingCard(___card, LMSK.Player().drawPile);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(
            clz = ShowCardAndAddToHandEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class}
    )
    public static class ShowCardAndAddToHandEffectPatch1 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractCard ___card) {
            LMSubscriber.PublishOnMakingCard(___card, LMSK.Player().hand);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(
            clz = ShowCardAndAddToHandEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class}
    )
    public static class ShowCardAndAddToHandEffectPatch2 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractCard ___card) {
            LMSubscriber.PublishOnMakingCard(___card, LMSK.Player().hand);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = MakeTempCardAtBottomOfDeckAction.class, method = "update")
    public static class MakeTempCardAtBottomOfDeckActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractGameAction _inst, AbstractCard card) {
            LMSubscriber.PublishOnMakingCard(card, LMSK.Player().drawPile);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "type");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
}