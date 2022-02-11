package rs.lazymankits.cards;

import com.megacrit.cardcrawl.cards.CardSave;

public class BranchableCardSave extends CardSave {
    public int chosenBranch;
    
    public BranchableCardSave(String cardID, int timesUpgraded, int misc, int chosenBranch) {
        super(cardID, timesUpgraded, misc);
        this.chosenBranch = chosenBranch;
    }
}