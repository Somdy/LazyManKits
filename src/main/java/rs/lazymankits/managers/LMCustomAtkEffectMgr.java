package rs.lazymankits.managers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import org.jetbrains.annotations.Nullable;
import rs.lazymankits.interfaces.TripleUniMap;

import java.util.ArrayList;
import java.util.List;

public class LMCustomAtkEffectMgr {
    private static final List<AtkEffectSet> customs = new ArrayList<>();

    public static void RegisterEffect(AbstractGameAction.AttackEffect effect, TextureAtlas.AtlasRegion img, String soundID) {
        customs.add(new AtkEffectSet(effect, img, soundID));
    }

    public static boolean Contains(AbstractGameAction.AttackEffect effect) {
        return customs.stream().anyMatch(e -> e.identify(effect));
    }

    @Nullable
    public static TextureAtlas.AtlasRegion GetImg(AbstractGameAction.AttackEffect effect) {
        if (Contains(effect)) {
            for (AtkEffectSet set : customs) {
                if (set.identify(effect))
                    return set.accept(effect);
            }
        }
        return null;
    }

    @Nullable
    public static String GetSound(AbstractGameAction.AttackEffect effect) {
        if (Contains(effect)) {
            for (AtkEffectSet set : customs) {
                if (set.identify(effect))
                    return set.find(effect);
            }
        }
        return null;
    }

    private static class AtkEffectSet implements TripleUniMap<AbstractGameAction.AttackEffect, TextureAtlas.AtlasRegion, String> {
        AbstractGameAction.AttackEffect effect;
        TextureAtlas.AtlasRegion region;
        String soundID;

        public AtkEffectSet(AbstractGameAction.AttackEffect effect, TextureAtlas.AtlasRegion region, String soundID) {
            this.effect = effect;
            this.region = region;
            this.soundID = soundID;
        }

        @Override
        public TextureAtlas.AtlasRegion accept(AbstractGameAction.AttackEffect effect) {
            if (identify(effect))
                return region;
            return null;
        }

        @Override
        public String find(AbstractGameAction.AttackEffect effect) {
            if (identify(effect))
                return soundID;
            return null;
        }

        @Override
        public boolean identify(AbstractGameAction.AttackEffect effect) {
            return this.effect == effect;
        }
    }
}