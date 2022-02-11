package rs.lazymankits;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import rs.lazymankits.abstracts.LMDynamicVar;
import rs.lazymankits.annotations.Encapsulated;
import rs.lazymankits.cards.TestCard;
import rs.lazymankits.cards.TestCard2;
import rs.lazymankits.data.LMXCardDataReader;
import rs.lazymankits.interfaces.LMSubscriber;
import rs.lazymankits.interfaces.LMSubscriberInterface;
import rs.lazymankits.listeners.ApplyPowerListener;
import rs.lazymankits.listeners.DrawCardListener;
import rs.lazymankits.listeners.TurnEventListener;
import rs.lazymankits.listeners.UseCardListener;
import rs.lazymankits.managers.LMDynVarMgr;
import rs.lazymankits.utils.LMGameFps;
import rs.lazymankits.utils.LMGameGeneralUtils;
import rs.lazymankits.utils.LMKeyword;

import java.util.Map;

@SpireInitializer
public class LManager implements LMGameGeneralUtils, OnStartBattleSubscriber, PostBattleSubscriber, PostUpdateSubscriber, 
        EditKeywordsSubscriber, EditCardsSubscriber, EditStringsSubscriber {
    private static boolean EverythingInitialized;
    private static String modid = "lmkmod:";
    
    public static boolean PostTurnStartDraw;
    
    public static LMGameFps gameFps;
    
    public static void initialize() {
        EverythingInitialized = false;
        
        LMSubscriber.Initialize();
        LMXCardDataReader.Initialize();
        
        LMDynVarMgr.Initialize();
        
        UseCardListener.Initialize();
        DrawCardListener.Initialize();
        TurnEventListener.Initialize();
        ApplyPowerListener.Initialize();
        
        PostTurnStartDraw = true;
        
        new LManager();
        EverythingInitialized = true;
    }
    
    public LManager() {
        BaseMod.subscribe(this);
    }
    
    public static boolean EverythingReady() {
        return EverythingInitialized;
    }
    
    @Encapsulated
    public static void Sub(LMSubscriberInterface sub) {
        LMSubscriber.Sub(sub);
    }
    
    @Encapsulated
    public static void Unsub(LMSubscriberInterface sub) {
        LMSubscriber.Unsub(sub);
    }
    
    public static boolean ReadLMXCardData(Class<?> clazz, String path, String id, String uniqueID) {
        return LMXCardDataReader.RegisterLMXData(clazz, path, id, uniqueID);
    }

    public static boolean ReadLMXCardData(Class<?> clazz, String path, String setName, String id, String uniqueID) {
        return LMXCardDataReader.RegisterLMXData(clazz, path, setName, id, uniqueID);
    }

    public static boolean ReadLMXCardDatas(Class<?> clazz, String dirPath, String setName, String id, String uniqueID, String... fileNames) {
        return LMXCardDataReader.RegisterLMXDatas(clazz, dirPath, setName, id, uniqueID, fileNames);
    }

    public static boolean ReadLMXCardDatas(Class<?> clazz, String dirPath, String id, String uniqueID, String... fileNames) {
        return LMXCardDataReader.RegisterLMXDatas(clazz, dirPath, id, uniqueID, fileNames);
    }
    
    @Encapsulated
    public static void AddLMDynamicVar(LMDynamicVar var) {
        LMDynVarMgr.AddDynamicVar(var);
    }

    public static void ReceiveOnTurnStart(AbstractCreature who, boolean postDraw) {
        UseCardListener.UpdatePostTurnStart();
        TurnEventListener.TriggerStartOfTurnEvents(who);
    }

    public static void ReceiveOnPlayerEndsTurn(AbstractPlayer p) {
        TurnEventListener.TriggerEndOfTurnEvents(p);
    }
    
    public static void ReceiveOnEndTurnPreDiscard() {
        TurnEventListener.TriggerEndTurnPreDiscard();
    }
    
    public static void ReceiveAtEndOfRound() {
        DrawCardListener.UpdateAtEndOfRound();
    }
    
    public static void ReceiveOnInitialize() {
        gameFps = new LMGameFps();
        gameFps.setCurrFps(System.nanoTime());
    }

    public static void CleanAfterJobsDone() {
        System.gc();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom r) {
        TurnEventListener.LoadAtBattleStarts(r);
    }

    @Override
    public void receivePostBattle(AbstractRoom r) {
        ApplyPowerListener.ClearPostBattle();
        DrawCardListener.ClearPostBattle();
        UseCardListener.ClearPostBattle();
    }

    @Override
    public void receivePostUpdate() {
        gameFps.calculate();
    }

    @Override
    public void receiveEditKeywords() {
        Map<String, LMKeyword> map = LMKeyword.SelfFromJson("SharedAssets/locals/"
                + getSupportedLanguage(Settings.language) + "/keywords.json");
        map.forEach((k, v) -> BaseMod.addKeyword(modid, v.PROPER, v.NAMES, v.DESCRIPTION));
    }

    @Override
    public void receiveEditCards() {
//        BaseMod.addCard(new TestCard());
        BaseMod.addCard(new TestCard2());
    }

    @Override
    public void receiveEditStrings() {
        String lang = getSupportedLanguage(Settings.language);
        BaseMod.loadCustomStringsFile(CardStrings.class, "SharedAssets/locals/" + lang + "/cards.json");
    }
}