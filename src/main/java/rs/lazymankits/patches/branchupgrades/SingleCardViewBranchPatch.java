package rs.lazymankits.patches.branchupgrades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
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

public class SingleCardViewBranchPatch {

    @SpirePatch(clz = SingleCardViewPopup.class, method = SpirePatch.CLASS)
    public static class OptFields {
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
    public static AbstractCard copy = null;
    public static boolean CardChecked = false;

    public static void CheckIfButtonsReady(SingleCardViewPopup _inst) {
        if (OptFields.SelectingBranch.get(_inst)) {
            if (OptFields.PrevBtn.get(_inst) == null) {
                OptFields.PrevBtn.set(_inst, new Hitbox(70, 70));
                OptFields.PrevBtn.get(_inst).move(Settings.WIDTH * 0.85F, Settings.HEIGHT / 2F + 55F * Settings.scale);
            }
            if (OptFields.NextBtn.get(_inst) == null) {
                OptFields.NextBtn.set(_inst, new Hitbox(70, 70));
                OptFields.NextBtn.get(_inst).move(Settings.WIDTH * 0.85F, Settings.HEIGHT / 2F - 45F * Settings.scale);
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "update")
    public static class CheckIfCardHasBranches {
        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup _inst) {
            CheckIfCardBranchable(_inst);
        }
    }
    
    public static void CheckIfCardBranchable(SingleCardViewPopup _inst) {
        AbstractCard card = GetHoveredCard();
        if (card instanceof BranchableUpgradeCard && ((BranchableUpgradeCard) card).canBranch() 
                && ViewingUpgrade()) {
            SetBranchesPreview(_inst, card);
        }
        if (OptFields.SelectingBranch.get(_inst) && Current >= 0 && Branches.length > 0) {
            if (Branches[Current] != null) {
                Branches[Current].update();
            }
            if (Prev > -1) {
                Branches[Prev].update();
            }
            if (Next > -1) {
                Branches[Next].update();
            }
        }
    }
    
    @SpirePatch(clz = SingleCardViewPopup.class, method = "updateInput")
    public static class UpdateInputButtons {
        @SpirePrefixPatch
        public static void Prefix(SingleCardViewPopup _inst) {
            UpdateButtons(_inst);
        }
    }

    public static void UpdateButtons(SingleCardViewPopup _inst) {
        if (OptFields.PrevBtn.get(_inst) != null) {
            Hitbox btn = OptFields.PrevBtn.get(_inst);
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
        if (OptFields.NextBtn.get(_inst) != null) {
            Hitbox btn = OptFields.NextBtn.get(_inst);
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

    public static void SwapPrevBranch(SingleCardViewPopup _inst) {
        //LMDebug.Log("Prev button pressed...");
        CurrBranch--;
        AbstractCard card = GetHoveredCard();
        SetBranchesPreview(_inst, card);
    }

    public static void SwapNextBranch(SingleCardViewPopup _inst) {
        //LMDebug.Log("Next button pressed...");
        CurrBranch++;
        AbstractCard card = GetHoveredCard();
        SetBranchesPreview(_inst, card);
    }
    
    @SpirePatch(clz = SingleCardViewPopup.class, method = "updateInput")
    public static class SecureArrowsHitboxes {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() throws Exception {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    int line = 291;
                    try {
                        Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "close");
                        line = LineFinder.findAllInOrder(m.where(), matcher)[0];
                    } catch (Exception ignored) {}
                    if (m.getMethodName().equals("close") && m.getLineNumber() == line) {
                        m.replace("if (" + SingleCardViewBranchPatch.class.getName() + ".ArrowsUnhovered(this)) $_ = $proceed($$);");
                    }
                }
            };
        }
    }
    
    public static boolean ArrowsUnhovered(SingleCardViewPopup _inst) {
        boolean next = false;
        boolean prev = false;
        if (OptFields.NextBtn.get(_inst) != null) {
            next = OptFields.NextBtn.get(_inst).hovered;
        }
        if (OptFields.PrevBtn.get(_inst) != null) {
            prev = OptFields.PrevBtn.get(_inst).hovered;
        }
        return !next && !prev;
    }
    
    @SpirePatch(clz = SingleCardViewPopup.class, method = "updateUpgradePreview")
    public static class CheckIfViewingUpgrade {
        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup _inst) {
            OptFields.SelectingBranch.set(_inst, ViewingUpgrade());
        }
    }

