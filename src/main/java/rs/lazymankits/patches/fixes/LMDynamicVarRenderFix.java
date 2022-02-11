package rs.lazymankits.patches.fixes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.SingleRelicViewPopup;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMDynamicVar;
import rs.lazymankits.interfaces.LMDynVarImpler;
import rs.lazymankits.managers.LMDynVarMgr;
import rs.lazymankits.utils.PatternUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

public class LMDynamicVarRenderFix {
    
    public static String ReplaceDynVar(String rawDescription, LMDynVarImpler r) {
        AtomicReference<String> description = new AtomicReference<>(rawDescription);
        Matcher matcher = PatternUtils.Match("\\s(!.\\S+!)\\s", rawDescription);
        while (matcher.find()) {
            String key = matcher.group(2);
            Optional<LMDynamicVar> var = LMDynVarMgr.GetRelicDynVar(key);
            var.ifPresent(v -> {
                StringBuilder replacement = new StringBuilder(v.dynamicValue(r));
                Color color = v.normalColor(r);
                if (v.isModified(r)) {
                    color = v.dynamicValue(r) >= v.baseVaule(r) ? v.increasedColor(r) : v.decreasedColor(r);
                }
                replacement.insert(0, "[#" + color.toString() + "]").append("[]");
                description.set(rawDescription.replace(matcher.group(0), replacement));
            });
        }
        return description.get();
    }
    
    @SpirePatch(clz = SingleRelicViewPopup.class, method = "renderDescription")
    public static class SingleViewPopupFix {
        @SpirePrefixPatch
        public static void Prefix(SingleRelicViewPopup _inst, SpriteBatch sb) {
            try {
                Field relic = SingleRelicViewPopup.class.getDeclaredField("relic");
                relic.setAccessible(true);
                AbstractRelic r = (AbstractRelic) relic.get(_inst);
                if (r instanceof LMDynVarImpler) {
                    r.description = ReplaceDynVar(r.description, (LMDynVarImpler) r);
                }
            } catch (Exception e) {
                LMDebug.Log("Failed to render relic custom variable on single view popup: " + e.getMessage());
            }
        }
    }
    @SpirePatch(clz = AbstractRelic.class, method = "renderBossTip")
    public static class RenderBossTipFix {
        @SpirePrefixPatch
        public static void Prefix(AbstractRelic _inst, SpriteBatch sb) {
            try {
                if (_inst instanceof LMDynVarImpler) {
                    Optional<PowerTip> tip = _inst.tips.stream().filter(t -> t.header.equals(_inst.name)).findFirst();
                    tip.ifPresent(t -> {
                        String rawDescription = t.body;
                        rawDescription = ReplaceDynVar(rawDescription, (LMDynVarImpler) _inst);
                        t.body = rawDescription;
                    });
                }
            } catch (Exception e) {
                LMDebug.Log("Failed to render relic custom variable on boss tip: " + e.getMessage());
            }
        }
    }
    @SpirePatch(clz = AbstractRelic.class, method = "renderTip")
    public static class RenderTipFix {
        @SpirePrefixPatch
        public static void Prefix(AbstractRelic _inst, SpriteBatch sb) {
            try {
                if (_inst instanceof LMDynVarImpler) {
                    Optional<PowerTip> tip = _inst.tips.stream().filter(t -> t.header.equals(_inst.name)).findFirst();
                    tip.ifPresent(t -> {
                        String rawDescription = t.body;
                        rawDescription = ReplaceDynVar(rawDescription, (LMDynVarImpler) _inst);
                        t.body = rawDescription;
                    });
                }
            } catch (Exception e) {
                LMDebug.Log("Failed to render relic custom variable on tip: " + e.getMessage());
            }
        }
    }
}