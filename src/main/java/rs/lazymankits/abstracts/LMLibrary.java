package rs.lazymankits.abstracts;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.cards.LMLibraryChoiceCard;
import rs.lazymankits.cards.LMLibraryCostCard;
import rs.lazymankits.interfaces.cards.LibraryChoiceCard;
import rs.lazymankits.interfaces.cards.LibraryTokenCard;
import rs.lazymankits.utils.LMGameGeneralUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LMLibrary implements LMGameGeneralUtils {
    public static final int END_SELECTION = 0;
    
    public final int ID;
    List<AbstractCard> options;
    List<AbstractCard> extras;
    List<AbstractCard> bannedList;
    Comparator<AbstractCard> comparator;
    boolean useCustomizedChoices;
    
    public LMLibrary(int ID) {
        this.ID = ID;
        options = new ArrayList<>();
        extras = new ArrayList<>();
        bannedList = new ArrayList<>();
        initOptions(options);
        comparator = null;
        useCustomizedChoices = false;
    }
    
    protected abstract void initOptions(List<AbstractCard> opts);
    
    protected void addExtraOpt(@NotNull AbstractCard... cards) {
        for (AbstractCard c : cards)
            if (!extras.contains(c)) extras.add(c);
    }
    
    /**
     * Returns a list of available choices for player to choose
     * @param num the requested number of choices
     * @param times how many times is player choosing
     * @param flags any flags that are passed
     * @return the list of available choices or customized choices if {@code useCustomizedChoices} is set true
     * @apiNote Do not override this method but #returnCustomChoices(int num, int times, int flags) to customize choices
     * @see #returnCustomChoices(int, int, int...)
     */
    public List<AbstractCard> returnChoices(int num, int times, int... flags) {
        if (!useCustomizedChoices) {
            List<AbstractCard> tmp = getAllOptions();
            List<AbstractCard> choices = new ArrayList<>();
            int count = Math.min(num, tmp.size());
            while (choices.size() < count) {
                Optional<AbstractCard> opt = getRandom(tmp, cardRandomRng());
                opt.ifPresent(c -> {
                    choices.add(c);
                    tmp.remove(c);
                });
            }
            if (arrayContainsInt(flags, END_SELECTION)) {
                bannedList.clear();
            } else if (count > num) {
                bannedList.addAll(choices);
            } else {
                LMDebug.deLog(this, "Lacking of available choices: ");
                Log("Asked for " + num + " and only " + count + " available choices left");
            }
        }
        return returnCustomChoices(num, times, flags);
    }
    
    /**
     * Returns a list of customized choices for player to see when {@code useCustomizedChoices} is set true
     * @param num the requested number of choices
     * @param times how many times is player choosing
     * @param flags any flags that are passed
     * @return the list of customized choices
     */
    public List<AbstractCard> returnCustomChoices(int num, int times, int... flags) {
        return new ArrayList<>();
    }
    
    public List<AbstractCard> getAllOptions() {
        List<AbstractCard> tmp = new ArrayList<>();
        tmp.addAll(options);
        tmp.addAll(extras);
        tmp.removeIf(c -> bannedList.contains(c));
        Collections.shuffle(tmp, new Random(cardRandomRng().randomLong()));
        if (comparator != null) tmp.sort(comparator);
        return tmp;
    }
    
    public void clearExtraOptions() {
        extras.clear();
    }
    
    public LMLibrary sortBy(Comparator<AbstractCard> comparator) {
        this.comparator = comparator;
        return this;
    }
    
    public LMLibrary removeSorter() {
        comparator = null;
        return this;
    }
    
    protected static AbstractCard CreateOption(String name, String img, String description, AbstractCard.CardType type,
                                               AbstractCard.CardColor color, AbstractCard.CardRarity rarity, 
                                               AbstractCard.CardTarget target) {
        LMLibraryChoiceCard opt = new LMLibraryChoiceCard(name, img, -2, description, type, color, rarity, target);
        return opt;
    }
    
    protected static AbstractCard CreateOption(String name, String description, ChoiceDef def) {
        LMLibraryChoiceCard opt = new LMLibraryChoiceCard(name, -2, description, def);
        return opt;
    }
    
    protected static AbstractCard createCostOption(String name, String img, int cost, AbstractCard.CardColor color, 
                                    AbstractCard.CardRarity rarity) {
        LMLibraryCostCard opt = new LMLibraryCostCard(name, img, cost, color, rarity);
        return opt;
    }
    
    protected static AbstractCard createCostOption(String name, String img, int cost, AbstractCard.CardColor color) {
        return createCostOption(name, img, cost, color, AbstractCard.CardRarity.SPECIAL);
    }
    
    protected void Log(Object what) {
        LMDebug.Log(this, what);
    }
    
    public static LibraryCard RawCard(String ID, String name, String img, int cost, AbstractCard.CardType type,
                                      AbstractCard.CardColor color, AbstractCard.CardRarity rarity) {
        return new LibraryCard(ID, name, img, cost, type, color, rarity);
    }
    
    public static LibraryCard RawCard(String name, String img, int cost, AbstractCard.CardType type,
                                      AbstractCard.CardColor color, AbstractCard.CardRarity rarity) {
        return new LibraryCard(LibraryCard.DEFAULT_TOKEN_ID, name, img, cost, type, color, rarity);
    }
    
    public static LibraryCard RawCard(String name, String img, int cost, AbstractCard.CardType type,
                                      AbstractCard.CardRarity rarity) {
        return new LibraryCard(LibraryCard.DEFAULT_TOKEN_ID, name, img, cost, type, AbstractCard.CardColor.COLORLESS, rarity);
    }
    
    public static LibraryCard RawCard(String name, String img, int cost, AbstractCard.CardRarity rarity) {
        return new LibraryCard(LibraryCard.DEFAULT_TOKEN_ID, name, img, cost, AbstractCard.CardType.SKILL, 
                AbstractCard.CardColor.COLORLESS, rarity);
    }
    
    public static LibraryCard RawCard(String name, String img, int cost, AbstractCard.CardType type) {
        return new LibraryCard(LibraryCard.DEFAULT_TOKEN_ID, name, img, cost, type, AbstractCard.CardColor.COLORLESS, 
                AbstractCard.CardRarity.SPECIAL);
    }
    
    public static LibraryCard RawCard(String name, String img, int cost) {
        return new LibraryCard(LibraryCard.DEFAULT_TOKEN_ID, name, img, cost, AbstractCard.CardType.SKILL, AbstractCard.CardColor.COLORLESS,
                AbstractCard.CardRarity.SPECIAL);
    }
    
    public static class LibraryCard extends LMCustomCard implements LibraryTokenCard {
        public static final String DEFAULT_TOKEN_ID = "LM_LIB_TMP";
        private static int AutoID = 1;
        List<AbstractCard> choices;
        
        private LibraryCard(String ID, String name, String img, int cost, CardType type, CardColor color, CardRarity rarity) {
            super(ID, name, img, cost, "RAW", type, color, rarity, CardTarget.ENEMY);
            choices = new ArrayList<>();
        }
        
        private LibraryCard() {
            this(DEFAULT_TOKEN_ID, "LIB_TMP", "SharedAssets/images/cards/wild.png", -2, 
                    CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL);
        }
    
        @Override
        public void build() {
            LibraryTokenCard.super.build();
            if (cardID.equals(DEFAULT_TOKEN_ID)) {
                cardID += "#" + AutoID;
                AutoID++;
            }
        }
    
        @Override
        public void accept(AbstractCard choice) {
            if (choice instanceof LibraryChoiceCard) {
                ((LibraryChoiceCard) choice).apply(this);
                if (((LibraryChoiceCard) choice).practical())
                    choices.add(choice);
                return;
            }
            choices.add(choice);
        }
    
        @Override
        public List<AbstractCard> choices() {
            return choices;
        }
    
        @Override
        public void updateModdedDescription() {
            keywords.clear();
            description.clear();
            for (AbstractCard choice : choices) {
                keywords.addAll(choice.keywords);
                for (int i = 0; i < choice.description.size(); i++) {
                    adjustDescriptions(choice.description.get(i), choice);
                }
                description.addAll(choice.description);
            }
        }
        
        private void adjustDescriptions(DescriptionLine desc, AbstractCard choice) {
            String[] array = splitText(desc);
            if (array != null) {
                Arrays.stream(array).forEach(s -> {
                    int count = 0;
                    if (dynamicVarMatch(s, choice))
                        count++;
                    LMDebug.Log(this, count + " dynamic vars modified in " + choice.name);
                });
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < array.length; i++) {
                    builder.append(array[i]);
                }
                String result = builder.toString();
                if (!result.isEmpty()) {
                    desc.text = result;
                }
            }
        }
        
        private boolean dynamicVarMatch(@NotNull String s, AbstractCard choice) {
            String origin = s.toLowerCase(Locale.ROOT);
            switch (s) {
                case "!D!":
                    s = colorDynamicVar(String.valueOf(choice.damage), choice.damage - choice.baseDamage);
                    break;
                case "!M!":
                    s = colorDynamicVar(String.valueOf(choice.magicNumber), choice.magicNumber - choice.baseMagicNumber);
                    break;
                case "!B!":
                    s = colorDynamicVar(String.valueOf(choice.block), choice.block - choice.baseBlock);
                    break;
                default:
            }
            String end = "";
            String regex = Settings.lineBreakViaCharacter ? "\\$(.+)\\$\\$" : "!(.+)!(.*) ";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                s = matcher.group(1);
                if (!Settings.lineBreakViaCharacter)
                    end = matcher.group(2);
            }
            DynamicVariable dv = BaseMod.cardDynamicVariableMap.get(s);
            if (dv != null) {
                s = colorDynamicVar(String.valueOf(dv.value(choice)), dv.value(choice) - dv.baseValue(choice));
                if (!end.isEmpty()) s += end;
            }
            return !s.toLowerCase(Locale.ROOT).equals(origin);
        }
    
        /**
         * Returns a colorized string of the dynamic variable value.
         * @param var the value of the variable
         * @param status if the variable is increased or decreased
         * @apiNote if status: ==0 -- not modified, > 0 -- increased, < 0 -- decreased
         * @return the colorized string of colorized dynamic variable value.
         */
        private String colorDynamicVar(String var, int status) {
            if (status > 0) {
                return "[#" + Settings.GREEN_TEXT_COLOR + "]" + var + "[]";
            } else if (status < 0) {
                return "[#" + Settings.RED_TEXT_COLOR + "]" + var + "[]";
            } else {
                return var;
            }
        }
        
        private String[] splitText(DescriptionLine desc) {
            if (Settings.lineBreakViaCharacter) {
                return desc.getCachedTokenizedTextCN();
            }
            return desc.getCachedTokenizedText();
        }
    
        @Override
        public void upgrade() {}
    
        @Override
        public boolean canUpgrade() {
            return false;
        }
    
        @Override
        public void use(AbstractPlayer p, AbstractMonster m) {
            LibraryTokenCard.super.use(p, m);
        }
    }
    
    public static ChoiceDef CreateDef(String img) {
        return new ChoiceDef().setImg(img);
    }
    
    public static class ChoiceDef {
        String img;
        AbstractCard.CardType type;
        AbstractCard.CardColor color;
        AbstractCard.CardRarity rarity;
        AbstractCard.CardTarget target;
    
        private ChoiceDef(String img, AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, 
                         AbstractCard.CardTarget target) {
            this.img = img;
            this.type = type;
            this.color = color;
            this.rarity = rarity;
            this.target = target;
        }
    
        private ChoiceDef() {
            type = AbstractCard.CardType.SKILL;
            color = AbstractCard.CardColor.COLORLESS;
            rarity = AbstractCard.CardRarity.SPECIAL;
            target = AbstractCard.CardTarget.ENEMY;
        }
    
        public String getImg() {
            return img;
        }
    
        public ChoiceDef setImg(String img) {
            this.img = img;
            return this;
        }
    
        public AbstractCard.CardType getType() {
            return type;
        }
    
        public ChoiceDef setType(AbstractCard.CardType type) {
            this.type = type;
            return this;
        }
    
        public AbstractCard.CardColor getColor() {
            return color;
        }
    
        public ChoiceDef setColor(AbstractCard.CardColor color) {
            this.color = color;
            return this;
        }
    
        public AbstractCard.CardRarity getRarity() {
            return rarity;
        }
    
        public ChoiceDef setRarity(AbstractCard.CardRarity rarity) {
            this.rarity = rarity;
            return this;
        }
    
        public AbstractCard.CardTarget getTarget() {
            return target;
        }
    
        public ChoiceDef setTarget(AbstractCard.CardTarget target) {
            this.target = target;
            return this;
        }
    }
}