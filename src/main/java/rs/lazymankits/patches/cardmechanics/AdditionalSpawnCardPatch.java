package rs.lazymankits.patches.cardmechanics;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import javassist.CtBehavior;
import rs.lazymankits.LManager;
import rs.lazymankits.interfaces.cards.AdditionalSpawnCard;
import rs.lazymankits.utils.LMSK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdditionalSpawnCardPatch {
    public static final Map<AbstractPlayer.PlayerClass, List<AbstractCard>> AdditionalCards = new HashMap<>();
    
    @SpirePatch2(clz = AbstractDungeon.class, method = "returnTrulyRandomCardInCombat", paramtypez = {})
    public static class ReturnRandomCardInCombatPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"list"})
        public static void Insert(ArrayList<AbstractCard> list) {
            List<AbstractCard> cards = LManager.GetAdditionSpawnCards(LMSK.Player().chosenClass);
            List<AbstractCard> appends = new ArrayList<>();
            assert cards != null;
            cards.forEach(c -> {
                if (c instanceof AdditionalSpawnCard
                        && ((AdditionalSpawnCard) c).canSpawnInCombat(list, LMSK.Player().chosenClass))
                    appends.add(c.makeCopy());
            });
            list.addAll(appends);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }
    
    @SpirePatch2(clz = AbstractDungeon.class, method = "returnTrulyRandomCardInCombat",
            paramtypez = {AbstractCard.CardType.class})
    public static class ReturnRandomCardInCombatPatch2 {
        @SpireInsertPatch(locator = Locator.class, localvars = {"list"})
        public static void Insert(ArrayList<AbstractCard> list, AbstractCard.CardType type) {
            List<AbstractCard> cards = LManager.GetAdditionSpawnCards(LMSK.Player().chosenClass);
            List<AbstractCard> appends = new ArrayList<>();
            assert cards != null;
            cards.forEach(c -> {
                if (c instanceof AdditionalSpawnCard
                        && ((AdditionalSpawnCard) c).canSpawnInCombat(list, type, LMSK.Player().chosenClass))
                    appends.add(c.makeCopy());
            });
            list.addAll(appends);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }
    
    @SpirePatch2(clz = AbstractDungeon.class, method = "returnTrulyRandomColorlessCardInCombat",
            paramtypez = {Random.class})
    public static class ReturnRandomColorlessCardInCombatPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"list"})
        public static void Insert(ArrayList<AbstractCard> list, Random rng) {
            List<AbstractCard> cards = LManager.GetAdditionSpawnCards(LMSK.Player().chosenClass);
            List<AbstractCard> appends = new ArrayList<>();
            assert cards != null;
            cards.forEach(c -> {
                if (c instanceof AdditionalSpawnCard
                        && ((AdditionalSpawnCard) c).canSpawnInCombatAsColorless(list, rng, null, LMSK.Player().chosenClass))
                    appends.add(c.makeCopy());
            });
            list.addAll(appends);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }
    
    @SpirePatch2(clz = AbstractDungeon.class, method = "returnTrulyRandomColorlessCardFromAvailable",
            paramtypez = {String.class, Random.class})
    public static class ReturnRandomColorlessCardFromAvailablePatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"list"})
        public static void Insert(ArrayList<AbstractCard> list, String prohibited, Random rng) {
            List<AbstractCard> cards = LManager.GetAdditionSpawnCards(LMSK.Player().chosenClass);
            List<AbstractCard> appends = new ArrayList<>();
            assert cards != null;
            cards.forEach(c -> {
                if (c instanceof AdditionalSpawnCard
                        && ((AdditionalSpawnCard) c).canSpawnInCombatAsColorless(list, rng, prohibited, LMSK.Player().chosenClass))
                    appends.add(c.makeCopy());
            });
            list.addAll(appends);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(ArrayList.class, "get");
                return LineFinder.findInOrder(ctBehavior, matcher);
            }
        }
    }
}