package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import rs.lazymankits.interfaces.LMSubscriber;

public class OnAttackHook {
    @SpirePatch(clz = AbstractPlayer.class, method = "damage")
    public static class PlayerDamage {
        @SpireInsertPatch(rloc = 61, localvars = {"damageAmount"})
        public static void Insert(AbstractPlayer _inst, DamageInfo info, int damageAmount) {
            LMSubscriber.PublishOnAttacked(damageAmount, info, _inst);
        }
    }

    @SpirePatch(clz = AbstractMonster.class, method = "damage")
    public static class MonsterDamage {
        @SpireInsertPatch(rloc = 59, localvars = {"damageAmount"})
        public static void Insert(AbstractMonster _inst, DamageInfo info, int damageAmount) {
            LMSubscriber.PublishOnAttacked(damageAmount, info, _inst);
        }
    }
}