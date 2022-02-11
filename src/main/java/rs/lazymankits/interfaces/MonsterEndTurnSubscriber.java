package rs.lazymankits.interfaces;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface MonsterEndTurnSubscriber extends LMSubscriberInterface {
    void receiveOnMonsterTurnEnds(AbstractMonster m);
}