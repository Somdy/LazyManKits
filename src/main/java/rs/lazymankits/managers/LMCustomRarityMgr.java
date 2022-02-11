package rs.lazymankits.managers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import org.jetbrains.annotations.Nullable;
import rs.lazymankits.interfaces.LMSubscriber;

import java.util.HashMap;
import java.util.Map;

public class LMCustomRarityMgr {
    private static final Map<String, AbstractCard.CardRarity> customs = new HashMap<>();
    
    public static void StartRegisterRarity() {
        LMSubscriber.PublishOnRegisteringAttrs();
    }

    public static boolean RegisterRarity(String key, AbstractCard.CardRarity rarity) {
        if (customs.containsKey(key))
            return false;
        customs.put(key, rarity);
        return true;
    }

    @Nullable
    public static AbstractCard.CardRarity GetCustomRarity(String key) {
        if (customs.containsKey(key))
            return customs.get(key);
        return null;
    }
}