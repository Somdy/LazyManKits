package rs.lazymankits.patches.triggers;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.listeners.ApplyPowerListener;

public class OnApplyPowerTrigger {
    @SpirePatch(clz = ApplyPowerAction.class, method = "update")
    public static class OnApplyPowerCheckerPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"powerToApply", "duration"})
        public static SpireReturn Insert(ApplyPowerAction _inst, @ByRef AbstractPower[] powerToAppy, @ByRef float[] duration) {
            return InsertManipulator(_inst, _inst.target, _inst.source, powerToAppy, duration);
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "hasRelic");
                return LineFinder.findInOrder(ctMethodToPatch, methodCallMatcher);
            }
        }
    }

    private static SpireReturn InsertManipulator(@NotNull AbstractGameAction action, AbstractCreature target, AbstractCreature source,
                                                 @NotNull AbstractPower[] power, float[] duration) {
        boolean resetAmount = power[0].amount == action.amount;
        power[0] = ApplyPowerListener.OnApplyPower(power[0], target, source);
        if (power[0] != null && power[0].amount != action.amount && resetAmount)
            action.amount = power[0].amount;
        if (power[0] == null) {
            //CardCrawlGame.sound.play(NesFab.makeID("Nullify_EXT"));
            AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(target, ApplyPowerAction.TEXT[2]));
            duration[0] -= Gdx.graphics.getDeltaTime();
            action.isDone = true;
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}