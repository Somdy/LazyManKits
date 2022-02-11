package rs.lazymankits.interfaces.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import rs.lazymankits.interfaces.LMSubscriberInterface;

public interface AtDamageGiveModifier extends LMSubscriberInterface {
    default float atDamageGive(float damage, DamageInfo.DamageType type) {
        return damage;
    }
    
    default float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        return atDamageGive(damage, type);
    }
    
    default float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCreature owner, AbstractCreature target) {
        return atDamageGive(damage, type);
    }
}