    public static void SetBranchesPreview(SingleCardViewPopup _inst, AbstractCard card) {
        if (card instanceof BranchableUpgradeCard) {
            if (CurrBranch < 0) {
                CurrBranch = ((BranchableUpgradeCard) card).defaultBranch();
            }
//            List<UpgradeBranch> branches = ((BranchableUpgradeCard) card).possibleBranches();
            List<UpgradeBranch> branches = ((BranchableUpgradeCard) card).getPossibleBranches();
            if (branches == null || branches.isEmpty()) return;
            int length = Math.min(Branches.length, branches.size());
            if (!CardChecked) {
                copy = card.makeStatEquivalentCopy();
                Last = length - 1;
                LMDebug.Log(card.name + " has " + (Last + 1) + " upgrade branches");
                for (int i = 0; i < length; i++) {
                    AbstractCard previewCard = card.makeStatEquivalentCopy();
                    ((BranchableUpgradeCard) previewCard).getPossibleBranches().get(i).upgrade();
                    previewCard.displayUpgrades();
                    Branches[i] = previewCard;
                }
                CardChecked = true;
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
            //LMDebug.Log("Current branch: " + Current + ", prev: " + Prev + ", next: " + Next);
            for (int i = 0; i < length; i++) {
                if (i != Current && i != Prev && i != Next) {
                    Branches[i].drawScale = 0.25F;
                    Branches[i].targetDrawScale = 0.25F;
                    Branches[i].transparency = 1F;
                    Branches[i].targetTransparency = 1F;
                }
            }
            CheckIfButtonsReady(_inst);
        }
    }
    
//    @SpirePatch(clz = SingleCardViewPopup.class, method = "render")
//    public static class StopSTopSTOpSTOPNewNEwNEWaACardCArdCARdCARDEachEAchEAChEACHFrameFRameFRAmeFRAMeFRAME {
//        @SpireInstrumentPatch
//        public static ExprEditor Instrument() {
//            return new ExprEditor() {
//                @Override
//                public void edit(MethodCall m) throws CannotCompileException {
//                    if (m.getClassName().equals(AbstractCard.class.getName())
//                            && m.getMethodName().equals("makeStatEquivalentCopy")) {
//                        String path = SingleCardViewBranchPatch.class.getName();
//                        m.replace("if (" + path + ".copy == null && " + path + ".IsSelectingBranch(this))" +
//                                " {$_ = $proceed($$);}");
//                    }
//                }
//            };
//        }
//    }
    
    @SpirePatch(clz = SingleCardViewPopup.class, method = "render")
    public static class RenderingBranches {
        @SpireInsertPatch(locator = Locator.class, localvars = {"copy"})
        public static void Insert(SingleCardViewPopup _inst, SpriteBatch sb, @ByRef AbstractCard[] fakeCopy) {
            AbstractCard card = GetHoveredCard();
            if (card instanceof BranchableUpgradeCard && ((BranchableUpgradeCard) card).canBranch()) {
                if (!ViewingUpgrade()) {
                    fakeCopy[0] = card.makeStatEquivalentCopy();
                    return;
                }
                if (Current < 0) {
                    CheckIfCardBranchable(_inst);
                    return;
                }
                SetHoveredCard(Branches[Current]);
                if (Prev > -1) {
                    Branches[Prev].drawScale = 0.85F;
                    Branches[Prev].current_x = Settings.WIDTH * 0.92F;
                    Branches[Prev].current_y = Settings.HEIGHT * 0.8F;
                    Branches[Prev].target_x = Settings.WIDTH * 0.9F;
                    Branches[Prev].target_y = Settings.HEIGHT * 0.8F;
                    Branches[Prev].transparency = 1F;
                    Branches[Prev].targetTransparency = 1F;
                    Branches[Prev].render(sb);
                    Branches[Prev].updateHoverLogic();
                    Branches[Prev].renderCardTip(sb);
                }
                if (Next > -1) {
                    Branches[Next].drawScale = 0.85F;
                    Branches[Next].current_x = Settings.WIDTH * 0.92F;
                    Branches[Next].current_y = Settings.HEIGHT * 0.18F;
                    Branches[Next].target_x = Settings.WIDTH * 0.9F;
                    Branches[Next].target_y = Settings.HEIGHT * 0.18F;
                    Branches[Next].transparency = 1F;
                    Branches[Next].targetTransparency = 1F;
                    Branches[Next].render(sb);
                    Branches[Next].updateHoverLogic();
                    Branches[Next].renderCardTip(sb);
                }
                if (Prev <= -1 && Next <= -1)
                    LMDebug.Log(card.name + " should be branchable but has no any branches available");
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "renderCardBack");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
    
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderArrows")
    public static class RenderSelectArrows {
        public static void Postfix(SingleCardViewPopup _inst, SpriteBatch sb) {
            AbstractCard card = GetHoveredCard();
            if (ViewingUpgrade() && OptFields.SelectingBranch.get(_inst)) {
                RenderPrevAndNextArrows(_inst, sb);
            }
        }
    }

    public static void RenderPrevAndNextArrows(SingleCardViewPopup _inst, SpriteBatch sb) {
        if (OptFields.NextBtn.get(_inst) != null) {
            Hitbox box = OptFields.NextBtn.get(_inst);
            sb.setColor(Color.WHITE.cpy());
            float scale = box.hovered ? 1.75F * Settings.scale : 1.25F * Settings.scale;
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
        if (OptFields.PrevBtn.get(_inst) != null) {
            Hitbox box = OptFields.PrevBtn.get(_inst);
            sb.setColor(Color.WHITE.cpy());
            float scale = box.hovered ? 1.75F * Settings.scale : 1.25F * Settings.scale;
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
    
    @SpirePatch(clz = SingleCardViewPopup.class, method = "close")
    public static class CloseAllLogics {
        @SpirePostfixPatch
        public static void Postfix(SingleCardViewPopup _inst) {
            OptFields.SelectingBranch.set(_inst, false);
            OptFields.PrevBtn.set(_inst, null);
            OptFields.NextBtn.set(_inst, null);
            CardChecked = false;
            copy = null;
            CurrBranch = -1;
            Current = -1;
        }
    }

    public static boolean IsSelectingBranch(SingleCardViewPopup _inst) {
        return OptFields.SelectingBranch.get(_inst);
    }
    
    public static boolean ViewingUpgrade() {
        return SingleCardViewPopup.isViewingUpgrade;
    }
    
    public static AbstractCard GetHoveredCard() {
        try {
            Field card = SingleCardViewPopup.class.getDeclaredField("card");
            card.setAccessible(true);
            return (AbstractCard) card.get(CardCrawlGame.cardPopup);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void SetHoveredCard(AbstractCard c) {
        if (c == null) return;
        try {
            Field card = SingleCardViewPopup.class.getDeclaredField("card");
            card.setAccessible(true);
            card.set(CardCrawlGame.cardPopup, c);
        } catch (Exception ignored) {
        }
    }
}