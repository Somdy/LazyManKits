package rs.lazymankits.abstracts;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import rs.lazymankits.actions.CustomDmgInfo;
import rs.lazymankits.actions.DamageSource;
import rs.lazymankits.utils.LMGameGeneralUtils;

import java.util.Optional;

public class LMCustomRelic extends CustomRelic implements LMGameGeneralUtils {
    private boolean rightClickStarted;
    private boolean rightClicked;

    public LMCustomRelic(String id, Texture texture, Texture outline, RelicTier tier, LandingSound sfx) {
        super(id, texture, outline, tier, sfx);
        rightClicked = rightClickStarted = false;
    }

    protected void addTips(String head, String body) {
        if (tips.stream().noneMatch(t -> t.header.equals(head) || t.body.equals(body)))
            tips.add(new PowerTip(head, body));
    }

    protected void replaceTips(String head, String body) {
        Optional<PowerTip> tip = tips.stream().filter(t -> t.header.equals(head) || t.body.equals(body)).findFirst();
        if (tip.isPresent()) {
            tips.remove(tip.get());
        } else {
            addTips(head, body);
        }
    }

    protected void resetOverlayInfo() {
        tips.clear();
        tips.add(new PowerTip(name, description));
        initializeTips();
    }

    protected CustomDmgInfo crtDmgInfo(AbstractCreature source, int base, DamageInfo.DamageType type) {
        return new CustomDmgInfo(crtDmgSrc(source), base, type);
    }

    protected DamageSource crtDmgSrc(AbstractCreature source) {
        return new DamageSource(source, this);
    }

    protected AbstractPlayer cpr() {
        return AbstractDungeon.player;
    }

    protected boolean onRightClick() {
        return false;
    }

    @Override
    public void update() {
        super.update();
        if (isObtained && hb != null && hb.hovered && InputHelper.justClickedRight) {
            rightClickStarted = true;
        }
        if (rightClickStarted && InputHelper.justReleasedClickRight) {
            if (hb != null && hb.hovered)
                rightClicked = true;
            rightClickStarted = false;
        }
        if (rightClicked) {
            onRightClick();
            rightClicked = false;
        }
    }
}