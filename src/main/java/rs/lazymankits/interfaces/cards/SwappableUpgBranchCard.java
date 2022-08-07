package rs.lazymankits.interfaces.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import org.apache.commons.lang3.NotImplementedException;
import rs.lazymankits.patches.branchupgrades.SwappableLogicPatch;

import java.util.List;

public interface SwappableUpgBranchCard {
    
    default int getChosenBranchIndex() {
        if (this instanceof AbstractCard) {
            if (this instanceof BranchableUpgradeCard)
                return ((BranchableUpgradeCard) this).chosenBranch();
            throw new NotImplementedException("[" + ((AbstractCard) this).name + "] IS NOT A BRANCHABLE UPGRADE CARD");
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    default List<UpgradeBranch> getAllBranches() {
        if (this instanceof AbstractCard) {
            if (this instanceof BranchableUpgradeCard)
                return ((BranchableUpgradeCard) this).getPossibleBranches();
            throw new NotImplementedException("[" + ((AbstractCard) this).name + "] IS NOT A BRANCHABLE UPGRADE CARD");
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    default boolean hasSwappableBranches() {
        if (this instanceof AbstractCard) {
            List<UpgradeBranch> branches = getAllBranches();
            return branches.size() >= 2;
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    default boolean swappable() {
        return hasSwappableBranches() && canSwap();
    }
    
    default boolean canSwap() {
        return true;
    }
    
    default List<UpgradeBranch> getSwappableBranches() {
        if (this instanceof AbstractCard && swappable()) {
            List<UpgradeBranch> branches = getAllBranches();
            int chosenIndex = getChosenBranchIndex();
//            LMDebug.Log("Removing [" + ((AbstractCard) this).name + "]'s chosen branch: " + chosenIndex);
            branches.remove(chosenIndex);
            return branches;
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    default List<UpgradeBranch> getSwappableBranches(int chosenIndex) {
        if (this instanceof AbstractCard && swappable()) {
            List<UpgradeBranch> branches = getAllBranches();
//            LMDebug.Log("Removing [" + ((AbstractCard) this).name + "]'s chosen branch: " + chosenIndex);
            branches.remove(chosenIndex);
            return branches;
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    default void swappingUpgrade(int oldBranch) {
        if (this instanceof AbstractCard && this instanceof BranchableUpgradeCard) {
            this.getSwappableBranches(oldBranch).get(getChosenBranchIndex()).upgrade();
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    @Deprecated
    default AbstractCard getPlainSourceCopy(int oldBranch) {
        if (this instanceof AbstractCard && this instanceof BranchableUpgradeCard) {
            AbstractCard card = ((AbstractCard) this).makeCopy();
            ((BranchableUpgradeCard) card).setChosenBranch(oldBranch);
            return card;
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    default AbstractCard getPlainSourceCopy() {
        if (this instanceof AbstractCard) {
            return ((AbstractCard) this).makeCopy();
        }
        throw new NotImplementedException("NOT A CARD");
    }
    
    default AbstractCard getChosenBranchCopy() {
        if (this instanceof AbstractCard) {
            return SwappableLogicPatch.GetChosenBranch((AbstractCard) this);
        }
        throw new NotImplementedException("NOT A CARD");
    }
}