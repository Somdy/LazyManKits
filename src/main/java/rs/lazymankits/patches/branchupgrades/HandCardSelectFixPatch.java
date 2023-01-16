package rs.lazymankits.patches.branchupgrades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import rs.lazymankits.LMDebug;
import rs.lazymankits.interfaces.cards.BranchableUpgradeCard;
import rs.lazymankits.interfaces.cards.UpgradeBranch;

import java.lang.reflect.Field;
import java.util.List;

import static rs.lazymankits.patches.branchupgrades.BranchableUpgradePatch.MAX_BRANCHES;

@SuppressWarnings("unused")
public class HandCardSelectFixPatch {
    @SpirePatch(clz = HandCardSelectScreen.class, method = SpirePatch.CLASS)
    public static class HandOptFields {
        public static SpireField<Boolean> SelectingBranch = new SpireField<>(() -> false);
        public static SpireField<Hitbox> PrevBtn = new SpireField<>(() -> null);
        public static SpireField<Hitbox> NextBtn = new SpireField<>(() -> null);
    }

    public static int CurrBranch = -1;
    public static AbstractCard[] Branches = new AbstractCard[MAX_BRANCHES];
    public static int Current = -1;
    public static int Prev = -1;
    public static int Next = -1;
    public static int Last = MAX_BRANCHES - 1;
    public static boolean CardChecked = false;
    
    public static boolean IsSelectingBranch() {
        return HandOptFields.SelectingBranch.get(AbstractDungeon.handCardSelectScreen);
    }

    public static void CheckIfButtonsReady(HandCardSelectScreen _inst) {
        if (HandOptFields.SelectingBranch.get(_inst)) {
            if (HandOptFields.PrevBtn.get(_inst) == null) {
                HandOptFields.PrevBtn.set(_inst, new Hitbox(60, 60));
                HandOptFields.PrevBtn.get(_inst).move(Settings.WIDTH * 0.83F, Settings.HEIGHT / 2F + 200F * Settings.scale);
            }
            if (HandOptFields.NextBtn.get(_inst) == null) {
                HandOptFields.NextBtn.set(_inst, new Hitbox(60, 60));
                HandOptFields.NextBtn.get(_inst).move(Settings.WIDTH * 0.83F, Settings.HEIGHT / 2F + 120F * Settings.scale);
            }
        }
    }
    
    //@SpirePatch(clz = HandCardSelectScreen.class, method = "selectHoveredCard")
    public static class CheckIfCardHasBranches {
        //@SpireInsertPatch(locator = Locator.class)
        public static void Insert(HandCardSelectScreen _inst) {
            AbstractCard card = _inst.selectedCards.group.get(0);
            if (card instanceof BranchableUpgradeCard && ((BranchableUpgradeCard) card).canBranch()) {
                SetBranchesPreview(_inst, card);
            }
        }
        /*private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class,
                        "makeStatEquivalentCopy");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }*/
    }

    public static void SetBranchesPreview(HandCardSelectScreen _inst, AbstractCard card) {
        if (card instanceof BranchableUpgradeCard) {
            if (CurrBranch < 0) {
                CurrBranch = ((BranchableUpgradeCard) card).defaultBranch();
            }
//            List<UpgradeBranch> branches = ((BranchableUpgradeCard) card).possibleBranches();
            List<UpgradeBranch> branches = ((BranchableUpgradeCard) card).getPossibleBranches();
            if (branches == null || branches.isEmpty()) return;
            int length = Math.min(Branches.length, branches.size());
            Last = length - 1;
            LMDebug.Log(card.name + " has " + (Last + 1) + " upgrade branches");
            for (int i = 0; i < length; i++) {
                AbstractCard previewCard = card.makeStatEquivalentCopy();
                ((BranchableUpgradeCard) previewCard).getPossibleBranches().get(i).upgrade();
                if (!((BranchableUpgradeCard) previewCard).usingLocalBranch()) 
                    ((BranchableUpgradeCard) previewCard).setChosenBranch(i);
                previewCard.displayUpgrades();
                Branches[i] = previewCard;
            }
            Current = CurrBranch;
            if (Current > 0) {
                Prev = Current - 1;
            } else {
                Prev = -1;
            }
            if (Current < Last) {
                Next = Current + 1;
            } else {
                Next = -1;
            }
            HandOptFields.SelectingBranch.set(_inst, true);
            LMDebug.Log("Current branch: " + Current + ", prev: " + Prev + ", next: " + Next);
            for (int i = 0; i < length; i++) {
                if (i != Current && i != Prev && i != Next) {
                    Branches[i].drawScale = 0.25F;
                    Branches[i].targetDrawScale = 0.25F;
                    Branches[i].transparency = 0F;
                    Branches[i].targetTransparency = 0F;
                }
            }
            CheckIfButtonsReady(_inst);
        }
    }
    
