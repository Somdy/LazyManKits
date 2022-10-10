package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.LManager;
import rs.lazymankits.interfaces.cards.ModifyBlockModifier;
import rs.lazymankits.interfaces.utilities.AtDamageGiveModifier;
import rs.lazymankits.interfaces.utilities.AtDamageReceiveModifier;

import java.util.ArrayList;

public class LMSubscriber {
    private static ArrayList<LMSubscriberInterface> toRemove;
    private static ArrayList<OnInitializeSubscriber> onInitializeSubscribers;
    private static ArrayList<EndTurnPreDiscardSubscriber> endTurnPreDiscardSubscribers;
    private static ArrayList<MonsterEndTurnSubscriber> monsterEndTurnSubscribers;
    private static ArrayList<OnCardPlayedSubscriber> onCardPlayedSubscribers;
    private static ArrayList<OnPlayCardSubscriber> onPlayCardSubscribers;
    private static ArrayList<TurnStartSubscriber> turnStartSubscribers;
    private static ArrayList<OnGainBlockSubscriber> onGainBlockSubscribers;
    private static ArrayList<OnAttackdSubscriber> onAttackdSubscribers;
    private static ArrayList<OnShuffleSubscriber> onShuffleSubscribers;
    private static ArrayList<OnMakingCardInCombatSubscriber> onMakingCardInCombatSubscribers;
    
    private static ArrayList<AtDamageGiveModifier> atDamageGiveModifiers;
    private static ArrayList<AtDamageReceiveModifier> atDamageReceiveModifiers;
    private static ArrayList<ModifyBlockModifier> modifyBlockModifiers;
    
    private static ArrayList<LMXCardDataSubscriber> lmxCardDataSubscribers;
    private static ArrayList<RegisterCardAttrsSubscriber> registerCardAttrsSubscribers;
    
    public static void Initialize() {
        if (LManager.EverythingReady()) return;
        toRemove = new ArrayList<>();
        onInitializeSubscribers = new ArrayList<>();
        endTurnPreDiscardSubscribers = new ArrayList<>();
        monsterEndTurnSubscribers = new ArrayList<>();
        onCardPlayedSubscribers = new ArrayList<>();
        onPlayCardSubscribers = new ArrayList<>();
        turnStartSubscribers = new ArrayList<>();
        onGainBlockSubscribers = new ArrayList<>();
        onAttackdSubscribers = new ArrayList<>();
        onShuffleSubscribers = new ArrayList<>();
        onMakingCardInCombatSubscribers = new ArrayList<>();
        
        atDamageGiveModifiers = new ArrayList<>();
        atDamageReceiveModifiers = new ArrayList<>();
        modifyBlockModifiers = new ArrayList<>();
        
        lmxCardDataSubscribers = new ArrayList<>();
        registerCardAttrsSubscribers = new ArrayList<>();
    }
    
    public static void Sub(LMSubscriberInterface sub) {
        subIfInstance(onInitializeSubscribers, sub, OnInitializeSubscriber.class);
        subIfInstance(endTurnPreDiscardSubscribers, sub, EndTurnPreDiscardSubscriber.class);
        subIfInstance(monsterEndTurnSubscribers, sub, MonsterEndTurnSubscriber.class);
        subIfInstance(onCardPlayedSubscribers, sub, OnCardPlayedSubscriber.class);
        subIfInstance(onPlayCardSubscribers, sub, OnPlayCardSubscriber.class);
        subIfInstance(turnStartSubscribers, sub, TurnStartSubscriber.class);
        subIfInstance(onGainBlockSubscribers, sub, OnGainBlockSubscriber.class);
        subIfInstance(onAttackdSubscribers, sub, OnAttackdSubscriber.class);
        subIfInstance(onShuffleSubscribers, sub, OnShuffleSubscriber.class);
        subIfInstance(onMakingCardInCombatSubscribers, sub, OnMakingCardInCombatSubscriber.class);
        
        subIfInstance(atDamageGiveModifiers, sub, AtDamageGiveModifier.class);
        subIfInstance(atDamageReceiveModifiers, sub, AtDamageReceiveModifier.class);
        subIfInstance(modifyBlockModifiers, sub, ModifyBlockModifier.class);
        
        subIfInstance(lmxCardDataSubscribers, sub, LMXCardDataSubscriber.class);
        subIfInstance(registerCardAttrsSubscribers, sub, RegisterCardAttrsSubscriber.class);
    }
    
