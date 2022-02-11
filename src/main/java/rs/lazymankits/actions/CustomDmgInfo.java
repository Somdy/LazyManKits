package rs.lazymankits.actions;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;

public class CustomDmgInfo extends DamageInfo {
    public DamageSource source;
    
    public CustomDmgInfo(@NotNull DamageSource source, int base, DamageType type) {
        super(source.getSource(), base, type);
        this.source = source;
    }

    @NotNull
    public static CustomDmgInfo[] createInfoArray(CustomDmgInfo baseInfo) {
        return createInfoArray(baseInfo, false);
    }

    @NotNull
    public static CustomDmgInfo[] createInfoArray(CustomDmgInfo baseInfo, boolean pureDmg) {
        CustomDmgInfo[] infos = new CustomDmgInfo[AbstractDungeon.getMonsters().monsters.size()];
        for (int i = 0; i < infos.length; i++) {
            CustomDmgInfo tmp = new CustomDmgInfo(baseInfo.source, baseInfo.base, baseInfo.type);
            AbstractMonster m = AbstractDungeon.getMonsters().monsters.get(i);
            //LMDebug.Log("Initial info for " + i + " at damage: " + baseInfo.base
                    //+ " has vul?" + m.powers.stream().anyMatch(p -> p.ID.equals(VulnerablePower.POWER_ID)));
            if (!pureDmg)
                tmp.applyPowers(baseInfo.owner, m);
            infos[i] = tmp;
            //LMDebug.Log("Final info for " + i + " at final damage: " + tmp.base
                    //+ " has vul?" + m.powers.stream().anyMatch(p -> p.ID.equals(VulnerablePower.POWER_ID)));
        }
        return infos;
    }
}