package rs.lazymankits.managers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import org.jetbrains.annotations.Nullable;
import rs.lazymankits.interfaces.LMSubscriber;

import java.util.HashMap;
import java.util.Map;

public class LMCustomCardTagMgr {
    private static final Map<String, AbstractCard.CardTags> customs = new HashMap<>();
    
    public static void StartRegisterTags() {
        LMSubscriber.PublishOnRegisteringAttrs();
    }

    public static boolean RegisterTag(String key, AbstractCard.CardTags tag) {
        if (customs.containsKey(key))
            return false;
        customs.put(key, tag);
        return true;
    }

    @Nullable
    public static AbstractCard.CardTags GetCustomTag(String tag) {
        if (customs.containsKey(tag))
            return customs.get(tag);
        return null;
    }
}