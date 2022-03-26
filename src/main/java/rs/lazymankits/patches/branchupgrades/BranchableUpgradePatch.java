package rs.lazymankits.patches.branchupgrades;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.PeekButton;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import rs.lazymankits.LMDebug;
import rs.lazymankits.interfaces.cards.BranchableUpgradeCard;
import rs.lazymankits.interfaces.cards.UpgradeBranch;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BranchableUpgradePatch {
    
    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class CardBranchField {
        public static SpireField<Integer> ChosenBranch = new SpireField<>(() -> -1);
    }
    
    @SpirePatch(clz = CardSave.class, method = SpirePatch.CLASS)
    public static class CardSaveField {
        public static SpireField<Integer> ChosenBranch = new SpireField<>(() -> -1);
    }
    
    @SpirePatch(clz = GridCardSelectScreen.class, method = SpirePatch.CLASS)
    public static class OptFields {
        public static SpireField<Boolean> SelectingBranch = new SpireField<>(() -> false);
        //public static SpireField<Boolean> ForBranchingUpgrades = new SpireField<>(() -> false);
        public static SpireField<Hitbox> PrevBtn = new SpireField<>(() -> null);
        public static SpireField<Hitbox> NextBtn = new SpireField<>(() -> null);
    }
    
    public static final int MAX_BRANCHES = 15;
    public static int CurrBranch = -1;
    public static AbstractCard[] Branches = new AbstractCard[MAX_BRANCHES];
    public static int Current = -1;
    public static int Prev = -1;
    public static int Next = -1;
    public static int Last = MAX_BRANCHES - 1;
    
    public static boolean IsSelectingBranch() {
        return OptFields.SelectingBranch.get(AbstractDungeon.gridSelectScreen);
    }
    
    public static void CheckIfButtonsReady(GridCardSelectScreen _inst) {
        if (OptFields.SelectingBranch.get(_inst)) {
            if (OptFields.PrevBtn.get(_inst) == null) {
                OptFields.PrevBtn.set(_inst, new Hitbox(60, 60));
                OptFields.PrevBtn.get(_inst).move(Settings.WIDTH * 0.75F, Settings.HEIGHT / 2F + 50F * Settings.scale);
            }
            if (OptFields.NextBtn.get(_inst) == null) {
                OptFields.NextBtn.set(_inst, new Hitbox(60, 60));
                OptFields.NextBtn.get(_inst).move(Settings.WIDTH * 0.75F, Settings.HEIGHT / 2F - 50F * Settings.scale);
            }
        }
    }
    
    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class CheckIfCardHasBranches {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GridCardSelectScreen _inst) {
            AbstractCard card = GetHoveredCard();
            if (card instanceof BranchableUpgradeCard && ((BranchableUpgradeCard) card).canBranch()) {
                SetBranchesPreview(_inst, card);
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, 
                        "makeStatEquivalentCopy");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
    
    public static void SetBranchesPreview(GridCardSelectScreen _inst, AbstractCard card) {
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
//                ((BranchableUpgradeCard) previewCard).possibleBranches().get(i).upgrade();
                ((BranchableUpgradeCard) previewCard).getPossibleBranches().get(i).upgrade();
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
            OptFields.SelectingBranch.set(_inst, true);
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
    
    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class UpdateBranchCards {
        @SpireInsertPatch(rloc = 199)
        public static void Insert(GridCardSelectScreen _inst) {
            if (OptFields.SelectingBranch.get(_inst)) {
                if (Branches[Current] != null) {
                    Branches[Current].update();
                    UpdateButtons(_inst);
                }
                if (Prev > -1 && Branches[Prev] != null) {
                    Branches[Prev].update();
                }
                if (Next > -1 && Branches[Next] != null) {
                    Branches[Next].update();
                }
            }
        }
        /*private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.MethodCallMatcher matcher = new Matcher.MethodCallMatcher(AbstractCard.class, "update");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }*/
    }
    
    public static void UpdateButtons(GridCardSelectScreen _inst) {
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
    
    public static void SwapPrevBranch(GridCardSelectScreen _inst) {
        LMDebug.Log("Prev button pressed...");
        CurrBranch--;
        AbstractCard card = GetHoveredCard();
        SetBranchesPreview(_inst, card);
    }
    
    public static void SwapNextBranch(GridCardSelectScreen _inst) {
        LMDebug.Log("Next button pressed...");
        CurrBranch++;
        AbstractCard card = GetHoveredCard();
        SetBranchesPreview(_inst, card);
    }
    
    @SpirePatch(clz = GridCardSelectScreen.class, method = "render")
    public static class RenderUpgradeBranches {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(GridCardSelectScreen.class.getName()) && m.getMethodName().equals("renderArrows")) {
                        m.replace("$_ = $proceed($$); " +
                                "if (" + BranchableUpgradePatch.class.getName() + ".RenderingBranches(this, sb)) {return;}");
                    }
                }
            };
        }
    }
    
    public static boolean RenderingBranches(GridCardSelectScreen _inst, SpriteBatch sb) {
        AbstractCard card = GetHoveredCard();
        if (_inst.forUpgrade && card instanceof BranchableUpgradeCard && ((BranchableUpgradeCard) card).canBranch()) {
            card.current_x = Settings.WIDTH * 0.35F;
            card.current_y = Settings.HEIGHT / 2F;
            card.target_x = Settings.WIDTH * 0.35F;
            card.target_y = Settings.HEIGHT / 2F;
            card.drawScale = 0.9F;
            card.render(sb);
            card.updateHoverLogic();
            //_inst.upgradePreviewCard = null; // TODO: Not allowed, causing a npe
            if (Branches[Current].hb.hovered)
                Branches[Current].drawScale = 1F;
            else
                Branches[Current].drawScale = 0.8F;
            Branches[Current].current_x = Settings.WIDTH * 0.64F;
            Branches[Current].current_y = Settings.HEIGHT / 2F;
            Branches[Current].target_x = Settings.WIDTH * 0.64F;
            Branches[Current].target_y = Settings.HEIGHT / 2F;
            Branches[Current].transparency = 1F;
            Branches[Current].render(sb);
            Branches[Current].updateHoverLogic();
            Branches[Current].renderCardTip(sb);
            Branches[Current].beginGlowing();
            if (Prev > -1 && Branches[Prev] != null) {
                Branches[Prev].drawScale = 0.65F;
                Branches[Prev].current_x = Settings.WIDTH * 0.64F;
                Branches[Prev].current_y = Settings.HEIGHT * 0.8F;
                Branches[Prev].target_x = Settings.WIDTH * 0.64F;
                Branches[Prev].target_y = Settings.HEIGHT * 0.8F;
                Branches[Prev].transparency = 0.85F;
                Branches[Prev].targetTransparency = 0.85F;
                Branches[Prev].render(sb);
                Branches[Prev].updateHoverLogic();
                Branches[Prev].renderCardTip(sb);
            }
            if (Next > -1 && Branches[Next] != null) {
                Branches[Next].drawScale = 0.65F;
                Branches[Next].current_x = Settings.WIDTH * 0.64F;
                Branches[Next].current_y = Settings.HEIGHT * 0.18F;
                Branches[Next].target_x = Settings.WIDTH * 0.64F;
                Branches[Next].target_y = Settings.HEIGHT * 0.18F;
                Branches[Next].transparency = 0.85F;
                Branches[Next].targetTransparency = 0.85F;
                Branches[Next].render(sb);
                Branches[Next].updateHoverLogic();
                Branches[Next].renderCardTip(sb);
            }
            if (Prev <= -1 && Next <= -1)
                LMDebug.Log(card.name + " should be branchable but has no any branches available");
            if (!PeekButton.isPeeking && (_inst.forUpgrade || _inst.forTransform || _inst.forPurge
                    || _inst.isJustForConfirming || _inst.anyNumber)) {
                _inst.confirmButton.render(sb);
            }
            _inst.peekButton.render(sb);
            CardGroup targetGroup = ReflectionHacks.getPrivate(_inst, GridCardSelectScreen.class, "targetGroup");
            String tipMsg = ReflectionHacks.getPrivate(_inst, GridCardSelectScreen.class, "tipMsg");
            if ((!_inst.isJustForConfirming || targetGroup.size() > 5) && !PeekButton.isPeeking) {
                FontHelper.renderDeckViewTip(sb, tipMsg, 96F * Settings.scale, Settings.CREAM_COLOR);
            }
            return true;
        }
        return false;
    }
    
    public static void RenderPrevAndNextArrows(GridCardSelectScreen _inst, SpriteBatch sb) {
        if (OptFields.NextBtn.get(_inst) != null) {
            Hitbox box = OptFields.NextBtn.get(_inst);
            sb.setColor(Color.WHITE.cpy());
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
        if (OptFields.PrevBtn.get(_inst) != null) {
            Hitbox box = OptFields.PrevBtn.get(_inst);
            sb.setColor(Color.WHITE.cpy());
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
    
    @SpirePatch(clz = GridCardSelectScreen.class, method = "renderArrows")
    public static class RenderSelectArrows {
        @SpirePostfixPatch
        public static void Postfix(GridCardSelectScreen _inst, SpriteBatch sb) {
            if (OptFields.SelectingBranch.get(_inst)) {
                RenderPrevAndNextArrows(_inst, sb);
            }
        }
    }
    
    @SpirePatch(clz = GridCardSelectScreen.class, method = "cancelUpgrade")
    public static class CancelUpgradeCheck {
        @SpirePrefixPatch
        public static void Prefix(GridCardSelectScreen _inst) {
            OptFields.SelectingBranch.set(_inst, false);
            //HandOptFields.ForBranchingUpgrades.set(_inst, false);
            CurrBranch = -1;
            Current = -1;
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "closeCurrentScreen")
    public static class CloseScreeenCheck {
        @SpirePrefixPatch
        public static void Prefix() {
            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.GRID
                    && OptFields.SelectingBranch.get(AbstractDungeon.gridSelectScreen)) {
                OptFields.SelectingBranch.set(AbstractDungeon.gridSelectScreen, false);
                //HandOptFields.ForBranchingUpgrades.set(_inst, false);
                CurrBranch = -1;
                Current = -1;
            }
        }
    }

    @SpirePatch(clz = GridCardSelectScreen.class, method = "update")
    public static class ConfirmBranchingCheck {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(GridCardSelectScreen _inst) {
            AbstractCard card = GetHoveredCard();
            if (card instanceof BranchableUpgradeCard && OptFields.SelectingBranch.get(_inst) && CurrBranch >= 0) {
                LMDebug.Log("Final chosen branch: " + CurrBranch);
                ((BranchableUpgradeCard) card).setChosenBranch(CurrBranch);
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

    public static AbstractCard GetHoveredCard() {
        try {
            Field hoveredCard = AbstractDungeon.gridSelectScreen.getClass().getDeclaredField("hoveredCard");
            hoveredCard.setAccessible(true);
            return (AbstractCard) hoveredCard.get(AbstractDungeon.gridSelectScreen);
        } catch (Exception e) {
            LMDebug.Log("Failed to catch that shit card in upgrading");
            return null;
        }
    }
    
    @SpirePatch(clz = AbstractCard.class, method = "makeStatEquivalentCopy")
    public static class FixEverythingAboutFuckingShitMakeStatEquivalentCopyNoneShouldUseRawOrAnythingToCompletelyOverrideThisDamnMethod {
        @SpireInsertPatch(locator = Locator.class, localvars = {"card"})
        public static void Insert(AbstractCard _inst, AbstractCard card) {
            if (_inst instanceof BranchableUpgradeCard) {
                int branch = ((BranchableUpgradeCard) _inst).chosenBranch();
                if (branch > -1) {
                    CardBranchField.ChosenBranch.set(card, branch);
                }
            }
        }
        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "timesUpgraded");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
    
    /*@SpirePatch(clz = CardGroup.class, method = "getCardDeck")
    public static class SaveChosenBranchPatch {
        @SpireInstrumentPatch
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                @Override
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(ArrayList.class.getName()) && m.getMethodName().equals("add")) {
                        m.replace("if (!" + BranchableUpgradePatch.class.getName()
                                + ".SaveBranchUpgrade(card, retVal)) {$_ = $proceed($$);}");
                    }
                }
            };
        }
    }
    
    public static boolean SaveBranchUpgrade(AbstractCard card, ArrayList<CardSave> saves) {
        if (card instanceof BranchableUpgradeCard) {
            CardSave save = new BranchableCardSave(card.cardID, card.timesUpgraded, card.misc, 
                    ((BranchableUpgradeCard) card).chosenBranch());
            saves.add(save);
            return true;
        }
        return false;
    }

    @SpirePatch(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class LoadChosenBranchPatch {
        @SpireInsertPatch(rloc = 9, localvars = {"retVal"})
        public static void Insert(String key, int upgradeTime, int misc, AbstractCard retVal) {
            if (retVal instanceof BranchableUpgradeCard && CardCrawlGame.saveFile != null) {
                for (CardSave save : CardCrawlGame.saveFile.cards) {
                    if (save.id.equals(key) && save instanceof BranchableCardSave) {
                        int branch = ((BranchableCardSave) save).chosenBranch;
                        CardBranchField.ChosenBranch.set(retVal, branch);
                        break;
                    }
                }
            }
        }
    }*/
    
    static final Map<UUID, Integer> timesMap = new HashMap<>();
    
    @SpirePatch(clz = CardCrawlGame.class, method = "loadPlayerSave")
    public static class PreLoadTimesMapPatch {
        @SpirePrefixPatch
        public static void Prefix(CardCrawlGame _inst, AbstractPlayer p) {
            timesMap.clear();
        }
    }
    
    @SpirePatch(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
    public static class LoadChosenBranchPatch {
        @SpireInsertPatch(rloc = 9, localvars = {"retVal"})
        public static void Insert(String key, @ByRef int[] times, int misc, AbstractCard retVal) {
            if (retVal instanceof BranchableUpgradeCard && times[0] > 0 && CardCrawlGame.saveFile != null) {
                int tU = times[0];
                LMDebug.Log("Preloading " + retVal.name + "'s upgraded times: " + tU);
                timesMap.put(retVal.uuid, tU);
                times[0] = 0;
            }
        }
    }
    
    public static int GetBranchUpgradedTimes(AbstractCard card) {
        if (timesMap.containsKey(card.uuid))
            return timesMap.get(card.uuid);
        return 0;
    }
}