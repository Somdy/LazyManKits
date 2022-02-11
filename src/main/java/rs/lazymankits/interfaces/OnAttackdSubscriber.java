package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;

public interface OnAttackdSubscriber extends LMSubscriberInterface {
    /**
     * triggers when a creature is attacking or attacked
     * @apiNote use info.owner or target to decide whether is attacking or attacked
     * @param damage the damage value
     * @param info the damage info
     * @param target the creature that's attacked
     */
    void onAttackd(int damage, DamageInfo info, AbstractCreature target);
}