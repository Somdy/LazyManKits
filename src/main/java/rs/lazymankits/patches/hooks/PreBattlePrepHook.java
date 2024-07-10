package rs.lazymankits.patches.hooks;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import rs.lazymankits.LManager;

@SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
public class PreBattlePrepHook {
    @SpirePrefixPatch
    public static void PrefixCall(AbstractPlayer _inst) {
        LManager.ReceiveOnPreBattlePreparationForNoCharactersInvolved();
    }
}