    @SpirePatch(clz = HandCardSelectScreen.class, method = "updateSelectedCards")
    public static class UpdateBranchCard {
        @SpirePrefixPatch
        public static void Prefix(HandCardSelectScreen _inst) {
            if (HandOptFields.SelectingBranch.get(_inst)) {
                if (Branches[Current] != null) {
                    Branches[Current].update();
                    UpdateButtons(_inst);
                }
            }
        }
    }

    public static void UpdateButtons(HandCardSelectScreen _inst) {
        if (HandOptFields.PrevBtn.get(_inst) != null) {
            Hitbox btn = HandOptFields.PrevBtn.get(_inst);
            btn.update();
            if (Prev > -1) {
                if (btn.justHovered)
                    CardCrawlGame.sound.play("UI_HOVER");
                if (btn.hovered && InputHelper.justClickedLeft) {
                    btn.clickStarted = true;
                    CardCrawlGame.sound.play("UI_CLICK_1");
                }
                if (btn.clicked) {
                    btn.clicked = false;
                    SwapPrevBranch(_inst);
                }
            }
        }
        if (HandOptFields.NextBtn.get(_inst) != null) {
            Hitbox btn = HandOptFields.NextBtn.get(_inst);
            btn.update();
            if (Next > -1) {
                if (btn.justHovered)
                    CardCrawlGame.sound.play("UI_HOVER");
                if (btn.hovered && InputHelper.justClickedLeft) {
                    btn.clickStarted = true;
                    CardCrawlGame.sound.play("UI_CLICK_1");
                }
                if (btn.clicked) {
                    btn.clicked = false;
                    SwapNextBranch(_inst);
                }
            }
        }
    }

    public static void SwapPrevBranch(HandCardSelectScreen _inst) {
        LMDebug.Log("Prev button pressed...");
        CurrBranch--;
        AbstractCard card = _inst.selectedCards.group.get(0);
        SetBranchesPreview(_inst, card);
    }

    public static void SwapNextBranch(HandCardSelectScreen _inst) {
        LMDebug.Log("Next button pressed...");
        CurrBranch++;
        AbstractCard card = _inst.selectedCards.group.get(0);
        SetBranchesPreview(_inst, card);
    }

