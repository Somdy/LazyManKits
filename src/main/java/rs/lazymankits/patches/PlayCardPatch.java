package rs.lazymankits.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CtBehavior;
import rs.lazymankits.LMDebug;
import rs.lazymankits.interfaces.LMSubscriber;
import rs.lazymankits.listeners.UseCardListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PlayCardPatch {
    @SpirePatch(clz = GameActionManager.class, method = "getNextAction")
    public static class CopyCardsPlayedInSingleTurn {
        @SpireInsertPatch(locator = CopyCardsPlayedInSingleTurn.Locator.class)
        public static void Insert(GameActionManager _inst) {
            boolean success = UseCardListener.CopyCardsPlayedLastTurn(_inst.cardsPlayedThisTurn);
            if (!success)
                LMDebug.Log("Failed to copy cards play last turn");
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "clear");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }

    @SpirePatch(clz = UseCardAction.class, method = "update")
    public static class UseCardActionPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(UseCardAction _inst) throws Exception {
            Field duration = AbstractGameAction.class.getDeclaredField("duration");
            duration.setAccessible(true);
            float dur = duration.getFloat(_inst);
            if (dur == 0.15F) {
                Field targetCard = _inst.getClass().getDeclaredField("targetCard");
                targetCard.setAccessible(true);
                AbstractCard card = (AbstractCard) targetCard.get(_inst);
                LMSubscriber.PublishCardPlayed(card, _inst);
                UseCardListener.OnCardPlayed(card, _inst);
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "isInAutoplay");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class BeforeCardUsedPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractPlayer _inst, AbstractCard c, AbstractMonster m, int energyOnUse) {
            LMSubscriber.PublishPlayingCard(c, m, energyOnUse);
            UseCardListener.OnPlayingCard(c, m, energyOnUse);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "use");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
}