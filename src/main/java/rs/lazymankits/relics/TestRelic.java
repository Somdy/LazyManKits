package rs.lazymankits.relics;

import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import rs.lazymankits.LManager;
import rs.lazymankits.abstracts.LMCustomRelic;
import rs.lazymankits.vfx.utils.SwapUpgBranchEffect;

public class TestRelic extends LMCustomRelic {
    public TestRelic() {
        super(LManager.Prefix("TestRelic"), ImageMaster.loadImage("SharedAssets/images/relics/test.png"),
                ImageMaster.loadImage("SharedAssets/images/relics/test.png"), RelicTier.SPECIAL, LandingSound.FLAT);
    }
    
    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
    
    @Override
    protected boolean onRightClick() {
        if (currRoom() instanceof RestRoom && currRoom().phase != AbstractRoom.RoomPhase.COMPLETE) {
            if (!cpr().masterDeck.isEmpty()) {
                effectToList(new SwapUpgBranchEffect(cpr().masterDeck.group));
            }
        }
        return super.onRightClick();
    }
}