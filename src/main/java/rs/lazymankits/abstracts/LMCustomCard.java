package rs.lazymankits.abstracts;

import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import rs.lazymankits.actions.CustomDmgInfo;
import rs.lazymankits.actions.DamageSource;
import rs.lazymankits.utils.LMGameGeneralUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LMCustomCard extends CustomCard implements LMGameGeneralUtils {
    protected List<TooltipInfo> tips;
    
    public LMCustomCard(String id, String name, String img, int cost, String rawDescription, CardType type, CardColor color, CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
    }

    protected void addTip(String head, String body) {
        if (tips == null)
            tips = new ArrayList<>();
        if (tips.stream().anyMatch(t -> t.title.equals(head) && t.description.equals(body)))
            return;
        tips.add(new TooltipInfo(head, body));
    }

    protected boolean replaceTip(String head, String body) {
        if (tips == null)
            return false;
        for (TooltipInfo tip : tips) {
            if (tip.title.equals(head) && !tip.description.equals(body)) {
                tip.description = body;
                return true;
            }
        }
        return false;
    }

    protected boolean removeTip(String head) {
        return removeTip(head, null);
    }

    protected boolean removeTip(String head, String body) {
        if (tips == null)
            return false;
        return tips.removeIf(t -> body == null ? t.title.equals(head) : (t.title.equals(head) && t.description.equals(body)));
    }

    @Override
    public List<TooltipInfo> getCustomTooltips() {
        return tips;
    }

    protected boolean inHand() {
        return inHand(this);
    }

    protected boolean inHand(AbstractCard card) {
        return cpr().hand.contains(card);
    }

    protected boolean addTags(CardTags... tags) {
        return this.tags.addAll(Arrays.asList(tags));
    }

    protected boolean addTags(List<CardTags> tags) {
        return this.tags.addAll(tags);
    }

    @Override
    protected void addToBot(AbstractGameAction action) {
        if (action != null)
            super.addToBot(action);
    }

    @Override
    protected void addToTop(AbstractGameAction action) {
        if (action != null)
            super.addToTop(action);
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

    public void setCostValue(int num, boolean ignoreModifier) {
        if (!ignoreModifier) {
            if (isCostModifiedForTurn || isCostModified || upgradedCost) return;
        }
        cost = costForTurn = num;
    }

    public void setDamageValue(int num, boolean ignoreModifier) {
        if (!ignoreModifier) {
            if (isDamageModified || upgradedDamage) return;
        }
        damage = baseDamage = num;
        if (baseDamage < 0) baseDamage = 0;
    }

    public void setBlockValue(int num, boolean ignoreModifier) {
        if (!ignoreModifier) {
            if (isBlockModified || upgradedBlock) return;
        }
        block = baseBlock = num;
        if (baseBlock < 0) baseBlock = 0;
    }

    public void setMagicValue(int num, boolean ignoreModifier) {
        if (!ignoreModifier) {
            if (isMagicNumberModified || upgradedMagicNumber) return;
        }
        magicNumber = baseMagicNumber = num;
        if (baseMagicNumber < 0) baseMagicNumber = 0;
    }
}