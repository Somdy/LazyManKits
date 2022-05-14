package rs.lazymankits.patches.branchupgrades;

import basemod.devcommands.ConsoleCommand;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import rs.lazymankits.devcommands.branches.BranchCMD;

public class DevCommandPatch {
    @SpirePatch(clz = ConsoleCommand.class, method = "initialize")
    public static class AddBranchCommand {
        @SpirePostfixPatch
        public static void Postfix() {
            ConsoleCommand.addCommand("branch", BranchCMD.class);
        }
    }
}