package rs.lazymankits.listeners;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.lazymankits.LMDebug;
import rs.lazymankits.LManager;
import rs.lazymankits.interfaces.TripleUniMap;
import rs.lazymankits.listeners.tools.PowerMplr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ApplyPowerListener {
    private static int INDEX = 1;
    private static List<CustomPowerMplr> manipulators;
    
    public static void Initialize() {
        if (LManager.EverythingReady()) return;
        manipulators = new ArrayList<>();
    }

    public static int AddNewManipulator(int ID, int Rank, Predicate<PowerMplr> effective, PowerMplr mplr) {
        int internalID = secureID(ID);
        if (!registerMplrWithAbsenceCheck(internalID, Rank, effective, mplr)) {
            LMDebug.Log(ApplyPowerListener.class,
                    "PowerMplr-" + internalID + " failed to register in. It will not have any effects.");
            return -1;
        }
        return internalID;
    }

    public static boolean RemoveManipulator(int ID) {
        if (contains(ID)) {
            manipulators.removeIf(m -> m.getID() == ID);
            LMDebug.deLog(ApplyPowerListener.class, "Removing PowerMplr-"  + ID + " from listener list.");
        }
        return manipulators.stream().noneMatch(m -> m.getID() == ID);
    }
    
    private static int secureID(int ID) {
        int internalID = ID * 10 + INDEX;
        INDEX++;
        return internalID;
    }

    private static boolean registerMplrWithAbsenceCheck(int ID, int Rank, Predicate<PowerMplr> effective, PowerMplr mplr) {
        if (contains(ID)) {
            boolean shouldReplace = getMplr(ID).getRank() > Rank;
            if (shouldReplace) {
                int index = manipulators.indexOf(getMplr(ID));
                manipulators.removeIf(m -> m.getID() == ID);
                if (index < 0) {
                    LMDebug.deLog(ApplyPowerListener.class, "Missing old PowerMplr-" + ID + ", adding a new one.");
                    manipulators.add(new CustomPowerMplr(ID, Rank, effective, mplr));
                } else {
                    manipulators.set(index, new CustomPowerMplr(ID, Rank, effective, mplr));
                    LMDebug.Log(ApplyPowerListener.class, "PowerMplr-" + ID + " has been replaced by a new one.");
                }
            }
        } else {
            manipulators.add(new CustomPowerMplr(ID, Rank, effective, mplr));
            LMDebug.Log("PowerMplr-" + ID + " has been applied.");
        }
        return true;
    }

    private static CustomPowerMplr getMplr(int ID) {
        return manipulators.stream().filter(m -> m.RankID.containsKey(ID)).findFirst().orElse(null);
    }

    private static boolean contains(int ID) {
        if (manipulators.isEmpty())
            return false;
        for (CustomPowerMplr mplr : manipulators) {
            return mplr.getID() == ID;
        }
        return false;
    }
    
    public static void ClearOnBattleStart() {
        manipulators.clear();
        INDEX = 1;
        LMDebug.deLog(ApplyPowerListener.class, "Clearing power manipulators");
    }

    public static void ClearPostBattle() {
        manipulators.clear();
        INDEX = 1;
    }

    public static AbstractPower OnApplyPower(@NotNull AbstractPower power, AbstractCreature target, AbstractCreature source) {
        String Power_ID = power.ID;
        if (!manipulators.isEmpty()) {
            List<PowerMplr> tmp = new ArrayList<>();
            manipulators.stream().filter(m -> m.effective.test(m.mplr)).forEach(m -> {
                if (!tmp.contains(m.mplr))
                    tmp.add(m.mplr);
            });
            if (!tmp.isEmpty()) {
                for (PowerMplr mplr : tmp) {
                    power = mplr.manipulate(power, target, source);
                    if (power == null) {
                        LMDebug.deLog(ApplyPowerListener.class, Power_ID + " was cancled.");
                        break;
                    }
                }
            }
        }
        return power;
    }

    private static class CustomPowerMplr implements TripleUniMap<PowerMplr, Map<Integer, Integer>, Predicate<PowerMplr>> {
        PowerMplr mplr;
        Map<Integer, Integer> RankID;
        Predicate<PowerMplr> effective;
        private final int ID;
        private final int Rank;

        public CustomPowerMplr(int ID, int Rank, Predicate<PowerMplr> effective, PowerMplr mplr) {
            this.ID = ID;
            this.Rank = Rank;
            RankID = new HashMap<>();
            RankID.put(ID, Rank);
            this.effective = effective;
            this.mplr = mplr;
        }

        @Override
        public Map<Integer, Integer> accept(PowerMplr powerMplr) {
            if (identify(powerMplr))
                return RankID;
            return null;
        }

        @Override
        public Predicate<PowerMplr> find(PowerMplr powerMplr) {
            if (identify(powerMplr))
                return effective;
            return null;
        }

        @Nullable
        @Contract(pure = true)
        public CustomPowerMplr get(Integer ID) {
            return this.ID == ID ? this : null;
        }

        public int getID() {
            return ID;
        }

        public int getRank() {
            return Rank;
        }

        @Override
        public boolean identify(PowerMplr powerMplr) {
            return this.mplr == powerMplr;
        }
    }
}