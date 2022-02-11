package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import rs.lazymankits.interfaces.LMSubscriber;

public class ModifyBlockHook {
    @SpirePatch(clz = AbstractCard.class, method = "applyPowersToBlock")
    public static class ModifyCardBlock {
        @SpireInsertPatch(rloc = 11, localvars = {"tmp"})
        public static void Insert(AbstractCard _inst, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.ModifyBlock(tmp[0], _inst);
        }
    }
}