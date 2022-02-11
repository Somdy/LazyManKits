package rs.lazymankits.cards;

import org.jetbrains.annotations.NotNull;
import rs.lazymankits.abstracts.LMCustomCard;
import rs.lazymankits.annotations.Replaced;
import rs.lazymankits.data.LMXCardData;

import java.util.List;

public abstract class LMXDataCustomCard extends LMCustomCard {
    
    private LMXCardData.SData data;
    private final int cardsn;
    
    public LMXDataCustomCard(@NotNull LMXCardData data, int sn, String img, CardColor color) {
        super("undetermined", "undetermined", img, -2, "undetermined", 
                CardType.SKILL, color, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.cardsn = sn;
        this.data = data.getSData(cardsn);
        assignBasics();
    }

    public LMXDataCustomCard(@NotNull LMXCardData data, String img, CardColor color) {
        super("undetermined", "undetermined", img, -2, "undetermined",
                CardType.SKILL, color, CardRarity.SPECIAL, CardTarget.ENEMY);
        this.cardsn = uniqueSn();
        this.data = data.getSData(cardsn);
        assignBasics();
    }

    protected void assignBasics() {
        if (!canAssignBasics()) return;
        if (data == null)
            throw new NullPointerException("Unable to find target card data whose id = " + cardsn);
        setCardID(data.getId());
        setCostValue(data.getBaseCost(), true);
        misc = data.getMisc();
        setDamageValue(data.getBaseDmg(), true);
        setBlockValue(data.getBaseBlock(), true);
        setMagicValue(data.getBaseMagics(), true);
        assignDefaultText(data.getText(), data.getPrmtText());
        this.type = data.getType();
        this.rarity = data.getRarity();
        this.target = data.getTarget();
        List<CardTags> tags = data.getTags();
        if (tags != null) addTags(tags);
        LMXCardData.CardAttributes attrs = data.getAttributes();
        this.exhaust = attrs.isExhaust();
        this.selfRetain = attrs.isSelfRetain();
        this.retain = attrs.isRetain();
        this.isInnate = attrs.isInnate();
        this.isEthereal = attrs.isEthereal();
        this.purgeOnUse = attrs.isPurge();
    }

    protected boolean canAssignBasics() {
        return !this.getClass().isAnnotationPresent(Replaced.class);
    }
    
    protected void assignDefaultText(String base, String prmt) {}

    protected LMXCardData.SData data() {
        return data;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public int getCardsn() {
        return cardsn;
    }

    protected int uniqueSn() {
        return -1;
    }
}