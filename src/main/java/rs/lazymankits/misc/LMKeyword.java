package rs.lazymankits.misc;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import rs.lazymankits.annotations.Replaced;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Deprecated
@Replaced(substitute = rs.lazymankits.utils.LMKeyword.class)
public class LMKeyword {
    public String PROPER_NAME;
    public String[] NAMES;
    public String DESCRIPTION;

    public static Map<String, LMKeyword> FromJson(String path) {
        String keywordStrings = Gdx.files.internal(path).readString(String.valueOf(StandardCharsets.UTF_8));
        Type typeToken = new TypeToken<Map<String, LMKeyword>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(keywordStrings, typeToken);
    }
}