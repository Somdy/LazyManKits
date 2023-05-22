package rs.lazymankits.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import rs.lazymankits.interfaces.powers.CardPlayablePower;
import rs.lazymankits.interfaces.powers.CardProofPower;
import rs.lazymankits.interfaces.powers.CardTauntPower;
import rs.lazymankits.listeners.UseCardListener;
import rs.lazymankits.utils.LMSK;

import java.util.List;

public class CanCardUsePatch {
    @SpirePatch(clz = AbstractCard.class, method = "canUse")
    public static class CustomCanUseCardLogicPatch {
        @SpirePrefixPatch
        public static SpireReturn<Boolean> Prefix(AbstractCard _inst, AbstractPlayer p, AbstractMonster m) {
            if (UseCardListener.ContainsUnplayableCard(_inst)) {
                boolean canPlay = UseCardListener.CanCardPlay(_inst, p, m);
                if (!canPlay) return SpireReturn.Return(false);
            }
//            if (m != null && !m.isDeadOrEscaped() && !m.powers.isEmpty() && m.powers.stream()
//                    .anyMatch(po -> po instanceof CardProofPower)) {
//                for (AbstractPower po : m.powers) {
//                    if (po instanceof CardProofPower) {
//                        boolean canPlay = ((CardProofPower) po).canPlayerUseCard(_inst, p, m);
//                        if (!canPlay) return SpireReturn.Return(false);
//                    }
//                }
//            }
//            List<AbstractMonster> monsters = LMSK.GetAllExptMonsters(mo -> mo != m && !mo.isDeadOrEscaped()
//                    && !mo.powers.isEmpty() && mo.powers.stream().anyMatch(po -> po instanceof CardTauntPower));
//            for (AbstractMonster mo : monsters) {
//                for (AbstractPower po : mo.powers) {
//                    if (po instanceof CardTauntPower) {
//                        boolean canPlay = ((CardTauntPower) po).canPlayerUseCardAtOthers(_inst, p, m);
//                        if (!canPlay) return SpireReturn.Return(false);
//                    }
//                }
//            }
            List<AbstractMonster> monsters = LMSK.GetAllExptMstr(mo -> !mo.powers.isEmpty() && mo.powers.stream()
                    .anyMatch(po -> po instanceof CardPlayablePower));
            for (AbstractMonster mo : monsters) {
                for (AbstractPower po : mo.powers) {
                    if (po instanceof CardPlayablePower) {
                        boolean canPlay = ((CardPlayablePower) po).canUseCard(_inst, p, m);
                        if (!canPlay) return SpireReturn.Return(false);
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }
}