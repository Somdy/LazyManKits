package rs.lazymankits.patches.branchupgrades;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.actions.unique.ArmamentsAction;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.actions.unique.TransmutationAction;
import com.megacrit.cardcrawl.actions.utility.ChooseOneColorless;
import com.megacrit.cardcrawl.actions.watcher.LessonLearnedAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.MindBloom;
import com.megacrit.cardcrawl.events.city.BackToBasics;
import com.megacrit.cardcrawl.events.exordium.ShiningLight;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import javassist.CtBehavior;
import rs.lazymankits.interfaces.LMSubscriber;
import rs.lazymankits.interfaces.cards.BranchableUpgradeCard;
import rs.lazymankits.interfaces.cards.RUM;
import rs.lazymankits.utils.LMSK;

import java.util.ArrayList;

public class VanillaRandomUpgradePatch {
    @SpirePatch(clz = MoltenEgg2.class, method = "onObtainCard")
    public static class MoltenEggPatch {
        @SpirePrefixPatch
        public static SpireReturn Prefix(MoltenEgg2 _inst, AbstractCard card) {
            if (card.type == AbstractCard.CardType.ATTACK && card.canUpgrade() && !card.upgraded) {
                if (CheckCardRUM(card, RUM.MOLTEN_EGG)) {
                    card.upgrade();
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
    
    @SpirePatch(clz = ToxicEgg2.class, method = "onObtainCard")
    public static class ToxicEggPatch {
        @SpirePrefixPatch
        public static SpireReturn Prefix(ToxicEgg2 _inst, AbstractCard card) {
            if (card.type == AbstractCard.CardType.SKILL && card.canUpgrade() && !card.upgraded) {
                if (CheckCardRUM(card, RUM.TOXIC_EGG)) {
                    card.upgrade();
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
    
    @SpirePatch(clz = FrozenEgg2.class, method = "onObtainCard")
    public static class FrozenEggPatch {
        @SpirePrefixPatch
        public static SpireReturn Prefix(FrozenEgg2 _inst, AbstractCard card) {
            if (card.type == AbstractCard.CardType.POWER && card.canUpgrade() && !card.upgraded) {
                if (CheckCardRUM(card, RUM.FROZEN_EGG)) {
                    card.upgrade();
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }
    }
    
    @SpirePatches({
            @SpirePatch(clz = Whetstone.class, method = "onEquip"),
            @SpirePatch(clz = WarPaint.class, method = "onEquip")
    })
    public static class WhetstoneAndWarPaintPatch {
        @SpireInsertPatch(locator = Locator1.class, localvars = {"upgradableCards"})
        public static void Insert1(AbstractRelic _inst, ArrayList<AbstractCard> list) {
            CheckCardRUM(list.get(0), _inst instanceof Whetstone ? RUM.WHETSTONE : RUM.WAR_PAINT);
        }
        private static class Locator1 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    
        @SpireInsertPatch(locator = Locator2.class, localvars = {"upgradableCards"})
        public static void Insert2(AbstractRelic _inst, ArrayList<AbstractCard> list) {
            CheckCardRUM(list.get(0), _inst instanceof Whetstone ? RUM.WHETSTONE : RUM.WAR_PAINT);
            CheckCardRUM(list.get(1), _inst instanceof Whetstone ? RUM.WHETSTONE : RUM.WAR_PAINT);
        }
        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[1];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = LessonLearnedAction.class, method = "update")
    public static class LessonLearnedActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"theCard"})
        public static void Insert(LessonLearnedAction _inst, AbstractCard card) {
            CheckCardRUM(card, RUM.LESSONS_LEARNT);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = MakeTempCardAtBottomOfDeckAction.class, method = "update")
    public static class MakeTempCardAtBottomOfDeckActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractGameAction _inst, AbstractCard card) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(
            clz = MakeTempCardInDiscardAction.class, 
            method = SpirePatch.CONSTRUCTOR, 
            paramtypez = {AbstractCard.class, boolean.class}
    )
    public static class MakeTempCardInDiscardActionPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameAction _inst, AbstractCard card, boolean b) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = MakeTempCardInDrawPileAction.class, method = "update")
    public static class MakeTempCardInDrawPileActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractGameAction _inst, AbstractCard card) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                return LineFinder.findAllInOrder(ctBehavior, matcher);
            }
        }
    }
    
