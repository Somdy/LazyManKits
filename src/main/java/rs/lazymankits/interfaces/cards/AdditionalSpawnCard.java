package rs.lazymankits.interfaces.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.random.Random;

import java.util.ArrayList;

public interface AdditionalSpawnCard {
    // spawn in combat
    boolean canSpawnInCombat(ArrayList<AbstractCard> list, AbstractPlayer.PlayerClass playerClass);
    boolean canSpawnInCombat(ArrayList<AbstractCard> list, AbstractCard.CardType requiredType, 
                             AbstractPlayer.PlayerClass playerClass);
    boolean canSpawnInCombatAsColorless(ArrayList<AbstractCard> list, Random rng, String prohibitedID, 
                                        AbstractPlayer.PlayerClass playerClass);
}