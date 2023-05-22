package rs.lazymankits.utils;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import org.jetbrains.annotations.NotNull;
import rs.lazymankits.LMDebug;
import rs.lazymankits.abstracts.DamageInfoTag;
import rs.lazymankits.actions.CustomDmgInfo;
import rs.lazymankits.actions.DamageSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LMDamageInfoHelper {
    
    public static List<DamageInfoTag> GetInfoTags(DamageInfo info) {
        List<DamageInfoTag> tags = DamageInfoTag.DamageInfoField.infoTags.get(info);
        if (tags == null)
            DamageInfoTag.DamageInfoField.infoTags.set(info, new ArrayList<>());
        return DamageInfoTag.DamageInfoField.infoTags.get(info);
    }
    
    @NotNull
    public static DamageInfo Create(AbstractCreature owner, int base, DamageInfo.DamageType type, DamageInfoTag... tags) {
        DamageInfo info = new DamageInfo(owner, base, type);
        PutTags(info, tags);
        return info;
    }
    
    @NotNull
    public static CustomDmgInfo Create(DamageSource source, int base, DamageInfo.DamageType type, DamageInfoTag... tags) {
        CustomDmgInfo info = new CustomDmgInfo(source, base, type);
        PutTags(info, tags);
        return info;
    }
    
    public static void PutTags(DamageInfo info, DamageInfoTag... tags) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        if (tags != null && tags.length > 0) {
            for (DamageInfoTag t : tags) {
                if (infoTags.contains(t)) {
                    LMDebug.Log("damage info tag [" + t.ID + "] will be replaced by a new one");
                    infoTags.remove(t);
                }
                infoTags.add(t);
            }
        }
    }
    
    public static void RemoveTags(DamageInfo info, String... tagIDs) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        if (!infoTags.isEmpty() && tagIDs != null && tagIDs.length > 0) {
            List<String> IDs = Arrays.asList(tagIDs);
            infoTags.removeIf(t -> IDs.contains(t.ID));
        }
    }
    
    public static void RemoveTags(DamageInfo info, DamageInfoTag... tags) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        if (!infoTags.isEmpty() && tags != null && tags.length > 0) {
            for (DamageInfoTag t : tags) {
                infoTags.remove(t);
            }
        }
    }
    
    public static void ClearTags(DamageInfo info) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        infoTags.clear();
    }
    
    public static boolean HasTag(DamageInfo info, DamageInfoTag tag) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        return infoTags.contains(tag);
    }
    
    public static boolean HasTag(DamageInfo info, String tagID) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        return infoTags.stream().anyMatch(t -> tagID.equals(t.ID));
    }
    
    public static boolean HasAnyTag(DamageInfo info, DamageInfoTag... tags) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        if (!infoTags.isEmpty() && tags != null && tags.length > 0) {
            for (DamageInfoTag t : tags) {
                if (infoTags.contains(t))
                    return true;
            }
        }
        return false;
    }
    
    public static boolean HasAnyTag(DamageInfo info, String... tagIDs) {
        List<DamageInfoTag> infoTags = GetInfoTags(info);
        if (!infoTags.isEmpty() && tagIDs != null && tagIDs.length > 0) {
            List<String> IDs = Arrays.asList(tagIDs);
            return infoTags.stream().anyMatch(t -> IDs.contains(t.ID));
        }
        return false;
    }
}