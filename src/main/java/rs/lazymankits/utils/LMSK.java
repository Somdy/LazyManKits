package rs.lazymankits.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.annotations.Encapsulated;

import java.util.*;
import java.util.function.Predicate;

public class LMSK {

    @Encapsulated
    public static <T> Optional<T> GetRandom(@NotNull List<T> list, Random rng) {
        if (list.isEmpty())
            return Optional.empty();
        if (list.size() == 1)
            return Optional.ofNullable(list.get(0));
        int index = rng.random(list.size() - 1);
        return Optional.ofNullable(list.get(index));
    }

    @Encapsulated
    public static <T> Optional<T> GetRandom(@NotNull List<T> list) {
        if (list.isEmpty())
            return Optional.empty();
        if (list.size() == 1)
            return Optional.ofNullable(list.get(0));
        int index = MathUtils.random(list.size() - 1);
        return Optional.ofNullable(list.get(index));
    }
    
    @NotNull
    @Deprecated
    public static <T> ArrayList<T> listFromObjs(@NotNull T... objs) {
        return ListFromObjs(objs);
    }

    @NotNull
    @Encapsulated
    public static <T> ArrayList<T> ListFromObjs(@NotNull T... objs) {
        ArrayList<T> list = new ArrayList<>();
        if (Arrays.stream(objs).noneMatch(Objects::nonNull))
            return list;
        for (T obj : objs) {
            if (obj != null && !list.contains(obj))
                list.add(obj);
        }
        return list;
    }

    @NotNull
    @Encapsulated
    public static <T> ArrayList<T> ListFromRepeatableObjs(@NotNull T... objs) {
        ArrayList<T> list = new ArrayList<>();
        if (Arrays.stream(objs).noneMatch(Objects::nonNull))
            return list;
        for (T obj : objs) {
            if (obj != null)
                list.add(obj);
        }
        return list;
    }
    
    public static boolean HasAnyExptCreatures(Predicate<AbstractCreature> expt) {
        List<AbstractCreature> tmp = new ArrayList<>();
        tmp.add(Player());
        tmp.addAll(GetAllExptMstr(m -> true));
        return tmp.stream().anyMatch(expt);
    }

    public static boolean HasAnyExptMonster(Predicate<AbstractMonster> expt) {
        return AbstractDungeon.getMonsters().monsters.stream().anyMatch(m -> !m.isDeadOrEscaped() && expt.test(m));
    }
    
    public static List<AbstractCreature> GetAllExptCreatures(Predicate<AbstractCreature> expt) {
        List<AbstractCreature> tmp = new ArrayList<>(AbstractDungeon.getMonsters().monsters);
        tmp.add(AbstractDungeon.player);
        tmp.removeIf(c -> !expt.test(c));
        return tmp;
    }
    
    @NotNull
    public static List<AbstractMonster> GetAllExptMonsters(Predicate<AbstractMonster> expt) {
        List<AbstractMonster> tmp = new ArrayList<>(AbstractDungeon.getMonsters().monsters);
        tmp.removeIf(m -> !expt.test(m));
        return tmp;
    }