    @SpirePatch(clz = HandCardSelectScreen.class, method = "render")
    public static class RenderUpgradeBranches {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(HandCardSelectScreen.class.getName()) && m.getMethodName().equals("renderArrows")) {
                        m.replace("$_ = $proceed($$); " +
                                "if (" + HandCardSelectFixPatch.class.getName() + ".RenderingBranches(this, sb)) {return;}");
                    }
                }
            };
        }
    }

    public static boolean RenderingBranches(HandCardSelectScreen _inst, SpriteBatch sb) {
        AbstractCard card = _inst.selectedCards.group.get(0);
        boolean forUpgrade;
        try {
            Field upgrade = _inst.getClass().getDeclaredField("forUpgrade");
            upgrade.setAccessible(true);
            forUpgrade = upgrade.getBoolean(_inst);
        } catch (Exception e) {
            forUpgrade = HandOptFields.SelectingBranch.get(_inst);
        }
        if (forUpgrade && card instanceof BranchableUpgradeCard && ((BranchableUpgradeCard) card).canBranch()) {
            /*card.current_x = Settings.WIDTH * 0.35F;
            card.current_y = Settings.HEIGHT / 2F;
            card.target_x = Settings.WIDTH * 0.35F;
            card.target_y = Settings.HEIGHT / 2F;
            card.drawScale = 0.9F;
            card.render(sb);
            card.updateHoverLogic();*/ // No need to render original card
            if (!CardChecked) {
                CheckIfCardHasBranches.Insert(_inst);
                CardChecked = true;
            }
            if (Current > -1) {
                Branches[Current].drawScale = 0.7F;
                Branches[Current].current_x = Settings.WIDTH * 0.63F;
                Branches[Current].current_y = Settings.HEIGHT / 2F + 160F * Settings.scale;
                Branches[Current].target_x = Settings.WIDTH * 0.63F;
                Branches[Current].target_y = Settings.HEIGHT / 2F + 160F * Settings.scale;
                Branches[Current].transparency = 1F;
                Branches[Current].applyPowers();
                Branches[Current].render(sb);
                Branches[Current].updateHoverLogic();
                Branches[Current].renderCardTip(sb);
                Branches[Current].beginGlowing();
            }
            if (Prev > -1) {
                Branches[Prev].drawScale = 0.4F;
                Branches[Prev].current_x = Settings.WIDTH * 0.75F;
                Branches[Prev].current_y = Settings.HEIGHT / 2F + 270F * Settings.scale;
                Branches[Prev].target_x = Settings.WIDTH * 0.75F;
                Branches[Prev].target_y = Settings.HEIGHT / 2F + 270F * Settings.scale;
                Branches[Prev].transparency = 0.7F;
                Branches[Prev].targetTransparency = 0.7F;
                Branches[Prev].applyPowers();
                Branches[Prev].render(sb);
                Branches[Prev].updateHoverLogic();
                Branches[Prev].renderCardTip(sb);
            }
            if (Next > -1) {
                Branches[Next].drawScale = 0.4F;
                Branches[Next].current_x = Settings.WIDTH * 0.75F;
                Branches[Next].current_y = Settings.HEIGHT / 2F + 50F * Settings.scale;
                Branches[Next].target_x = Settings.WIDTH * 0.75F;
                Branches[Next].target_y = Settings.HEIGHT / 2F + 50F * Settings.scale;
                Branches[Next].transparency = 0.7F;
                Branches[Next].targetTransparency = 0.7F;
                Branches[Next].applyPowers();
                Branches[Next].render(sb);
                Branches[Next].updateHoverLogic();
                Branches[Next].renderCardTip(sb);
            }
            try {
                Field peekButton = _inst.getClass().getDeclaredField("peekButton");
                peekButton.setAccessible(true);
                PeekButton peek = (PeekButton) peekButton.get(_inst);
                peek.render(sb);
            } catch (Exception e) {
                LMDebug.Log("Failed to render peek button in hand card select screen...");
            }
            AbstractDungeon.overlayMenu.combatDeckPanel.render(sb);
            AbstractDungeon.overlayMenu.discardPilePanel.render(sb);
            AbstractDungeon.overlayMenu.exhaustPanel.render(sb);
            return true;
        }
        return false;
    }

    public static void RenderPrevAndNextArrows(HandCardSelectScreen _inst, SpriteBatch sb) {
        if (HandOptFields.NextBtn.get(_inst) != null) {
            Hitbox box = HandOptFields.NextBtn.get(_inst);
            float scale = box.hovered ? 1.5F * Settings.scale : Settings.scale;
            if (Next <= -1) {
                sb.setColor(Color.GRAY.cpy());
                scale = Settings.scale;
            }
            box.render(sb);
            sb.draw(ImageMaster.UPGRADE_ARROW, box.x, box.y, 32F, 32F, 64F, 64F,
                    scale, scale, -90F, 0, 0, 64, 64,
                    false, false);
            sb.setColor(Color.WHITE.cpy());
        }
        if (HandOptFields.PrevBtn.get(_inst) != null) {
            Hitbox box = HandOptFields.PrevBtn.get(_inst);
            float scale = box.hovered ? 1.5F * Settings.scale : Settings.scale;
            if (Prev <= -1) {
                sb.setColor(Color.GRAY.cpy());
                scale = Settings.scale;
            }
            box.render(sb);
            sb.draw(ImageMaster.UPGRADE_ARROW, box.x, box.y, 32F, 32F, 64F, 64F,
                    scale, scale, 90F, 0, 0, 64, 64,
                    false, false);
            sb.setColor(Color.WHITE.cpy());
        }
    }

    @SpirePatch(clz = HandCardSelectScreen.class, method = "renderArrows")
    public static class RenderSelectArrows {
        @SpirePostfixPatch
        public static void Postfix(HandCardSelectScreen _inst, SpriteBatch sb) {
            if (HandOptFields.SelectingBranch.get(_inst)) {
                RenderPrevAndNextArrows(_inst, sb);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class CancelUpgradeCheck {
        @SpirePrefixPatch
        public static void Prefix() {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT
                    && HandOptFields.SelectingBranch.get(AbstractDungeon.handCardSelectScreen)) {
                HandOptFields.SelectingBranch.set(AbstractDungeon.handCardSelectScreen, false);
                //HandOptFields.ForBranchingUpgrades.set(_inst, false);
                CurrBranch = -1;
                CardChecked = false;
            }
        }
    }

    @SpirePatch(clz = HandCardSelectScreen.class, method = "update")
    public static class ConfirmBranchingCheck {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(HandCardSelectScreen _inst) {
            AbstractCard card = _inst.selectedCards.group.get(0);
            if (card instanceof BranchableUpgradeCard && HandOptFields.SelectingBranch.get(_inst) && CurrBranch >= 0) {
                LMDebug.Log("Final chosen branch: " + CurrBranch);
                if (Branches[Current] != null) {
                    int branch = ((BranchableUpgradeCard) Branches[Current]).finalBranch();
                    ((BranchableUpgradeCard) card).setChosenBranch(branch);
                } else {
                    ((BranchableUpgradeCard) card).setChosenBranch(CurrBranch);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractDungeon.class,
                        "closeCurrentScreen");
                int[] totalTimes = LineFinder.findAllInOrder(ctMethodToPatch, matcher);
                int lastTime = totalTimes.length - 1;
                return new int[] {totalTimes[lastTime]};
            }
        }
    }
}