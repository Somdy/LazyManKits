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
import java.util.List;

@Inencapsulated
public interface BranchableUpgradeCard extends CustomSavable<Integer> {
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
            LMDebug.Log("Setting " + ((AbstractCard) this).name + "'s final branch: " + branch);
            BranchableUpgradePatch.CardBranchField.ChosenBranch.set(this, branch);
        }
    }
    
    default int chosenBranch() {
        if (this instanceof AbstractCard) {
            int branch = BranchableUpgradePatch.CardBranchField.ChosenBranch.get(this);
            //LMDebug.Log("Getting " + ((AbstractCard) this).name + "'s final branch: " + branch);
            return branch > -1 ? branch : defaultBranch();
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
        if (this instanceof AbstractCard)
            ((AbstractCard) this).upgrade();
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

    @Override
    default Integer onSave() {
        if (this instanceof AbstractCard) {
            int branch = chosenBranch();
            LMDebug.Log("Saving " + ((AbstractCard) this).name + "'s final branch: " + branch);
            return chosenBranch();
        } else 
            throw new NotImplementedException("Not implemented branchable");
    }

    @Override
    default void onLoad(Integer branch) {
        if (branch == null) return;
        if (this instanceof AbstractCard) {
            BranchableUpgradePatch.CardBranchField.ChosenBranch.set(this, branch);
            int times = BranchableUpgradePatch.GetBranchUpgradedTimes((AbstractCard) this);
            for (int i = 0; i < times; i++) {
                upgradeCalledOnSL();
            }
        } else 
            throw new NotImplementedException("Not implemented branchable");
    }

    @Override
    default Type savedType() {
        return new TypeToken<Integer>(){}.getType();
    }
}