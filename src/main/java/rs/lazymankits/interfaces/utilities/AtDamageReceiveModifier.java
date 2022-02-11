package rs.lazymankits.interfaces.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import rs.lazymankits.interfaces.LMSubscriberInterface;

public interface AtDamageReceiveModifier extends LMSubscriberInterface {
    default float atDamageReceive(float damage, DamageInfo.DamageType type) {
        return damage;
    }

    default float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCreature owner, AbstractCreature target) {
        return atDamageReceive(damage, type);
    }

    default float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractCreature target) {
        return atDamageReceive(damage, type);
    }
}