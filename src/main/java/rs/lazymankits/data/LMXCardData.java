package rs.lazymankits.data;

import com.megacrit.cardcrawl.cards.AbstractCard;
import org.dom4j.Element;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.managers.LMCustomCardTagMgr;
import rs.lazymankits.managers.LMCustomRarityMgr;
import rs.lazymankits.utils.LMGameGeneralUtils;

import java.util.*;

public class LMXCardData implements LMGameGeneralUtils {
    private final String id;
    private String prefix;
    private final Map<Integer, SData> SDatas;
    private static final AbstractCard.CardTags[] sealedTags;
    
    private String MainDataName = "LMXCardData";
    private String SubDataName = "SData";

    static {
        sealedTags = new AbstractCard.CardTags[] {AbstractCard.CardTags.STRIKE, AbstractCard.CardTags.STARTER_STRIKE,
                AbstractCard.CardTags.STARTER_DEFEND, AbstractCard.CardTags.HEALING};
    }
    
    public LMXCardData(String id) {
        this.id = id;
        prefix = null;
        SDatas = new HashMap<>();
    }
    
    public boolean valid() {
        return prefix != null && !SDatas.isEmpty();
    }

    public String id() {
        return id;
    }

    public SData getSData(int sn) {
        return SDatas.get(sn);
    }
    
    public final LMXCardData copyData(@NotNull Element data) {
        if (!data.getName().equals(MainDataName) || !data.attributeValue("id").equals(id)) {
            Log(data.getName() + " is not a valid card data regarded with MainDataName = " + MainDataName + ", id = " + id);
            throw new IllegalArgumentException();
        }
        prefix = data.attributeValue("prefix");
        voidrun(() -> {
            for (Element e : data.elements()) {
                if (e.getName().equals(SubDataName) && e.attribute("sn") != null) {
                    SData sData = new SData();
                    sData.sn = Integer.parseInt(e.attributeValue("sn"));
                    sData.id = prefix + e.elementTextTrim("id");
                    if (SDatas.containsKey(sData.sn)) continue;
                    assignCost(sData, e);
                    assignMisc(sData, e);
                    assignDamage(sData, e);
                    assignBlock(sData, e);
                    assignMagics(sData, e);
                    assignText(sData, e);
                    assignType(sData, e);
                    assignTarget(sData, e);
                    if (!assignRarity(sData, e)) {
                        Log("card with sn = " + sData.sn + " has an unknown rarity, assigning special to it");
                        sData.rarity = AbstractCard.CardRarity.SPECIAL;
                    }
                    assignAttributes(sData, e);
                    assignTags(sData, e);
                    if (e.element("customs") != null) {
                        addCustomData(e.element("customs"), sData);
                    }
                    SDatas.put(sData.sn, sData);
                }
            }
            return null;
        });
        return this;
    }
    
    public final LMXCardData appendData(@NotNull Element data) {
        if (!data.getName().equals(MainDataName) || !data.attributeValue("id").equals(id)) {
            Log(data.getName() + " is not a valid card data regarded with MainDataName = " + MainDataName + ", id = " + id);
            throw new IllegalArgumentException();
        }
        String newPrefix = data.attributeValue("prefix");
        if (!prefix.equals(newPrefix)) {
            Log(data.getName() + " is not suitable to append with old data whose prefix is " + prefix);
            return this;
        }
        voidrun(() -> {
            for (Element e : data.elements()) {
                if (e.getName().equals(SubDataName) && e.attribute("sn") != null) {
                    SData sData = new SData();
                    sData.sn = Integer.parseInt(e.attributeValue("sn"));
                    sData.id = prefix + e.elementTextTrim("id");
                    if (SDatas.containsKey(sData.sn)) continue;
                    assignCost(sData, e);
                    assignMisc(sData, e);
                    assignDamage(sData, e);
                    assignBlock(sData, e);
                    assignMagics(sData, e);
                    assignText(sData, e);
                    assignType(sData, e);
                    assignTarget(sData, e);
                    if (!assignRarity(sData, e)) {
                        Log("card with sn = " + sData.sn + " has an unknown rarity, assigning special to it");
                        sData.rarity = AbstractCard.CardRarity.SPECIAL;
                    }
                    assignAttributes(sData, e);
                    assignTags(sData, e);
                    if (e.element("customs") != null) {
                        addCustomData(e.element("customs"), sData);
                    }
                    SDatas.put(sData.sn, sData);
                }
            }
            return null;
        });
        return this;
    }

    protected void addCustomData(@NotNull Element customs, SData sData) {}

