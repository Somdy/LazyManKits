package rs.lazymankits.listeners;

import com.megacrit.cardcrawl.cards.AbstractCard;
import rs.lazymankits.LManager;

import java.util.ArrayList;
import java.util.List;

public class DrawCardListener {
    public static List<AbstractCard> CardsDrawnThisTurn;
    public static List<AbstractCard> CardsDrawnLastTurn;
    public static List<AbstractCard> CardsDrawnLastBattleTurn;
    
    public static void Initialize() {
        if (LManager.EverythingReady()) return;
        
        CardsDrawnThisTurn = new ArrayList<>();
        CardsDrawnLastTurn = new ArrayList<>();
        CardsDrawnLastBattleTurn = new ArrayList<>();
    }
    
    public static void OnCardDrawn(AbstractCard card) {
        CardsDrawnThisTurn.add(card);
    }
    
    public static void UpdateAtEndOfRound() {
        CardsDrawnLastTurn.clear();
        for (AbstractCard card : CardsDrawnThisTurn) {
            if (!CardsDrawnLastTurn.contains(card))
                CardsDrawnLastTurn.add(card);
        }
        CardsDrawnThisTurn.clear();
    }
    
    public static void ClearPostBattle() {
        CardsDrawnLastTurn.clear();
        CardsDrawnLastBattleTurn.clear();
        for (AbstractCard card : CardsDrawnThisTurn) {
            if (!CardsDrawnLastBattleTurn.contains(card))
                CardsDrawnLastBattleTurn.add(card);
        }
        CardsDrawnThisTurn.clear();
    }
}