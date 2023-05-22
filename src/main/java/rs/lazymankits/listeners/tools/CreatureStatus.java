package rs.lazymankits.listeners.tools;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.stances.AbstractStance;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.utils.LMGameGeneralUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CreatureStatus implements LMGameGeneralUtils {
    public AbstractCreature who;
    public int maxHp;
    public int currHp;
    public int currBlock;
    public int lastDamageTaken;
    public int potionSlots;
    public int energyMaster;
    public int damageTakenThisCombat;
    public int nextMove;
    public CardGroup drawpile;
    public CardGroup discardpile;
    public CardGroup hand;
    public AbstractStance stance;
    public List<AbstractPower> powers;
    public List<AbstractRelic> relics;
    public List<AbstractPotion> potions;
    public List<AbstractOrb> orbs;
    public List<AbstractBlight> blights;
    public List<AbstractCard> cardsPlayedThisCombat;
    public List<AbstractCard> cardsDrawnLastTurn;
    public List<Byte> moveHistory;
    public String moveName;
    public EnemyMoveInfo moveInfo;
    
    public CreatureStatus(@NotNull AbstractCreature who) {
        this.who = who;
        this.maxHp = who.maxHealth;
        this.currHp = who.currentHealth;
        this.currBlock = who.currentBlock;
        this.lastDamageTaken = who.lastDamageTaken;
        this.powers = new ArrayList<>(who.powers);
        if (who.isPlayer)
            assignPlayerProperties();
        else 
            assignMonsterProperties();
    }
    
    private void assignPlayerProperties() {
        if (who instanceof AbstractPlayer) {
            potionSlots = ((AbstractPlayer) who).potionSlots;
            damageTakenThisCombat = ((AbstractPlayer) who).damagedThisCombat;
            drawpile = new CardGroup(((AbstractPlayer) who).drawPile, CardGroup.CardGroupType.DRAW_PILE);
            discardpile = new CardGroup(((AbstractPlayer) who).discardPile, CardGroup.CardGroupType.DISCARD_PILE);
            hand = new CardGroup(((AbstractPlayer) who).hand, CardGroup.CardGroupType.HAND);
            try {
                energyMaster = ((AbstractPlayer) who).energy.energyMaster;
                stance = ((AbstractPlayer) who).stance.getClass().newInstance();
            } catch (Exception e) {
                energyMaster = 3;
                stance = ((AbstractPlayer) who).stance;
                LMDebug.Log("Player [" + ((AbstractPlayer) who).getLocalizedCharacterName() + "] different from others");
            }
            relics = new ArrayList<>(((AbstractPlayer) who).relics);
            potions = new ArrayList<>(((AbstractPlayer) who).potions);
            orbs = new ArrayList<>(((AbstractPlayer) who).orbs);
            blights = new ArrayList<>(((AbstractPlayer) who).blights);
            cardsPlayedThisCombat = new ArrayList<>(cardsPlayedThisCombat());
            cardsDrawnLastTurn = new ArrayList<>(cardsDrawnLastTurn());
        }
    }
    
    private void assignMonsterProperties() {
        if (who instanceof AbstractMonster) {
            nextMove = ((AbstractMonster) who).nextMove;
            moveHistory = ((AbstractMonster) who).moveHistory;
            moveName = ((AbstractMonster) who).moveName;
            try {
                Field move = AbstractMonster.class.getDeclaredField("move");
                move.setAccessible(true);
                EnemyMoveInfo info = (EnemyMoveInfo) move.get(who);
                moveInfo = new EnemyMoveInfo(info.nextMove, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
            } catch (Exception e) {
                moveInfo = null;
            }
        }
    }
}