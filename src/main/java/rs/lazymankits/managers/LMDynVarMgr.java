package rs.lazymankits.managers;

import rs.lazymankits.LManager;
import rs.lazymankits.abstracts.LMDynamicVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LMDynVarMgr {
    private static Map<String, LMDynamicVar> relicDynamicVarMap;

    public static void Initialize() {
        if (LManager.EverythingReady()) return;
        relicDynamicVarMap = new HashMap<>();
    }
    
    public static void AddDynamicVar(LMDynamicVar var) {
        relicDynamicVarMap.put(var.key(), var);
    }
    
    public static Map<String, LMDynamicVar> GetRelicDynVarMap() {
        return relicDynamicVarMap;
    }
    
    public static Optional<LMDynamicVar> GetRelicDynVar(String key) {
        return Optional.ofNullable(relicDynamicVarMap.get(key));
    }
}