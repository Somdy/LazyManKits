package rs.lazymankits.devcommands.branches;

import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.interfaces.cards.BranchableUpgradeCard;

import java.util.ArrayList;

public class HandBranch extends ConsoleCommand {
    
    public HandBranch() {
        followup.put("add", HandAddBranch.class);
        //followup.put("upgrade", HandUpgradeBranch.class);
        requiresPlayer = true;
        simpleCheck = true;
    }
    
    @Override
    protected void execute(String[] strings, int i) {
        DisplayHandBranchOpts();
    }
    
    @Override
    protected void errorMsg() {
        DisplayHandBranchOpts();
    }
    
    public static void DisplayHandBranchOpts() {
        DevConsole.couldNotParse();
        DevConsole.log("Options are: ");
        DevConsole.log("* add [id] {branch index} {count}");
        DevConsole.log("* upgrade [id] {branch index} Deprecated");
        DevConsole.log("Note that branch index starts at 0");
    }
    
    @NotNull
    public static ArrayList<String> GetBranchableCardOpts() {
        ArrayList<String> opts = new ArrayList<>();
        for (String key : CardLibrary.cards.keySet()) {
            AbstractCard opt = CardLibrary.cards.get(key);
            if (opt instanceof BranchableUpgradeCard)
                opts.add(key.replace(" ", "_"));
        }
        return opts;
    }
}