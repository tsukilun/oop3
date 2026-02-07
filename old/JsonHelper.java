import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;

/**
 * JSON helper using Gson library.
 */
public class JsonHelper {

    private static final Gson gson = new Gson();

    //  Object → JSON 
    public static String userToJson(User user)         { return gson.toJson(user); }
    public static String usersToJson(List<User> users) { return gson.toJson(users); }
    public static String postToJson(Post post)         { return gson.toJson(post); }
    public static String postsToJson(List<Post> posts) { return gson.toJson(posts); }

    //  Simple responses 
    public static String message(String msg) {
        JsonObject obj = new JsonObject();
        obj.addProperty("message", msg);
        return gson.toJson(obj);
    }

    public static String error(String msg) {
        JsonObject obj = new JsonObject();
        obj.addProperty("error", msg);
        return gson.toJson(obj);
    }

    public static String idResponse(int id) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("message", "created");
        return gson.toJson(obj);
    }

    //  Parse JSON body 
    public static String getString(String json, String key) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        return obj.has(key) ? obj.get(key).getAsString() : null;
    }

    public static int getInt(String json, String key, int defaultVal) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return obj.has(key) ? obj.get(key).getAsInt() : defaultVal;
        } catch (Exception e) {
            return defaultVal;
        }
    }
}
