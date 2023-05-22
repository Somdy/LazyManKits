package rs.lazymankits.patches.creature;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import rs.lazymankits.abstracts.DamageInfoTag;
import rs.lazymankits.utils.LMDamageInfoHelper;

public class CreatureDamagePatch {
    @SpirePatch(clz = AbstractCreature.class, method = "decrementBlock")
    public static class IgnoreBlockPatch {
        @SpirePrefixPatch
        public static SpireReturn<Integer> Prefix(AbstractCreature _inst, DamageInfo info, int damageAmount) {
            if (LMDamageInfoHelper.HasTag(info, DamageInfoTag.BLOCK_IGNORED))
                return SpireReturn.Return(damageAmount);
            return SpireReturn.Continue();
        }
    }
}