package rs.lazymankits.data;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rs.lazymankits.LMDebug;
import rs.lazymankits.LManager;
import rs.lazymankits.interfaces.LMSubscriber;
import rs.lazymankits.managers.LMCustomCardTagMgr;
import rs.lazymankits.managers.LMCustomRarityMgr;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LMXCardDataReader {
    private static SAXReader reader;
    public static LMXCardData LMKExample;
    private static Map<String, LMXCardData> map;
    private static boolean Ready;
    
    public static void Initialize() {
        if (LManager.EverythingReady()) return;
        Ready = false;
        reader = new SAXReader();
        map = new HashMap<>();
        Ready = true;
    }
    
    public static void StartReadDatas() {
        InitializeExampleData();
        LMSubscriber.PublishOnReadingLMXData();
    }
    
    private static void InitializeExampleData() {
        try {
            Element[] elements = LoadElementsFromPath(LMXCardDataReader.class, "SharedAssets/datas", 
                    "cardata", "extracardata");
            Element cardata;
            for (int i = 0; i < elements.length; i++) {
                if (i == 0) {
                    Element head = elements[i];
                    cardata = head.element("LMXCardData");
                    LMKExample = new LMXCardData("LMKExample").copyData(cardata);
                    continue;
                }
                Element e = elements[i];
                cardata = e.element("LMXCardData");
                LMKExample.appendData(cardata);
            }
        } catch (Exception e) {
            Log("Failed to load example data");
            e.printStackTrace();
        }
        /*try {
            Element root = RootElement(LoadFileFromString("SharedAssets/datas/cardata"));
            Element cardata = root.element("LMXCardData");
            LMKExample = new LMXCardData("LMKExample").copyData(cardata);
        } catch (Exception e) {
            Log("Failed to load example data");
            e.printStackTrace();
        }*/
    }
    
    @NotNull
    private static Element[] LoadElementsFromPath(Class<?> clazz, String dirPath, @NotNull String... fileNames) throws Exception {
        URL[] urls = new URL[fileNames.length];
        Element[] elements = new Element[urls.length];
        for (int i = 0; i < fileNames.length; i++) {
            String path = dirPath + (dirPath.endsWith("/") ? "" : "/") + fileNames[i];
            Log("Locating target xml file: " + path);
            urls[i] = LoadFileFromString(clazz, path);
        }
        for (int i = 0; i < urls.length; i++) {
            elements[i] = RootElement(urls[i]);
        }
        /*URL url = LoadFileFromString(clazz, path);
        String truePath = url.getFile();
        File[] files = new File(truePath).listFiles();
        List<Element> elements = new ArrayList<>();
        for (File f : files) {
            boolean isFile = f.isFile();
            String suffix = f.getName().substring(f.getName().lastIndexOf(".") + 1);
            String filePath = f.getPath();
            if (isFile && suffix.equals("xml")) {
                Element e = RootElement(LoadFileFromString(clazz, filePath));
                if (!elements.contains(e)) elements.add(e);
            }
        }
        return elements.toArray(new Element[0]);*/
        return elements;
    }
    
    @Nullable
    public static LMXCardData GetData(String uniqueID) {
        return Ready ? map.get(uniqueID) : null;
    }
    
    public static boolean RegisterLMXData(Class<?> clazz, String path, String setName, String id, String uniqueID) {
        if (!Ready) return false;
        try {
            LMCustomCardTagMgr.StartRegisterTags();
            LMCustomRarityMgr.StartRegisterRarity();
            Element root = RootElement(LoadFileFromString(clazz, path));
            Element cardata = root.element(setName);
            LMXCardData data = new LMXCardData(id).copyData(cardata);
            map.put(uniqueID, data);
            boolean success = map.containsKey(uniqueID) && map.get(uniqueID).valid();
            if (!success) Log("Failed to read " + uniqueID + "'s data from " + clazz.getName());
            return success;
        } catch (Exception e) {
            Log("Failed to load " + uniqueID + "'s data from " + clazz.getName());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean RegisterLMXData(Class<?> clazz, String path, String id, String uniqueID) {
        return RegisterLMXData(clazz, path, "LMXCardData", id, uniqueID);
    }

    public static boolean RegisterLMXData(Class<?> clazz, Class<? extends LMXCardData> dataType, String path, String setName, String id, 
                                          String uniqueID) {
        if (!Ready) return false;
        try {
            LMCustomCardTagMgr.StartRegisterTags();
            LMCustomRarityMgr.StartRegisterRarity();
            Element root = RootElement(LoadFileFromString(clazz, path));
            Element cardata = root.element(setName);
            LMXCardData customData = dataType.newInstance();
            customData.copyData(cardata);
            LMXCardData data = new LMXCardData(id).copyData(cardata);
            map.put(uniqueID, data);
            boolean success = map.containsKey(uniqueID) && map.get(uniqueID).valid();
            if (!success) Log("Failed to read " + uniqueID + "'s custom data from " + clazz.getName());
            return success;
        } catch (Exception e) {
            Log("Failed to load " + uniqueID + "'s custom data from " + clazz.getName());
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean RegisterLMXDatas(Class<?> clazz, String dirPath, String setName, String id, String uniqueID, 
                                           String... fileNames) {
        if (!Ready) return false;
        try {
            LMCustomCardTagMgr.StartRegisterTags();
            LMCustomRarityMgr.StartRegisterRarity();
            Element[] elements = LoadElementsFromPath(clazz, dirPath, fileNames);
            Element cardata;
            LMXCardData data = null;
            for (int i = 0; i < elements.length; i++) {
                if (i == 0) {
                    Element headset = elements[i];
                    cardata = headset.element(setName);
                    data = new LMXCardData(id).copyData(cardata);
                    continue;
                }
                Element set = elements[i];
                cardata = set.element(setName);
                data.appendData(cardata);
            }
        } catch (Exception e) {
            Log("Failed to load " + uniqueID + "'s datas in " + dirPath + " from " + clazz.getName());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean RegisterLMXDatas(Class<?> clazz, String dirPath, String id, String uniqueID,
                                           String... fileNames) {
        return RegisterLMXDatas(clazz, dirPath, "LMXCardData", id, uniqueID, fileNames);
    }
    
    private static Element RootElement(URL path) throws DocumentException {
        return reader.read(path).getRootElement();
    }

    @Contract("_ -> new")
    private static URL LoadFileFromString(String path) {
        return LMXCardDataReader.class.getClassLoader().getResource(path);
    }
    
    private static URL LoadFileFromString(@NotNull Class<?> clazz, String path) {
        return clazz.getClassLoader().getResource(path);
    }
    
    private static void Log(Object what) {
        LMDebug.Log(LMXCardDataReader.class, what);
    }
}