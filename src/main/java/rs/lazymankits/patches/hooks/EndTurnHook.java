package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.DiscardAtEndOfTurnAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import rs.lazymankits.LManager;
import rs.lazymankits.interfaces.LMSubscriber;

public class EndTurnHook {
    @SpirePatch(clz = AbstractCreature.class, method = "applyEndOfTurnTriggers")
    public static class PostEndTurnHook {
        @SpirePostfixPatch
        public static void Postfix(AbstractCreature _inst) {
            if (_inst instanceof AbstractMonster) {
                LMSubscriber.PublishMonsterTurnEnds((AbstractMonster) _inst);
            }
            else if (_inst instanceof AbstractPlayer)
                LManager.ReceiveOnPlayerEndsTurn((AbstractPlayer) _inst);
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "endTurn")
    public static class EndTurnPreDiscardHook {
        public static void Prefix(AbstractRoom _inst) {
            LMSubscriber.PublishEndTurnPreDiscard();
            LManager.ReceiveOnEndTurnPreDiscard();
        }
    }
    
    @SpirePatch(clz = MonsterGroup.class, method = "applyEndOfTurnPowers")
    public static class EndOfRoundHook {
        @SpireInsertPatch(rloc = 7)
        public static void Insert(MonsterGroup _inst) {
            LManager.ReceiveAtEndOfRound();
        }
    }
}