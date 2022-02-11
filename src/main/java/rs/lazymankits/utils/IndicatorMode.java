package rs.lazymankits.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import javassist.CtBehavior;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.actions.utility.DelayAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class IndicatorMode implements LMGameGeneralUtils {
    private static List<IndicatorMode> invoker = new ArrayList<>();
    private static List<AbstractCreature> inopts = new ArrayList<>();
    private static boolean drawLine;
    private static boolean isHidden;
    private IndicatorInvoker register;
    private AbstractCreature hoveredCreature;
    private Predicate<AbstractCreature> predicator;
    private Consumer<AbstractCreature> dowhat;
    private Vector2 controlPoint;
    private Vector2 start;
    private Vector2[] points;
    private Color pointerColor;
    private float arrowScale;
    private float arrowScaleTimer;
    private float startX;
    private float startY;

    public IndicatorMode(IndicatorInvoker register) {
        this.register = register;
    }

    public static void register(IndicatorMode mode) {
        LMDebug.Log("Registering new invoker...");
        invoker.add(mode);
    }

    private void init() {
        points = new Vector2[20];
        drawLine = true;
        isHidden = false;
        inopts.clear();
        GameCursor.hidden = true;
        for (int i = 0; i < points.length; i++) points[i] = new Vector2();
    }

    public void close() {
        isHidden = true;
        drawLine = false;
        clearPostUsed();
    }

    public void active(@NotNull Vector2 start, Consumer<AbstractCreature> dowhat) {
        active(start, dowhat, c -> true, new Color(1.0F, 0.2F, 0.3F, 1.0F));
    }

    public void active(@NotNull Vector2 start, Consumer<AbstractCreature> dowhat, Predicate<AbstractCreature> predicator) {
        active(start, dowhat, predicator, new Color(1.0F, 0.2F, 0.3F, 1.0F));
    }

    public void active(@NotNull Vector2 start, Consumer<AbstractCreature> dowhat, Predicate<AbstractCreature> predicator,
                       Color pointerColor) {
        LMDebug.Log("Invoking indicator at " + start.x + ", " + start.y);
        this.start = start;
        startX = start.x;
        startY = start.y;
        this.dowhat = dowhat;
        this.predicator = predicator;
        this.pointerColor = pointerColor;
        init();
    }

    public void execute(@NotNull AbstractCreature t) {
        LMDebug.Log("Executing final action at " + t.name);
        dowhat.accept(t);
        drawLine = false;
        clearPostUsed();
    }

    private void clearPostUsed() {
        AbstractDungeon.actionManager.addToTop(new DelayAction(() -> {
            invoker.clear();
            inopts.clear();
        }));
    }

    private void updateInvokerMode() {
        if (InputHelper.justClickedRight || AbstractDungeon.isScreenUp
                || InputHelper.mY > Settings.HEIGHT - scale(80F) || AbstractDungeon.player.hoveredCard != null
                || InputHelper.mY < scale(140F)) {
            GameCursor.hidden = false;
            close();
        }
        hoveredCreature = null;
        List<AbstractCreature> candicates = new ArrayList<>(AbstractDungeon.getMonsters().monsters);
        candicates.add(cpr());
        candicates.removeIf(c -> {
            if (!predicator.test(c)) {
                inopts.add(c);
                return true;
            }
            return false;
        });
        for (AbstractCreature c : candicates) {
            if (!c.isDying && c.hb.hovered) {
                hoveredCreature = c;
                break;
            }
        }
        if (InputHelper.justClickedLeft) {
            InputHelper.justClickedLeft = false;
            if (hoveredCreature != null) {
                execute(hoveredCreature);
            }
            GameCursor.hidden = false;
            close();
        }
    }

    private void renderPointer(SpriteBatch sb) {
        float x = InputHelper.mX;
        float y = InputHelper.mY;
        controlPoint = new Vector2(startX - (x - startX) / 4.0F, startY + (y - startY - scale(40F)) / 2.0F);
        if (hoveredCreature == null) {
            arrowScale = Settings.scale;
            arrowScaleTimer = 0.0F;
            sb.setColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
        } else {
            arrowScaleTimer += Gdx.graphics.getDeltaTime();
            if (arrowScaleTimer > 1.0F) {
                arrowScaleTimer = 1.0F;
            }
            arrowScale = Interpolation.elasticOut.apply(Settings.scale, scale(1.2F), arrowScaleTimer);
            sb.setColor(pointerColor);
        }
        Vector2 tmp = new Vector2(controlPoint.x - x, controlPoint.y - y);
        tmp.nor();
        drawCurvedLine(sb, start, new Vector2(x, y), controlPoint);
        sb.draw(ImageMaster.TARGET_UI_ARROW, x - 128F, y - 128F, 128.0F, 128.0F,
                256.0F, 256.0F, arrowScale, arrowScale, tmp.angle() + 90.0F,
                0, 0, 256, 256, false, false);
    }

    private void drawCurvedLine(SpriteBatch sb, Vector2 start, Vector2 end, Vector2 control) {
        float radius = 7.0F * Settings.scale;
        for (int i = 0; i < points.length - 1; i++) {
            float angle;
            points[i] = Bezier.quadratic(points[i], i / 20.0F, start, control, end, new Vector2());
            radius += scale(0.4F);
            if (i != 0) {
                Vector2 tmp = new Vector2((points[i - 1]).x - (points[i]).x, (points[i - 1]).y - (points[i]).y);
                angle = tmp.nor().angle() + 90.0F;
            } else {
                Vector2 tmp = new Vector2(controlPoint.x - (points[i]).x, controlPoint.y - (points[i]).y);
                angle = tmp.nor().angle() + 270.0F;
            }
            sb.draw(ImageMaster.TARGET_UI_CIRCLE, points[i].x - 64.0F, points[i].y - 64.0F,
                    64.0F, 64.0F, 128.0F, 128.0F, radius / 18.0F, radius / 18.0F, angle,
                    0, 0, 128, 128, false, false);
        }
    }

    public void render(SpriteBatch sb) {
        if (!isHidden) {
            renderPointer(sb);
            if (hoveredCreature != null) {
                hoveredCreature.renderReticle(sb);
            }
        }
    }

    public void update() {
        if (!isHidden)
            updateInvokerMode();
    }

    private AbstractPlayer cpr() {
        return AbstractDungeon.player;
    }

    public static class IndicatorUpdateAndRenderPatch {
        @SpirePatch( clz = AbstractDungeon.class, method = "render")
        public static class InsertRender {

            @SpireInsertPatch(locator = RenderLocator.class)
            public static void Insert(Object _obj, SpriteBatch sb) {
                if (!invoker.isEmpty() && drawLine)
                    invoker.forEach(i -> i.render(sb));
            }
            private static class RenderLocator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                    Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "setColor");
                    return LineFinder.findInOrder(ctMethodToPatch, methodCallMatcher);
                }
            }
        }
        @SpirePatch(clz = AbstractDungeon.class, method = "update")
        public static class InsertPostUpdate {
            @SpireInsertPatch(locator = UpdateLocator.class)
            public static void Insert(Object __obj) {
                if (!invoker.isEmpty() && drawLine)
                    invoker.forEach(IndicatorMode::update);
            }
            private static class UpdateLocator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                    Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(OverlayMenu.class, "update");
                    return LineFinder.findInOrder(ctMethodToPatch, methodCallMatcher);
                }
            }
        }
    }

    public static class DarkenInoptionalCrtPatch {
        private static final ShaderProgram grayShader = new ShaderProgram(
                Gdx.files.internal("SharedAssets/shaders/invdtshdr/vertexShader.vs"),
                Gdx.files.internal("SharedAssets/shaders/invdtshdr/fragShader.fs"));
        @SpirePatch(clz = AbstractMonster.class, method = "render")
        public static class RenderInoptMonsterPatch {
            @SpirePrefixPatch
            public static void Prefix(AbstractMonster _inst, SpriteBatch sb) {
                if (!_inst.isDeadOrEscaped() && !inopts.isEmpty()) {
                    if (inopts.contains(_inst)) {
                        CardCrawlGame.psb.setShader(grayShader);
                        sb.setShader(grayShader);
                    }
                }
            }
            @SpireInsertPatch(rloc = 53)
            public static void Insert(AbstractMonster _inst, SpriteBatch sb) {
                CardCrawlGame.psb.setShader(null);
                sb.setShader(null);
            }
        }
        @SpirePatch(clz = AbstractPlayer.class, method = "renderPlayerImage")
        public static class RenderInoptPlayerPatch {
            @SpirePrefixPatch
            public static void Prefix(AbstractPlayer _inst, SpriteBatch sb) {
                if ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT || AbstractDungeon.getCurrRoom() instanceof MonsterRoom)
                        && !_inst.isDead) {
                    if (!inopts.isEmpty() && inopts.contains(_inst)) {
                        CardCrawlGame.psb.setShader(grayShader);
                        sb.setShader(grayShader);
                    }
                }
            }
            @SpirePostfixPatch
            public static void Postfix(AbstractPlayer _inst, SpriteBatch sb) {
                CardCrawlGame.psb.setShader(null);
                sb.setShader(null);
            }
        }
        @SpirePatch(clz = AbstractMonster.class, method = "dispose")
        public static class DisposeInoptMonsterPatch {
            @SpirePrefixPatch
            public static void Prefix(AbstractMonster _inst) {
                CardCrawlGame.psb.setShader(null);
            }
        }
        @SpirePatch(clz = AbstractPlayer.class, method = "dispose")
        public static class DisposeInoptPlayerPatch {
            @SpirePrefixPatch
            public static void Prefix(AbstractPlayer _inst) {
                CardCrawlGame.psb.setShader(null);
            }
        }
    }
}