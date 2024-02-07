package rs.lazymankits.patches;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.managers.LMCustomAtkEffectMgr;

import java.lang.reflect.Field;

public class CustomAtkEffectPatch {
    private static Field efct;
    static {
        try {
            efct = FlashAtkImgEffect.class.getDeclaredField("effect");
            efct.setAccessible(true);
        }
        catch (Exception ignored) {}
    }

    @SpirePatch(clz = FlashAtkImgEffect.class, method = "loadImage")
    public static class LoadImagePatch {
        @SpirePrefixPatch
        public static SpireReturn<TextureAtlas.AtlasRegion> Prefix(FlashAtkImgEffect _inst) throws Exception {
            AbstractGameAction.AttackEffect effect = (AbstractGameAction.AttackEffect) efct.get(_inst);
            if (LMCustomAtkEffectMgr.Contains(effect)) {
                return SpireReturn.Return(LMCustomAtkEffectMgr.GetImg(effect));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = FlashAtkImgEffect.class, method = "playSound")
    public static class PlaySoundPatch {
        @SpirePrefixPatch
        public static SpireReturn Prefix(FlashAtkImgEffect _inst, AbstractGameAction.AttackEffect effect) {
            if (LMCustomAtkEffectMgr.Contains(effect)) {
                CardCrawlGame.sound.play(LMCustomAtkEffectMgr.GetSound(effect));
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
