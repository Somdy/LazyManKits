package rs.lazymankits.interfaces.cards;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.commons.lang3.NotImplementedException;
import rs.lazymankits.LMDebug;
import rs.lazymankits.annotations.Inencapsulated;
import rs.lazymankits.patches.branchupgrades.BranchableUpgradePatch;
import rs.lazymankits.utils.LMSK;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Inencapsulated
public interface BranchableUpgradeCard extends CustomSavable<Map<String, String>> {
    List<UpgradeBranch> possibleBranches();
    
    default List<UpgradeBranch> getPossibleBranches() {
        if (this instanceof AbstractCard) {
            return possibleBranches();
        }
        return new ArrayList<>();
    }
    
    default int defaultBranch() {
        if (canBranch())
            return 0;
        return -1;
    }
    
    default boolean canBranch() {
        if (this instanceof AbstractCard) {
            return getPossibleBranches().size() > 0;
        }
        return false;
    }
    
    default void setChosenBranch(int branch) {
        if (this instanceof AbstractCard) {
            LMDebug.Log("Setting [" + ((AbstractCard) this).name + "]'s chosen branch: " + branch);
            BranchableUpgradePatch.CardBranchField.ChosenBranch.set(this, branch);
            BranchableUpgradePatch.CardBranchField.LocalChosenBranch.set(this, branch);
        }
    }
    
    default void setLocalBranch(int branch) {
        if (this instanceof AbstractCard) {
            if (BranchableUpgradePatch.CardBranchField.LocalChosenBranch.get(this) == branch) return;
//            LMDebug.Log("Setting " + ((AbstractCard) this).name + "'s local branch: " + branch);
            BranchableUpgradePatch.CardBranchField.LocalChosenBranch.set(this, branch);
        }
    }
    
    default int chosenBranch() {
        if (this instanceof AbstractCard) {
            //LMDebug.Log("Getting " + ((AbstractCard) this).name + "'s final branch: " + branch);
            int branch = BranchableUpgradePatch.CardBranchField.ChosenBranch.get(this);
            return branch > -1 ? branch : defaultBranch();
        } else 
            throw new NotImplementedException("Not implemented branchable");
    }
    
    default int localBranch() {
        if (this instanceof AbstractCard) {
            int branch = BranchableUpgradePatch.CardBranchField.LocalChosenBranch.get(this);
            return branch > -1 ? branch : defaultBranch();
        } else
            throw new NotImplementedException("Not implemented branchable");
    }
    
    default int finalBranch() {
        if (this instanceof AbstractCard) {
            int branch = chosenBranch();
            int localBranch = localBranch();
            if (branch != localBranch && usingLocalBranch()) {
                LMDebug.Log(((AbstractCard) this).name + " using local branch [" + localBranch + "] different from chosen [" + branch + "]");
                branch = localBranch;
            }
            return branch;
        } else
            throw new NotImplementedException("Not implemented branchable");
    }
    
    @Deprecated
    default boolean callUpgradeOnSL() {
        if (this instanceof AbstractCard)
            return ((AbstractCard) this).upgraded;
        return false;
    }
    
    default void upgradeCalledOnSL() {
        if (this instanceof AbstractCard) {
            ((AbstractCard) this).upgrade();
        }
    }
    
    default boolean allowBranchWhenUpgradeBy(int msg) {
        return true;
    }
    
    default int branchForRandomUpgrading(int msg) {
        return randomBranch(true);
    }
    
    default int getBranchForRandomUpgrading(int msg) {
        if (this instanceof AbstractCard)
            return branchForRandomUpgrading(msg);
        return -1;
    }
    
    default int randomBranch(boolean useRng) {
        if (useRng) {
            return LMSK.CardRandomRng().random(getPossibleBranches().size() - 1);
        }
        return MathUtils.random(getPossibleBranches().size() - 1);
    }
    
    default boolean usingLocalBranch() {
        return false;
    }
    
    default void upgradeAndCorrectBranch() {
        if (this instanceof AbstractCard) {
            int maxBranch = possibleBranches().size();
            int maxBranchAvailable = getPossibleBranches().size();
//            LMDebug.Log("[" + ((AbstractCard) this).name + "] max branch: " + maxBranch
//                    + ", max available: " + maxBranchAvailable + ", chosen: " + chosenBranch());
            if (maxBranch > maxBranchAvailable && chosenBranch() >= 0) {
//                LMDebug.Log("[" + ((AbstractCard) this).name + "] using alternative branches");
                if (chosenBranch() >= maxBranchAvailable - 1) {
                    if (localBranch() >= 0) {
                        possibleBranches().get(localBranch()).upgrade();
                    }
                    else {
                        getPossibleBranches().get(maxBranchAvailable).upgrade();
                    }
                }
            }
            if (maxBranch == maxBranchAvailable) {
                int chosenBranch = chosenBranch() >= 0 ? chosenBranch() : defaultBranch();
                getPossibleBranches().get(chosenBranch).upgrade();
            }
            if (usingLocalBranch()) {
                if (localBranch() != chosenBranch() && localBranch() >= 0)
                    setChosenBranch(localBranch());
            }
        } else
            throw new NotImplementedException("Not implemented branchable");
    }

    @Override
    default Map<String, String> onSave() {
        if (this instanceof AbstractCard) {
            int branch = chosenBranch();
            int localBranch = localBranch();
            LMDebug.Log("Saving [" + ((AbstractCard) this).name + "]'s chosen branch: " + branch + " and local branch: " + localBranch);
            Map<String, String> map = new HashMap<>();
            map.put("chosenBranch", String.valueOf(branch));
            map.put("localBranch", String.valueOf(localBranch));
            return map;
        } else 
            throw new NotImplementedException("Not implemented branchable");
    }

    @Override
    default void onLoad(Map<String, String> map) {
        if (map == null) return;
        if (this instanceof AbstractCard) {
            int branch = Integer.parseInt(map.get("chosenBranch"));
            int localBranch = Integer.parseInt(map.get("localBranch"));
            BranchableUpgradePatch.CardBranchField.ChosenBranch.set(this, branch);
            BranchableUpgradePatch.CardBranchField.LocalChosenBranch.set(this, localBranch);
            LMDebug.Log("Loading [" + ((AbstractCard) this).name + "]'s chosen branch: " + branch + " and local branch: " + localBranch);
            int times = BranchableUpgradePatch.GetBranchUpgradedTimes((AbstractCard) this);
            for (int i = 0; i < times; i++) {
                upgradeCalledOnSL();
            }
        } else 
            throw new NotImplementedException("Not implemented branchable");
    }

    @Override
    default Type savedType() {
        return new TypeToken<Map<String, String>>(){}.getType();
    }
}