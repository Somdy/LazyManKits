package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import rs.lazymankits.LManager;
import rs.lazymankits.interfaces.LMSubscriber;

public class TurnStartHook {
    @SpirePatch(clz = AbstractCreature.class, method = "applyStartOfTurnPostDrawPowers")
    public static class TurnStartPostDraw {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature _inst) {
            if (!LManager.PostTurnStartDraw)
                LManager.PostTurnStartDraw = true;
            LManager.ReceiveOnTurnStart(_inst, true);
            LMSubscriber.PublishPostTurnStart(_inst, true);
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "applyStartOfTurnPowers")
    public static class TurnStart {
        @SpirePrefixPatch
        public static void Prefix(AbstractCreature _inst) {
            if (LManager.PostTurnStartDraw)
                LManager.PostTurnStartDraw = false;
            LManager.ReceiveOnTurnStart(_inst, false);
            LMSubscriber.PublishPostTurnStart(_inst, false);
        }
    }
}