    private void assignTags(SData sData, @NotNull Element data) {
        if (data.element("tags") != null) {
            sData.tags = new ArrayList<>();
            String[] tags = data.element("tags").getStringValue().split(",");
            for (String tag : tags) {
                String originalName = tag;
                String tagName = tag
                        .toLowerCase()
                        .replace(" ", "")
                        .replace("_", "")
                        .trim();
                Optional<AbstractCard.CardTags> expected = Arrays.stream(sealedTags)
                        .filter(t -> t.name()
                                .toLowerCase()
                                .replace("_", "")
                                .replace(" ", "")
                                .replace("LMK", "")
                                .trim()
                                .equals(tagName))
                        .findFirst();
                if (!expected.isPresent() && LMCustomCardTagMgr.GetCustomTag(originalName) == null) {
                    Log("Failed to find tag: " + originalName);
                    continue;
                }
                sData.tags.add(expected.orElse(LMCustomCardTagMgr.GetCustomTag(originalName)));
            }
        }
    }

    private void assignAttributes(SData sData, @NotNull Element data) {
        CardAttributes attr = new CardAttributes();
        if (data.element("attributes") != null) {
            List<Element> attrs = data.element("attributes").elements();
            for (Element e : attrs) {
                String name = e.getName().toLowerCase().trim();
                if (name.equals("exhaust")) {
                    attr.exhaust = Boolean.parseBoolean(e.getStringValue());
                    attr.prmtExhaust.set(attr.exhaust);
                }
                if (name.equals("selfretain")) {
                    attr.selfRetain = Boolean.parseBoolean(e.getStringValue());
                    attr.prmtSelfRetain.set(attr.selfRetain);
                }
                if (name.equals("retain")) {
                    attr.retain = Boolean.parseBoolean(e.getStringValue());
                    attr.prmtRetain.set(attr.retain);
                }
                if (name.equals("innate")) {
                    attr.innate = Boolean.parseBoolean(e.getStringValue());
                    attr.prmtInnate.set(attr.innate);
                }
                if (name.equals("ethereal")) {
                    attr.ethereal = Boolean.parseBoolean(e.getStringValue());
                    attr.prmtEthereal.set(attr.ethereal);
                }
                if (name.equals("purge")) {
                    attr.purge = Boolean.parseBoolean(e.getStringValue());
                    attr.prmtPurge.set(attr.purge);
                }
            }
        }
        if (data.element("prmtAttrs") != null) {
            List<Element> attrs = data.element("prmtAttrs").elements();
            for (Element e : attrs) {
                String name = e.getName().toLowerCase().trim();
                if (name.equals("exhaust"))
                    attr.prmtExhaust.setNew(Boolean.parseBoolean(e.getStringValue()));
                if (name.equals("selfretain"))
                    attr.prmtSelfRetain.setNew(Boolean.parseBoolean(e.getStringValue()));
                if (name.equals("retain"))
                    attr.prmtRetain.setNew(Boolean.parseBoolean(e.getStringValue()));
                if (name.equals("innate"))
                    attr.prmtInnate.setNew(Boolean.parseBoolean(e.getStringValue()));
                if (name.equals("ethereal"))
                    attr.prmtEthereal.setNew(Boolean.parseBoolean(e.getStringValue()));
                if (name.equals("purge"))
                    attr.prmtPurge.setNew(Boolean.parseBoolean(e.getStringValue()));
            }
        }
        sData.attributes.copyFrom(attr);
    }

    private boolean assignRarity(SData sData, @NotNull Element data) {
        if (data.element("rarity") == null) {
            sData.rarity = AbstractCard.CardRarity.SPECIAL;
            return false;
        }
        String rarity = data.elementText("rarity").toLowerCase().trim();
        switch (rarity) {
            case "basic":
                sData.rarity = AbstractCard.CardRarity.BASIC;
                return true;
            case "common":
                sData.rarity = AbstractCard.CardRarity.COMMON;
                return true;
            case "uncommon":
                sData.rarity = AbstractCard.CardRarity.UNCOMMON;
                return true;
            case "rare":
                sData.rarity = AbstractCard.CardRarity.RARE;
                return true;
            case "curse":
                sData.rarity = AbstractCard.CardRarity.CURSE;
                return true;
            case "special":
                sData.rarity = AbstractCard.CardRarity.SPECIAL;
                return true;
        }
        sData.rarity = LMCustomRarityMgr.GetCustomRarity(rarity);
        return sData.rarity != null;
    }

