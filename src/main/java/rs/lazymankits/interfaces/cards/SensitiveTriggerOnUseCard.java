package rs.lazymankits.interfaces.cards;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface SensitiveTriggerOnUseCard {
    
    default boolean isSensitive() {
        return true;
    }
    
    /**
     * determine if this card should trigger a gear on using
     * @param gear certain methods of the gear that the card may trigger, it could be an {@link AbstractPower},
     *             {@link AbstractRelic}, {@link AbstractCard}, {@link AbstractBlight}, {@link AbstractStance}
     * @param gearName the method name that the card may trigger
     * @return true if the card should trigger the gear on using
     * @see AbstractPower#onUseCard(AbstractCard, UseCardAction)
     * @see AbstractPower#onAfterUseCard(AbstractCard, UseCardAction)
     * @see AbstractPower#onPlayCard(AbstractCard, AbstractMonster)
     * @see AbstractPower#onAfterCardPlayed(AbstractCard)
     * @see AbstractRelic#onUseCard(AbstractCard, UseCardAction)
     * @see AbstractRelic#onPlayCard(AbstractCard, AbstractMonster)
     * @see AbstractCard#triggerOnOtherCardPlayed(AbstractCard)
     * @see AbstractCard#onPlayCard(AbstractCard, AbstractMonster)
     * @see AbstractBlight#onPlayCard(AbstractCard, AbstractMonster)
     * @see AbstractStance#onPlayCard(AbstractCard)
     */
    boolean canTriggerOnGear(Object gear, String gearName);
    
    /**
     * determine if this card should record in {@link GameActionManager#cardsPlayedThisCombat}
     * @return true if the card should record in {@link GameActionManager#cardsPlayedThisCombat}
     */
    boolean countInCombatHistory();
    
    /**
     * determine if this card should record in {@link GameActionManager#cardsPlayedThisTurn}
     * and if the card should increase the value of {@link AbstractPlayer#cardsPlayedThisTurn}
     * @return true if the card should record in {@link GameActionManager#cardsPlayedThisTurn}
     * @apiNote returns {@link #countInCombatHistory()} by default
     */
    default boolean countInTurnHistory() {
        return countInCombatHistory();
    }
    
    default void doSelfCombatRecord() {}
    default void doSelfTurnRecord() {}
}