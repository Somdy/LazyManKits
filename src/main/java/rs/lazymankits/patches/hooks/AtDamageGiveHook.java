package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import rs.lazymankits.interfaces.LMSubscriber;

public class AtDamageGiveHook {
    @SpirePatch(clz = AbstractCard.class, method = "applyPowers")
    public static class ApplyCardPowers {
        @SpireInsertPatch(rloc = 23, localvars = {"tmp"})
        public static void Insert1(AbstractCard _inst, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageGive(tmp[0], _inst.damageTypeForTurn, _inst);
        }
        @SpireInsertPatch(rloc = 69, localvars = {"tmp", "i"})
        public static void Insert2(AbstractCard _inst, @ByRef float[][] tmp, int i) {
            tmp[0][i] = LMSubscriber.AtDamageGive(tmp[0][i], _inst.damageTypeForTurn, _inst);
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class CalcCardDamage {
        @SpireInsertPatch(rloc = 23, localvars = {"tmp"})
        public static void Insert1(AbstractCard _inst, AbstractMonster m, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageGive(tmp[0], _inst.damageTypeForTurn, _inst);
        }
        @SpireInsertPatch(rloc = 76, localvars = {"tmp", "i"})
        public static void Insert2(AbstractCard _inst, AbstractMonster m, @ByRef float[][] tmp, int i) {
            tmp[0][i] = LMSubscriber.AtDamageGive(tmp[0][i], _inst.damageTypeForTurn, _inst);
        }
    }

    @SpirePatch(clz = DamageInfo.class, method = "applyPowers")
    public static class ApplyDamageInfoPowers {
        @SpireInsertPatch(rloc = 34, localvars = {"tmp"})
        public static void Insert1(DamageInfo _inst, AbstractCreature owner, AbstractCreature target, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageGive(tmp[0], _inst.type, new AbstractCreature[]{owner, target});
        }
        @SpireInsertPatch(rloc = 71, localvars = {"tmp"})
        public static void Insert2(DamageInfo _inst, AbstractCreature owner, AbstractCreature target, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageGive(tmp[0], _inst.type, new AbstractCreature[]{owner, target});
        }
    }
    
    @SpirePatch(clz = AbstractMonster.class, method = "calculateDamage")
    public static class MonsterCalcDamage {
        @SpireInsertPatch(rloc = 22, localvars = {"target", "tmp"})
        public static void Insert(AbstractMonster _inst, int dmg, AbstractPlayer target, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageGive(tmp[0], DamageInfo.DamageType.NORMAL, new AbstractCreature[]{_inst, target});
        }
    }
}