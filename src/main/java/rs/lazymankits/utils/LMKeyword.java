package rs.lazymankits.utils;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.localization.Keyword;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LMKeyword {
    public String PROPER;
    public String[] NAMES;
    public String DESCRIPTION;

    public static Map<String, Keyword> FromJson(String path) {
        String keywordStrings = Gdx.files.internal(path).readString(String.valueOf(StandardCharsets.UTF_8));
        Gson gson = new Gson();
        Type typeToken = new TypeToken<Map<String, Keyword>>(){}.getType();
        return gson.fromJson(keywordStrings, typeToken);
    }
    
    public static Map<String, LMKeyword> SelfFromJson(String path) {
        String keywordStrings = Gdx.files.internal(path).readString(String.valueOf(StandardCharsets.UTF_8));
        Gson gson = new Gson();
        Type typeToken = new TypeToken<Map<String, LMKeyword>>(){}.getType();
        return gson.fromJson(keywordStrings, typeToken);
    }
}