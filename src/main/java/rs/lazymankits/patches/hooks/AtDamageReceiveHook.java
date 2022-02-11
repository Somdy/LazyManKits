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

public class AtDamageReceiveHook {
    @SpirePatch(clz = AbstractCard.class, method = "calculateCardDamage")
    public static class CalcCardDamage {
        @SpireInsertPatch(rloc = 29, localvars = {"tmp"})
        public static void Insert1(AbstractCard _inst, AbstractMonster m, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageReceive(tmp[0], _inst.damageTypeForTurn, _inst, m);
        }
        @SpireInsertPatch(rloc = 88, localvars = {"tmp"})
        public static void Insert2(AbstractCard _inst, AbstractMonster m, float[] tmp) {
            for (int i = 0; i < tmp.length; i++) {
                tmp[i] = LMSubscriber.AtDamageReceive(tmp[i], _inst.damageTypeForTurn, _inst, m);
            }
        }
    }

    @SpirePatch(clz = DamageInfo.class, method = "applyPowers")
    public static class ApplyDamageInfoPowers {
        @SpireInsertPatch(rloc = 34, localvars = {"tmp"})
        public static void Insert1(DamageInfo _inst, AbstractCreature owner, AbstractCreature target, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageReceive(tmp[0], _inst.type, new AbstractCreature[]{owner, target});
        }
        @SpireInsertPatch(rloc = 71, localvars = {"tmp"})
        public static void Insert2(DamageInfo _inst, AbstractCreature owner, AbstractCreature target, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageReceive(tmp[0], _inst.type, new AbstractCreature[]{owner, target});
        }
    }

    @SpirePatch(clz = DamageInfo.class, method = "applyEnemyPowersOnly")
    public static class ApplyDamageInfoEnemyPowers {
        @SpireInsertPatch(rloc = 13, localvars = {"tmp"})
        public static void Insert1(DamageInfo _inst, AbstractCreature target, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageReceive(tmp[0], _inst.type, new AbstractCreature[]{_inst.owner, target});
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "calculateDamage")
    public static class MonsterCalcDamage {
        @SpireInsertPatch(rloc = 22, localvars = {"target", "tmp"})
        public static void Insert(AbstractMonster _inst, int dmg, AbstractPlayer target, @ByRef float[] tmp) {
            tmp[0] = LMSubscriber.AtDamageReceive(tmp[0], DamageInfo.DamageType.NORMAL, new AbstractCreature[]{_inst, target});
        }
    }
}