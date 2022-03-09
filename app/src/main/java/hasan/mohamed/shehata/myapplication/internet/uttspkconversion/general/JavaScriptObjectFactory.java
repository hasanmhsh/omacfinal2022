package hasan.mohamed.shehata.myapplication.internet.uttspkconversion.general;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;


public class JavaScriptObjectFactory {

    public static <T> Map<String, T> toMap(String json) {
        return getData().fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    private static Gson getData() {
        return new Gson();
    }
    public static String setData(Object object) {
        return getData().toJson(object);
    }

    public static <T> List<T> toList(String json, Class<T> tClass) {
        return getData().fromJson(json, new TypeToken<List<T>>() {
        }.getType());
    }

    public static <T> T toObject(String json, Class<T> tClass) {
        return getData().fromJson(json, tClass);
    }

    public static <T> T toObjectHTMLEscapingIsDisabled(String json, Class<T> tClass) {
        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
        Gson gson = builder.create();
        return gson.fromJson(json, tClass);
    }
}