    private boolean assignTarget(SData sData, @NotNull Element data) {
        if (data.element("target") == null) {
            sData.target = AbstractCard.CardTarget.ENEMY;
            return false;
        }
        String target = data.elementText("target")
                .replace(" ", "")
                .replace("_", "")
                .toLowerCase()
                .trim();
        switch (target) {
            case "allenemy":
            case "allenemies":
                sData.target = AbstractCard.CardTarget.ALL_ENEMY;
                break;
            case "selfandenemy":
                sData.target = AbstractCard.CardTarget.SELF_AND_ENEMY;
                break;
            case "self":
                sData.target = AbstractCard.CardTarget.SELF;
                break;
            case "all":
                sData.target = AbstractCard.CardTarget.ALL;
                break;
            case "none":
                sData.target = AbstractCard.CardTarget.NONE;
                break;
            default:
                sData.target = AbstractCard.CardTarget.ENEMY;
                break;
        }
        return true;
    }

    private boolean assignType(SData sData, @NotNull Element data) {
        if (data.element("type") == null) {
            sData.type = AbstractCard.CardType.SKILL;
            return false;
        }
        String type = data.elementText("type").toLowerCase().trim();
        switch (type) {
            case "attack":
                sData.type = AbstractCard.CardType.ATTACK;
                break;
            case "power":
                sData.type = AbstractCard.CardType.POWER;
                break;
            case "status":
                sData.type = AbstractCard.CardType.STATUS;
                break;
            case "curse":
                sData.type = AbstractCard.CardType.CURSE;
                break;
            default:
                sData.type = AbstractCard.CardType.SKILL;
                break;
        }
        return true;
    }
    
    private boolean assignText(SData sData, @NotNull Element data) {
        if (data.element("text") == null) {
            sData.text = "missing default text";
            return false;
        }
        Element text = data.element("text");
        sData.text = text.attributeValue("base");
        boolean prmt = text.attributeValue("prmt") != null || !Boolean.parseBoolean(text.attributeValue("prmt"));
        sData.prmtText = prmt ? text.attributeValue("prmt") : sData.text;
        return true;
    }

    private boolean assignCost(SData sData, @NotNull Element data) {
        if (data.element("cost") == null) {
            sData.prmtNewCost = sData.baseCost = -2;
            return false;
        }
        Element cost = data.element("cost");
        sData.baseCost = Integer.parseInt(cost.attributeValue("base"));
        sData.prmtNewCost = cost.attribute("prmtNew") == null ? sData.baseCost : Integer.parseInt(cost.attributeValue("prmtNew"));
        return true;
    }

    private boolean assignMisc(SData sData, @NotNull Element data) {
        if (data.element("misc") == null)
            return false;
        Element misc = data.element("misc");
        sData.misc = Integer.parseInt(misc.attributeValue("value"));
        return true;
    }

    private boolean assignDamage(SData sData, @NotNull Element data) {
        if (data.element("damage") == null)
            return false;
        Element damage = data.element("damage");
        sData.baseDmg = Integer.parseInt(damage.attributeValue("base"));
        sData.prmtDmg = damage.attribute("prmt") == null ? 0 : Integer.parseInt(damage.attributeValue("prmt"));
        return true;
    }

    private boolean assignBlock(SData sData, @NotNull Element data) {
        if (data.element("block") == null)
            return false;
        Element block = data.element("block");
        sData.baseBlock = Integer.parseInt(block.attributeValue("base"));
        sData.prmtBlock = block.attribute("prmt") == null ? 0 : Integer.parseInt(block.attributeValue("prmt"));
        return true;
    }

    private boolean assignMagics(SData sData, @NotNull Element data) {
        if (data.element("magics") == null)
            return false;
        Element magics = data.element("magics");
        sData.baseMagics = Integer.parseInt(magics.attributeValue("base"));
        sData.prmtMagics = magics.attribute("prmt") == null ? 0 : Integer.parseInt(magics.attributeValue("prmt"));
        return true;
    }
    
    private void Log(Object what) {
        LMDebug.Log(this, what);
    }

    public class SData {
        private int sn;
        private String id;
        private int misc;
        private int baseDmg;
        private int prmtDmg;
        private int baseBlock;
        private int prmtBlock;
        private int baseMagics;
        private int prmtMagics;
        private int baseCost;
        private int prmtNewCost;
        private String text;
        private String prmtText;
        private AbstractCard.CardType type;
        private AbstractCard.CardTarget target;
        private AbstractCard.CardRarity rarity;
        private List<AbstractCard.CardTags> tags;
        private CardAttributes attributes;
        private Map<String, String> customs;

        public SData() {
            misc = 0;
            baseDmg = -1;
            prmtDmg = 0;
            baseBlock = -1;
            prmtBlock = 0;
            baseMagics = -1;
            prmtMagics = 0;
            baseCost = -2;
            prmtNewCost = -2;
            text = null;
            prmtText = null;
            attributes = new CardAttributes();
            customs = new HashMap<>();
        }

        public String getId() {
            return id;
        }

        public int getMisc() {
            return misc;
        }

        public int getBaseDmg() {
            return baseDmg;
        }

