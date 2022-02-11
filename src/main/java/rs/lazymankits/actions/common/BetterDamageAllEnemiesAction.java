package rs.lazymankits.actions.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.LMCustomGameAction;
import rs.lazymankits.actions.CustomDmgInfo;
import rs.lazymankits.actions.DamageSource;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class BetterDamageAllEnemiesAction extends LMCustomGameAction {
    public CustomDmgInfo[] infos;
    private CustomDmgInfo baseInfo;
    private boolean firstFrame;
    private boolean initializeInfos;
    private boolean pureDmg;
    private BiFunction<AbstractCreature, CustomDmgInfo, CustomDmgInfo> manipulate;
    private Consumer<AbstractCreature> dowhat;

    public BetterDamageAllEnemiesAction(CustomDmgInfo[] infos, AttackEffect effect, boolean pureDmg, boolean isFast, 
                                        Consumer<AbstractCreature> dowhat) {
        this.infos = infos;
        this.baseInfo = infos[0];
        this.firstFrame = true;
        this.initializeInfos = false;
        this.attackEffect = effect;
        this.pureDmg = pureDmg;
        this.dowhat = dowhat;
        this.manipulate = null;
        duration = isFast ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST;
    }

    public BetterDamageAllEnemiesAction(CustomDmgInfo info, AttackEffect effect, boolean isFast,
                                        Consumer<AbstractCreature> dowhat) {
        this.infos = null;
        this.baseInfo = info;
        this.firstFrame = true;
        this.initializeInfos = true;
        this.attackEffect = effect;
        this.pureDmg = true;
        this.dowhat = dowhat;
        this.manipulate = null;
        duration = isFast ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST;
    }

    public BetterDamageAllEnemiesAction(CustomDmgInfo info, AttackEffect effect, boolean isFast) {
        this.infos = null;
        this.baseInfo = info;
        this.firstFrame = true;
        this.initializeInfos = true;
        this.attackEffect = effect;
        this.pureDmg = true;
        this.dowhat = null;
        this.manipulate = null;
        duration = isFast ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST;
    }

    public BetterDamageAllEnemiesAction(CustomDmgInfo info, AttackEffect effect, Consumer<AbstractCreature> dowhat) {
        this.infos = null;
        this.baseInfo = info;
        this.firstFrame = true;
        this.initializeInfos = true;
        this.attackEffect = effect;
        this.pureDmg = true;
        this.dowhat = dowhat;
        this.manipulate = null;
        duration = Settings.ACTION_DUR_XFAST;
    }
    
    public BetterDamageAllEnemiesAction(int[] multiDamage, DamageSource source, DamageInfo.DamageType type,
                                        AttackEffect effect, Consumer<AbstractCreature> dowhat) {
        loadInfosFromMatrix(multiDamage, source, type);
        this.firstFrame = true;
        this.initializeInfos = false;
        this.attackEffect = effect;
        this.dowhat = dowhat;
        this.manipulate = null;
        duration = Settings.ACTION_DUR_XFAST;
    }
    
    public BetterDamageAllEnemiesAction(int[] multiDamage, DamageSource source, DamageInfo.DamageType type,
                                        AttackEffect effect) {
        loadInfosFromMatrix(multiDamage, source, type);
        this.firstFrame = true;
        this.initializeInfos = false;
        this.attackEffect = effect;
        this.dowhat = null;
        this.manipulate = null;
        duration = Settings.ACTION_DUR_XFAST;
    }
    
    private void loadInfosFromMatrix(int[] multiDamage, DamageSource source, DamageInfo.DamageType type) {
        infos = new CustomDmgInfo[multiDamage.length];
        for (int i = 0; i < multiDamage.length; i++) {
            infos[i] = new CustomDmgInfo(source, multiDamage[i], type);
        }
    }
    
    public BetterDamageAllEnemiesAction setPureDmg() {
        pureDmg = true;
        return this;
    }
    
    public BetterDamageAllEnemiesAction applyPowers() {
        pureDmg = false;
        return this;
    }
    
    public BetterDamageAllEnemiesAction doAction(Consumer<AbstractCreature> dowhat) {
        this.dowhat = dowhat;
        return this;
    }
    
    public BetterDamageAllEnemiesAction manipulateInfo(BiFunction<AbstractCreature, CustomDmgInfo, CustomDmgInfo> manipulate) {
        this.manipulate = manipulate;
        return this;
    }

    @Override
    public void update() {
        if (firstFrame) {
            int count = AbstractDungeon.getMonsters().monsters.size();
            if (initializeInfos || infos == null) {
                //LMDebug.Log("Initializing info: " + baseInfo.base);
                infos = CustomDmgInfo.createInfoArray(baseInfo, pureDmg);
            }
            boolean musicPlayed = false;
            for (int i = 0; i < count; i++) {
                AbstractMonster m  = AbstractDungeon.getMonsters().monsters.get(i);
                if (m != null && !m.isDeadOrEscaped()) {
                    if (musicPlayed)
                        effectToList(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, attackEffect, true));
                    else {
                        musicPlayed = true;
                        effectToList(new FlashAtkImgEffect(m.hb.cX, m.hb.cY, attackEffect));
                    }
                }
            }
            firstFrame = false;
        }
        tickDuration();
        if (isDone) {
            int[] damage = new int[infos.length];
            for (int i = 0; i < infos.length; i++) {
                if (manipulate != null)
                    infos[i] = manipulate.apply(AbstractDungeon.getMonsters().monsters.get(i), infos[i]);
                damage[i] = infos[i].output;
            }
            for (AbstractPower p : cpr().powers) {
                p.onDamageAllEnemies(damage);
            }
            int count = AbstractDungeon.getMonsters().monsters.size();
            for (int i = 0; i < count; i++) {
                AbstractMonster m  = AbstractDungeon.getMonsters().monsters.get(i);
                if (m != null && !m.isDeadOrEscaped()) {
                    if (attackEffect == AttackEffect.POISON) {
                        m.tint.color.set(Color.CHARTREUSE.cpy());
                        m.tint.changeColor(Color.WHITE.cpy());
                    } else if (attackEffect == AttackEffect.FIRE) {
                        m.tint.color.set(Color.RED.cpy());
                        m.tint.changeColor(Color.WHITE.cpy());
                    }
                    final CustomDmgInfo info = i > infos.length - 1 ? infos[infos.length - 1] : infos[i];
                    m.damage(info);
                    if (infos[i].source.isLeech() && m.lastDamageTaken > 0) {
                        int healAmt = MathUtils.floor(m.lastDamageTaken * infos[i].source.leechFactor());
                        addToTop(new HealAction(infos[i].source.getSource(), infos[i].source.getSource(), healAmt));
                    }
                    if (dowhat != null) {
                        dowhat.accept(m);
                    }
                }
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
            if (!Settings.FAST_MODE) {
                addToTop(new WaitAction(0.1F));
            }
        }
    }
}