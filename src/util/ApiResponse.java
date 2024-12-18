package util;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {

    private static final Gson gson = new Gson();

    public static String createJsonResponse(String status, int code, Object data, Object error) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("code", code);
        response.put("data", data);
        response.put("error", error);

        return gson.toJson(response);
    }

    public static String success(Object data) {
        return createJsonResponse("success", 200, data, null);
    }

    public static String error(int code, String message, String details) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("message", message);
        errorDetails.put("details", details);

        return createJsonResponse("error", code, null, errorDetails);
    }
}
