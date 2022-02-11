package rs.lazymankits.vfx.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.abstracts.LMCustomGameEffect;

public class CardAboveCreatureEffect extends LMCustomGameEffect {
    private final AbstractCard card;
    private final float x;
    private float y;

    public CardAboveCreatureEffect(AbstractCreature target, @NotNull AbstractCard card) {
        this.card = card.makeStatEquivalentCopy();
        this.card.drawScale = 0.01F;
        this.card.targetDrawScale = 0.45F;
        if (target instanceof AbstractMonster)
            this.card.targetDrawScale = 0.25F;
        x = target.hb.cX - target.animX;
        y = target.hb.cY + target.hb.height / 2F - target.animY;
        this.card.current_x = x;
        this.card.current_y = y;
        duration = 1.5F;
        startingDuration = 1.5F;
    }

    public void update() {
        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0.6F) {
            card.fadingOut = true;
        }
        card.update();
        y += Gdx.graphics.getDeltaTime() * 50.0F * Settings.scale;
        card.target_x = x;
        card.target_y = y;
        if (duration < 0.0F) {
            isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!isDone) {
            card.render(sb);
        }
    }

    @Override
    public void dispose() {

    }
}