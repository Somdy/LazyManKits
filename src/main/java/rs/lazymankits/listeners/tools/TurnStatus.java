package rs.lazymankits.listeners.tools;

import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.HashMap;
import java.util.Map;

public class TurnStatus {
    private Map<AbstractCreature, CreatureStatus> statusMap;
    public AbstractCreature target;
    
    public TurnStatus(AbstractCreature who) {
        this.target = who;
        statusMap = new HashMap<>();
        assignCurrentCreatureStatus();
    }
    
    public TurnStatus append(AbstractCreature who) {
        this.target = who;
        if (statusMap == null)
            statusMap = new HashMap<>();
        assignCurrentCreatureStatus();
        return this;
    }
    
    private void assignCurrentCreatureStatus() {
        statusMap.put(target, new CreatureStatus(target));
    }
    
    public CreatureStatus get(AbstractCreature who) {
        if (statusMap.containsKey(who))
            return statusMap.get(who);
        return null;
    }
}