    public static void Unsub(LMSubscriberInterface sub) {
        unsubIfInstance(onInitializeSubscribers, sub, OnInitializeSubscriber.class);
        unsubIfInstance(endTurnPreDiscardSubscribers, sub, EndTurnPreDiscardSubscriber.class);
        unsubIfInstance(monsterEndTurnSubscribers, sub, MonsterEndTurnSubscriber.class);
        unsubIfInstance(onCardPlayedSubscribers, sub, OnCardPlayedSubscriber.class);
        unsubIfInstance(onPlayCardSubscribers, sub, OnPlayCardSubscriber.class);
        unsubIfInstance(turnStartSubscribers, sub, TurnStartSubscriber.class);
        unsubIfInstance(onGainBlockSubscribers, sub, OnGainBlockSubscriber.class);
        unsubIfInstance(onAttackdSubscribers, sub, OnAttackdSubscriber.class);
        unsubIfInstance(onShuffleSubscribers, sub, OnShuffleSubscriber.class);
        unsubIfInstance(onMakingCardInCombatSubscribers, sub, OnMakingCardInCombatSubscriber.class);

        unsubIfInstance(atDamageGiveModifiers, sub, AtDamageGiveModifier.class);
        unsubIfInstance(atDamageReceiveModifiers, sub, AtDamageReceiveModifier.class);
        unsubIfInstance(modifyBlockModifiers, sub, ModifyBlockModifier.class);

        unsubIfInstance(lmxCardDataSubscribers, sub, LMXCardDataSubscriber.class);
        unsubIfInstance(registerCardAttrsSubscribers, sub, RegisterCardAttrsSubscriber.class);
    }
    
    private static <T> void subIfInstance(ArrayList<T> list, LMSubscriberInterface sub, @NotNull Class<T> clazz) {
        if (clazz.isInstance(sub))
            list.add(clazz.cast(sub));
    }

    private static <T> void unsubIfInstance(ArrayList<T> list, LMSubscriberInterface sub, @NotNull Class<T> clazz) {
        if (clazz.isInstance(sub))
            list.remove(clazz.cast(sub));
    }
    
    public static void PublishOnMakingCard(@NotNull AbstractCard card, CardGroup destination) {
        Log("publish on making card: " + card.name + (destination != null ? " to " + destination.type.name() : ""));
        for (OnMakingCardInCombatSubscriber sub : onMakingCardInCombatSubscribers) {
            sub.receiveOnMakingCardInCombat(card, destination);
        }
    }
    
    public static void PublishOnShuffle() {
        Log("publish on shuffle");
        for (OnShuffleSubscriber sub : onShuffleSubscribers) {
            sub.receiveOnShuffle();
        }
    }
    
    public static void PublishOnInitialize() {
        Log("publish on initializing");
        for (OnInitializeSubscriber sub : onInitializeSubscribers) {
            sub.receiveOnInitialize();
        }
    }

    public static void PublishEndTurnPreDiscard() {
        Log("publish pre discarding");
        for (EndTurnPreDiscardSubscriber sub : endTurnPreDiscardSubscribers) {
            sub.receiveOnEndTurnPreDiscard();
        }
    }

    public static void PublishMonsterTurnEnds(@NotNull AbstractMonster m) {
        deLog("publish ending turn of " + m.name);
        for (MonsterEndTurnSubscriber sub : monsterEndTurnSubscribers) {
            sub.receiveOnMonsterTurnEnds(m);
        }
    }

