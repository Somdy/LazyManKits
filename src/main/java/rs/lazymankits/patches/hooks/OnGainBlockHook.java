package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import rs.lazymankits.abstracts.LMCustomPower;
import rs.lazymankits.interfaces.LMSubscriber;

public class OnGainBlockHook {
    @SpirePatch(clz = AbstractCreature.class, method = "addBlock")
    public static class ModifyBlockPatch {
        @SpireInsertPatch(rloc = 1, localvars = {"tmp"})
        public static void Insert(AbstractCreature _inst, int blockAmount, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.PublishOnGainBlock(_inst, tmp[0]);
            for (AbstractPower p : _inst.powers) {
                if (p instanceof LMCustomPower)
                    tmp[0] = ((LMCustomPower) p).modifyOnGainingBlock(tmp[0]);
            }
        }
    }
}