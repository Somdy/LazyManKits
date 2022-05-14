package rs.lazymankits.devcommands.branches;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.devcommands.hand.Hand;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import rs.lazymankits.interfaces.cards.BranchableUpgradeCard;
import rs.lazymankits.utils.LMSK;

import java.util.ArrayList;

public class DeckAddBranch extends ConsoleCommand {
    
    @Override
    protected void execute(String[] tokens, int depth) {
        int branchIndex = Hand.countIndex(tokens);
        String cardName = Hand.cardName(tokens, depth);
        AbstractCard card = CardLibrary.getCard(cardName).makeStatEquivalentCopy();
        if (card == null) {
            DevConsole.log("Could not find card " + cardName);
            return;
        }
        if (!(card instanceof BranchableUpgradeCard)) {
            DevConsole.log(cardName + " is not a branch-upgrade card");
            return;
        }
        if (!((BranchableUpgradeCard) card).canBranch()) {
            DevConsole.log(cardName + " has branches but not allowed");
            return;
        }
        int branchID = ((BranchableUpgradeCard) card).defaultBranch();
        if (tokens.length > branchIndex + 1 && ConvertHelper.tryParseInt(tokens[branchIndex + 1]) != null)
            branchID = ConvertHelper.tryParseInt(tokens[branchIndex + 1], branchID);
        int maxBranch = ((BranchableUpgradeCard) card).getPossibleBranches().size() - 1;
        int count = 1;
        if (tokens.length > branchIndex + 2 && ConvertHelper.tryParseInt(tokens[count + 2]) != null)
            count = ConvertHelper.tryParseInt(tokens[branchIndex + 2], 1);
        if (branchID > maxBranch) {
            DevConsole.log(cardName + " has only " + (maxBranch + 1) + " branches but asked for branch " + branchID);
            DevConsole.log("Note that branch index should start at 0");
            DevConsole.log("Setting " + cardName + "'s chosen branch to the last branch");
            branchID = maxBranch;
        }
        DevConsole.log("Adding " + count + (count == 1 ? " copy of " : " copies of ") + cardName
                + " upgraded at branch " + branchID);
        ((BranchableUpgradeCard) card).setChosenBranch(branchID);
        card.upgrade();
        for (int i = 0; i < count; i++) {
            AbstractCard copy = card.makeStatEquivalentCopy();
            LMSK.AddToBot(new VFXAction(new ShowCardAndObtainEffect(copy, Settings.WIDTH / 2F, Settings.HEIGHT / 2F)));
        }
    }
    
    @Override
    protected ArrayList<String> extraOptions(String[] tokens, int depth) {
        ArrayList<String> opts = BranchCMD.GetBranchableCardOpts();
        if (opts.contains(tokens[depth])) {
            if (tokens.length > depth + 1 && tokens[depth + 1].matches("\\d*")) {
                if (tokens.length > depth + 2) {
                    if (tokens[depth + 2].matches("\\d+")) {
                        ConsoleCommand.complete = true;
                    } else if (tokens[depth + 3].length() > 0) {
                        tooManyTokensError();
                    }
                }
                return ConsoleCommand.smallNumbers();
            }
        }
        return opts;
    }
    
    @Override
    protected void errorMsg() {
        BranchCMD.DisplayHandBranchOpts();
    }
}