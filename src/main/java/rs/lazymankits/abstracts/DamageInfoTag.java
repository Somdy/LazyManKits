package rs.lazymankits.abstracts;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import rs.lazymankits.LMDebug;

import java.util.*;

public class DamageInfoTag {
    public static final DamageInfoTag BLOCK_IGNORED = new DamageInfoTag("LMK_BLOCK_IGNORED");
    
    public static final String SOURCE_TAG_ID = "LMK_DamageInfoSourceTag";
    
    public final String ID;
    private Map<String, String> uniMap = new HashMap<>();
    private Map<String, Object> objMap = new HashMap<>();
    
    /**
     * @param ID only ID specifies a tag, meaning two tags with the same ID would be considered as the same tag
     */
    public DamageInfoTag(String ID) {
        this.ID = ID;
    }
    
    public DamageInfoTag putBool(String key, boolean value) {
        uniMap.put(key, Boolean.toString(value));
        return this;
    }
    
    public boolean getBool(String key) {
        return Boolean.parseBoolean(uniMap.get(key));
    }
    
    public DamageInfoTag putInt(String key, int value) {
        uniMap.put(key, Integer.toString(value));
        return this;
    }
    
    public int getInt(String key) {
        try {
            return Integer.parseInt(uniMap.get(key));
        } catch (Exception e) {
            log("no value associated with key [" + key + "] or the value does not contain a parsable integer, returning 0");
            e.printStackTrace();
            return 0;
        }
    }
    
    public DamageInfoTag putFloat(String key, float value) {
        uniMap.put(key, Float.toString(value));
        return this;
    }
    
    public float getFloat(String key) {
        try {
            return Float.parseFloat(uniMap.get(key));
        } catch (Exception e) {
            log("no value associated with key [" + key + "] or the value does not contain a parsable float, returning 0F");
            e.printStackTrace();
            return 0F;
        }
    }
    
    public DamageInfoTag putString(String key, String value) {
        uniMap.put(key, value);
        return this;
    }
    
    public String getString(String key) {
        return uniMap.get(key);
    }
    
    public DamageInfoTag putObj(String key, Object value) {
        objMap.put(key, value);
        return this;
    }
    
    public Object getObj(String key) {
        return objMap.get(key);
    }
    
    /**
     * returns the object associated with key [source] if the key is valid.
     * @return the object associated with [source]
     * @apiNote use {@link DamageInfoTag#SourceTag(Object)} to get a damage info tag with the key [source] associated an object
     */
    public Object getSource() {
        return objMap.get("source");
    }
    
    public DamageInfoTag cpy() {
        DamageInfoTag tag = new DamageInfoTag(ID);
        tag.uniMap = new HashMap<>(uniMap);
        tag.objMap = new HashMap<>(objMap);
        return tag;
    }
    
    protected void log(Object what) {
        LMDebug.Log(this, what);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DamageInfoTag that = (DamageInfoTag) o;
        return Objects.equals(ID, that.ID);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
    
    @Override
    public String toString() {
        return "DamageInfoTag{ ID = " + ID + ", uniMap size = " + uniMap.size() + ", objMap size = " + objMap.size() + " }";
    }
    
    public static DamageInfoTag SourceTag(Object obj) {
        return new DamageInfoTag(SOURCE_TAG_ID).putObj("source", obj);
    }
    
    @SpirePatch(clz = DamageInfo.class, method = SpirePatch.CLASS)
    public static class DamageInfoField {
        public static SpireField<List<DamageInfoTag>> infoTags = new SpireField<>(ArrayList::new);
    }
    
//    public static void main(String[] args) {
//        String hex = Integer.toHexString(0xFFBD00);
//        System.out.println(hex);
//    }
}