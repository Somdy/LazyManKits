package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import rs.lazymankits.interfaces.LMSubscriber;

public class OnShuffleHook {
    @SpirePatches2({
            @SpirePatch2(clz = EmptyDeckShuffleAction.class, method = SpirePatch.CONSTRUCTOR),
            @SpirePatch2(clz = ShuffleAllAction.class, method = SpirePatch.CONSTRUCTOR)
    })
    public static class ConstructorsPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            LMSubscriber.PublishOnShuffle();
        }
    }
    @SpirePatch2(clz = ShuffleAction.class, method = "update")
    public static class ShuffleActionUpdatePatch {
        @SpireInsertPatch(rloc = 4)
        public static void Insert(boolean ___triggerRelics) {
            if (___triggerRelics) {
                LMSubscriber.PublishOnShuffle();
            }
        }
    }
}