    public static void PublishCardPlayed(AbstractCard card, UseCardAction action) {
        //Log("publish " + card.name + " used.");
        for (OnCardPlayedSubscriber sub : onCardPlayedSubscribers) {
            sub.receiveOnCardPlayed(card, action);
        }
    }

    public static void PublishPlayingCard(@NotNull AbstractCard card, AbstractCreature target, int energyOnUse) {
        Log("publish playing " + card.name);
        for (OnPlayCardSubscriber sub : onPlayCardSubscribers) {
            sub.receiveOnPlayCard(card, target, energyOnUse);
        }
    }

    public static void PublishPostTurnStart(AbstractCreature creature, boolean postDraw) {
        //Log("publish post turn start, is player? " + creature.isPlayer);
        for (TurnStartSubscriber sub : turnStartSubscribers) {
            sub.receiveOnTurnStarts(creature, postDraw);
        }
    }
    
    public static float PublishOnGainBlock(AbstractCreature who, float amount) {
        float block = amount;
        for (OnGainBlockSubscriber sub : onGainBlockSubscribers) {
            block = sub.receiveOnGainBlock(who, amount);
        }
        return block;
    }
    
    public static void PublishOnReadingLMXData() {
        for (LMXCardDataSubscriber sub : lmxCardDataSubscribers) {
            sub.receiveOnReadingData();
        }
    }
    
    public static void PublishOnRegisteringAttrs() {
        for (RegisterCardAttrsSubscriber sub : registerCardAttrsSubscribers) {
            sub.receiveOnRegisteringCardAttrs();
        }
    }

    public static void PublishOnAttacked(int damage, DamageInfo info, AbstractCreature target) {
        for (OnAttackdSubscriber sub : onAttackdSubscribers) {
            sub.onAttackd(damage, info, target);
        }
    }
    
    /*public static float atDamageGive(float damage, DamageInfo.DamageType type) {
        for (AtDamageGiveModifier mod : atDamageGiveModifiers) {
            damage = mod.atDamageGive(damage, type);
        }
        return damage;
    }*/

    public static float AtDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        for (AtDamageGiveModifier mod : atDamageGiveModifiers) {
            damage = mod.atDamageGive(damage, type, card);
        }
        return damage;
    }

    public static float AtDamageGive(float damage, DamageInfo.DamageType type, AbstractCreature[] crts) {
        for (AtDamageGiveModifier mod : atDamageGiveModifiers) {
            damage = mod.atDamageGive(damage, type, crts[0], crts[1]);
        }
        return damage;
    }
    
    /*public static float atDamageReceive(float damage, DamageInfo.DamageType type) {
        for (AtDamageReceiveModifier mod : atDamageReceiveModifiers) {
            damage = mod.atDamageReceive(damage, type);
        }
        return damage;
    }

    public static float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        for (AtDamageReceiveModifier mod : atDamageReceiveModifiers) {
            damage = mod.atDamageReceive(damage, type, card);
        }
        return damage;
    }*/

    public static float AtDamageReceive(float damage, DamageInfo.DamageType type, AbstractCreature[] crts) {
        for (AtDamageReceiveModifier mod : atDamageReceiveModifiers) {
            damage = mod.atDamageReceive(damage, type, crts[0], crts[1]);
        }
        return damage;
    }

    public static float AtDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractCreature target) {
        for (AtDamageReceiveModifier mod : atDamageReceiveModifiers) {
            damage = mod.atDamageReceive(damage, type, card, target);
        }
        return damage;
    }
    
    public static float ModifyBlock(float blockamt, AbstractCard card) {
        for (ModifyBlockModifier mod : modifyBlockModifiers) {
            blockamt = mod.modifyBlock(blockamt, card);
        }
        return blockamt;
    }

    private static void deLog(Object what) {
        LMDebug.deLog(LMSubscriber.class, what);
    }

    private static void Log(Object what) {
        LMDebug.Log(LMSubscriber.class, what);
    }
}