    @SpirePatch(
            clz = MakeTempCardInHandAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, boolean.class}
    )
    public static class MakeTempCardInHandActionPatch1 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameAction _inst, AbstractCard card, boolean b) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(
            clz = MakeTempCardInHandAction.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, int.class}
    )
    public static class MakeTempCardInHandActionPatch2 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameAction _inst, AbstractCard card, int amount) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(
            clz = ShowCardAndAddToDiscardEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class}
    )
    public static class ShowCardAndAddToDiscardEffectPatch1 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameEffect _inst, AbstractCard card, float x, float y) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(
            clz = ShowCardAndAddToDiscardEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class}
    )
    public static class ShowCardAndAddToDiscardEffectPatch2 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameEffect _inst, AbstractCard card) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(
            clz = ShowCardAndAddToDrawPileEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class}
    )
    public static class ShowCardAndAddToDrawPileEffectPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameEffect _inst, AbstractCard card, 
                                  float x, float y, boolean a, boolean b, boolean c) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
            LMSubscriber.PublishOnMakingCard(card, LMSK.Player().drawPile);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(
            clz = ShowCardAndAddToHandEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class}
    )
    public static class ShowCardAndAddToHandEffectPatch1 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameEffect _inst, AbstractCard card, float x, float y) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
            LMSubscriber.PublishOnMakingCard(card, LMSK.Player().hand);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(
            clz = ShowCardAndAddToHandEffect.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class}
    )
    public static class ShowCardAndAddToHandEffectPatch2 {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(AbstractGameEffect _inst, AbstractCard card) {
            CheckCardRUM(card, RUM.MASTER_REALITY);
            LMSubscriber.PublishOnMakingCard(card, LMSK.Player().hand);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = DiscoveryAction.class, method = "update")
    public static class DiscoveryActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"disCard", "disCard2"})
        public static void Insert(AbstractGameAction _inst, AbstractCard c1, AbstractCard c2) {
            CheckCardRUM(c1, RUM.MASTER_REALITY);
            CheckCardRUM(c2, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = ChooseOneColorless.class, method = "update")
    public static class ChooseOneColorlessPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"disCard"})
        public static void Insert(AbstractGameAction _inst, AbstractCard c1) {
            CheckCardRUM(c1, RUM.MASTER_REALITY);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = ArmamentsAction.class, method = "update")
    public static class ArmamentsActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractGameAction _inst, AbstractCard c) {
            CheckCardRUM(c, RUM.ARMAMENTS);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = ApotheosisAction.class, method = "upgradeAllCardsInGroup")
    public static class ApotheosisActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractGameAction _inst, AbstractCard c) {
            CheckCardRUM(c, RUM.APOTHEOSIS);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = UpgradeRandomCardAction.class, method = "update")
    public static class UpgradeRandomCardActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"upgradeable"})
        public static void Insert(AbstractGameAction _inst, CardGroup upgradeable) {
            CheckCardRUM(upgradeable.group.get(0), RUM.WARPED_TONGS);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = MindBloom.class, method = "buttonEffect")
    public static class MindBloomPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractEvent _inst, int b, AbstractCard c) {
            CheckCardRUM(c, RUM.BLOSSOM);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch(clz = TransmutationAction.class, method = "update")
    public static class TransmutationActionPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractGameAction _inst, AbstractCard c) {
            CheckCardRUM(c, RUM.TRANSMUTATION);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(clz = AbstractDungeon.class, method = "transformCard", 
            paramtypez = {AbstractCard.class, boolean.class, Random.class})
    public static class AbstractDungeonTransformPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert() {
            CheckCardRUM(AbstractDungeon.transformedCard, RUM.TRANSFORM_UPGRADE);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    public static class AbstractDungeonRewardPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractCard c) {
            CheckCardRUM(c, RUM.REWARD_UPGRADE);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(clz = BackToBasics.class, method = "upgradeStrikeAndDefends")
    public static class BackToBasicsPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"c"})
        public static void Insert(AbstractCard c) {
            CheckCardRUM(c, RUM.BACK_TO_BASICS);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
    }
    
    @SpirePatch2(clz = ShiningLight.class, method = "upgradeCards")
    public static class ShiningLightPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"upgradableCards"})
        public static void Insert(ArrayList<AbstractCard> upgradableCards) {
            CheckCardRUM(upgradableCards.get(0), RUM.SHINING_LIGHT);
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[0];
                return new int[] {line};
            }
        }
        @SpireInsertPatch(locator = Locator2.class, localvars = {"upgradableCards"})
        public static void Insert2(ArrayList<AbstractCard> upgradableCards) {
            CheckCardRUM(upgradableCards.get(0), RUM.SHINING_LIGHT);
            CheckCardRUM(upgradableCards.get(1), RUM.SHINING_LIGHT);
        }
        private static class Locator2 extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "upgrade");
                int line = LineFinder.findAllInOrder(ctBehavior, matcher)[1];
                return new int[] {line};
            }
        }
    }
    
    private static boolean CheckCardRUM(AbstractCard card, int msg) {
        if (card instanceof BranchableUpgradeCard && ((BranchableUpgradeCard) card).canBranch()
                && ((BranchableUpgradeCard) card).allowBranchWhenUpgradeBy(msg)) {
            int branchID = ((BranchableUpgradeCard) card).getBranchForRandomUpgrading(msg);
            ((BranchableUpgradeCard) card).setChosenBranch(branchID);
            return true;
        }
        return false;
    }
}