        public int getPrmtDmg() {
            return prmtDmg;
        }

        public int getBaseBlock() {
            return baseBlock;
        }

        public int getPrmtBlock() {
            return prmtBlock;
        }

        public int getBaseMagics() {
            return baseMagics;
        }

        public int getPrmtMagics() {
            return prmtMagics;
        }

        public int getBaseCost() {
            return baseCost;
        }

        public int getPrmtNewCost() {
            return prmtNewCost;
        }
        
        public String getText() {
            return text;
        }
        
        public String getPrmtText() {
            return prmtText;
        }

        public AbstractCard.CardType getType() {
            return type;
        }

        public AbstractCard.CardTarget getTarget() {
            return target;
        }

        public AbstractCard.CardRarity getRarity() {
            return rarity;
        }

        public List<AbstractCard.CardTags> getTags() {
            return tags;
        }

        public CardAttributes getAttributes() {
            return attributes;
        }

        public void putCustom(String key, String value) {
            putCustom(key, value, false);
        }

        public void putCustom(String key, String value, boolean forced) {
            if (!forced)
                customs.putIfAbsent(key, value);
            else if (customs.containsKey(key))
                customs.replace(key, value);
        }

        public String getCustom(String key) {
            if (customs.containsKey(key))
                return customs.get(key);
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SData)) return false;
            SData sData = (SData) o;
            return sn == sData.sn && id.equals(sData.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sn, id);
        }
    }

    public class CardAttributes {
        private boolean exhaust;
        private PrmtAttributes prmtExhaust;
        private boolean selfRetain;
        private PrmtAttributes prmtSelfRetain;
        private boolean retain;
        private PrmtAttributes prmtRetain;
        private boolean innate;
        private PrmtAttributes prmtInnate;
        private boolean ethereal;
        private PrmtAttributes prmtEthereal;
        private boolean purge;
        private PrmtAttributes prmtPurge;

        public CardAttributes() {
            exhaust = false;
            prmtExhaust = new PrmtAttributes(false, false);
            selfRetain = false;
            prmtSelfRetain = new PrmtAttributes(false, false);
            retain = false;
            prmtRetain = new PrmtAttributes(false, false);
            innate = false;
            prmtInnate = new PrmtAttributes(false, false);
            ethereal = false;
            prmtEthereal = new PrmtAttributes(false, false);
            purge = false;
            prmtPurge = new PrmtAttributes(false, false);
        }

        private void copyFrom(CardAttributes attr) {
            exhaust = attr.exhaust;
            prmtExhaust = attr.prmtExhaust;
            selfRetain = attr.selfRetain;
            prmtSelfRetain = attr.prmtSelfRetain;
            retain = attr.retain;
            prmtRetain = attr.prmtRetain;
            innate = attr.innate;
            prmtInnate = attr.prmtInnate;
            ethereal = attr.ethereal;
            prmtEthereal = attr.prmtEthereal;
            purge = attr.purge;
            prmtPurge = attr.prmtPurge;
        }

        public boolean isExhaust() {
            return exhaust;
        }

        public boolean isPrmtExhaust() {
            return prmtExhaust.value;
        }

        public boolean isExhautedChanged() {
            return prmtExhaust.changed;
        }

        public boolean isSelfRetain() {
            return selfRetain;
        }

        public boolean isPrmtSelfRetain() {
            return prmtSelfRetain.value;
        }

        public boolean isSelfRetainChanged() {
            return prmtSelfRetain.changed;
        }

        public boolean isRetain() {
            return retain;
        }

        public boolean isPrmtRetain() {
            return prmtRetain.value;
        }

        public boolean isRetainChanged() {
            return prmtRetain.changed;
        }

        public boolean isInnate() {
            return innate;
        }

        public boolean isPrmtInnate() {
            return prmtInnate.value;
        }

        public boolean isInnateChanged() {
            return prmtInnate.changed;
        }

        public boolean isEthereal() {
            return ethereal;
        }

        public boolean isPrmtEthereal() {
            return prmtEthereal.value;
        }

        public boolean isEtherealChanged() {
            return prmtEthereal.changed;
        }

        public boolean isPurge() {
            return purge;
        }

        public boolean isPrmtPurge() {
            return prmtPurge.value;
        }

        public boolean isPurgeChanged() {
            return prmtPurge.changed;
        }
    }

    public class PrmtAttributes {
        private boolean changed;
        private boolean value;

        public PrmtAttributes(boolean changed, boolean value) {
            this.changed = changed;
            this.value = value;
        }

        public PrmtAttributes set(boolean value) {
            this.changed = false;
            this.value = value;
            return this;
        }

        public PrmtAttributes setNew(boolean value) {
            this.changed = true;
            this.value = value;
            return this;
        }
    }
}