    @NotNull
    public static List<AbstractMonster> GetAllExptMstr(Predicate<AbstractMonster> expt) {
        List<AbstractMonster> tmp = new ArrayList<>();
        for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
            if (!m.isDeadOrEscaped() && expt.test(m))
                tmp.add(m);
        }
        return tmp;
    }

    @NotNull
    public static Optional<AbstractMonster> GetExptMstr(Predicate<AbstractMonster> expt) {
        List<AbstractMonster> monsters = new ArrayList<>(AbstractDungeon.getMonsters().monsters);
        monsters.removeIf(m -> !expt.test(m));
        return GetRandom(monsters);
        //return AbstractDungeon.getMonsters().monsters.parallelStream().filter(m -> !m.isDeadOrEscaped() && expt.test(m)).findAny();
    }

    @NotNull
    @SafeVarargs
    public static <T extends AbstractCard> List<T> GetAllExptCards(Predicate<T> expt, @NotNull List<T>... lists) {
        List<T> cards = new ArrayList<>();
        for (List<T> list : lists) cards.addAll(list);
        cards.removeIf(c -> !expt.test(c));
        return cards;
    }

    @NotNull
    @SafeVarargs
    public static <T extends AbstractCard> Optional<T> GetExptCard(Predicate<T> expt, @NotNull List<T>... lists) {
        List<T> cards = new ArrayList<>();
        for (List<T> list : lists) cards.addAll(list);
        cards.removeIf(c -> !expt.test(c));
        return GetRandom(cards);
    }

    @NotNull
    @SafeVarargs
    public static List<AbstractCard> GetALLUnexhaustedCards(List<? extends AbstractCard>... included) {
        List<AbstractCard> tmp = new ArrayList<AbstractCard>() {{
            addAll(Player().drawPile.group);
            addAll(Player().hand.group);
            addAll(Player().discardPile.group);
        }};
        if (included != null) {
            for (List<? extends AbstractCard> cards : included) tmp.addAll(cards);
        }
        return tmp;
    }
    
    public static Optional<AbstractCard> ReturnTrulyRndCardInCombat(Predicate<AbstractCard> expt) {
        List<AbstractCard> tmp = new ArrayList<>();
        AbstractDungeon.srcCommonCardPool.group.stream()
                .filter(c -> expt.test(c) && UnlockTracker.isCardSeen(c.cardID))
                .forEach(tmp::add);
        AbstractDungeon.srcUncommonCardPool.group.stream()
                .filter(c -> expt.test(c) && UnlockTracker.isCardSeen(c.cardID))
                .forEach(tmp::add);
        AbstractDungeon.srcRareCardPool.group.stream()
                .filter(c -> expt.test(c) && UnlockTracker.isCardSeen(c.cardID))
                .forEach(tmp::add);
        AbstractDungeon.srcColorlessCardPool.group.stream()
                .filter(c -> expt.test(c) && UnlockTracker.isCardSeen(c.cardID))
                .forEach(tmp::add);
        return LMSK.GetRandom(tmp, LMSK.CardRandomRng());
    }

    public static Optional<AbstractCard> ReturnTrulyRndCardInCombat() {
        return ReturnTrulyRndCardInCombat(c -> true);
    }

    @Encapsulated
    public static int AscnLv() {
        return AbstractDungeon.ascensionLevel;
    }

    @Encapsulated
    public static void AddToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }

    @Encapsulated
    public static void AddToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    @Encapsulated
    public static boolean RandomBool(@NotNull Random rng, float chance) {
        return rng.randomBoolean(chance);
    }

    @Encapsulated
    public static boolean RandomBool(@NotNull Random rng) {
        return rng.randomBoolean();
    }

    @Encapsulated
    public static Random MonsterRng() {
        return AbstractDungeon.monsterRng;
    }

    @Encapsulated
    public static Random CardRandomRng() {
        return AbstractDungeon.cardRandomRng;
    }

    @Encapsulated
    public static Random RelicRng() {
        return AbstractDungeon.relicRng;
    }

    @Encapsulated
    public static Random MiscRng() {
        return AbstractDungeon.miscRng;
    }

    @Encapsulated
    public static Random TreasureRng() {
        return AbstractDungeon.treasureRng;
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Color Color(int r, int g, int b) {
        return Color(r, g, b, 1);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Color Color(int r, int g, int b, int a) {
        return Color(r, g, b, a * 1F);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Color Color(int r, int g, int b, float a) {
        return Color(r * 1F, g * 1F, b * 1F, a);
    }
    
    @NotNull
    @Contract("_, _, _, _ -> new")
    public static Color Color(float r, float g, float b, float a) {
        return new Color(r / 255F, g / 255F, b / 255F, a);
    }

    @Encapsulated
    public static AbstractPlayer Player() {
        return AbstractDungeon.player;
    }
}