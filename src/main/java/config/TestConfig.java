package config;

import io.restassured.RestAssured;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.Map;

import static Constants.Constants.RunVerible.*;

public class TestConfig {

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = server;

    }

    @SafeVarargs
    protected final Map<String, String> createParams(Pair<String, String>... pairs) {
        Map<String, String> params = new HashMap<>();
        params.put("v", version);
        params.put("access_token", token);
        params.put("user_id", user_id);
        for (Pair<String, String> pair : pairs) {
            params.put(pair.getLeft(), pair.getRight());
        }
        return params;
    }
    @SafeVarargs
    protected final Map<String, String> createParamsGroup(Pair<String, String>... pairs) {
        Map<String, String> params = new HashMap<>();
        params.put("v", version);
        params.put("access_token", token);
        params.put("user_id", user_id);
        params.put("group_id", group_id);
        for (Pair<String, String> pair : pairs) {
            params.put(pair.getLeft(), pair.getRight());
        }
        return params;
    }

    protected void print(String value, String descr) {
        System.out.printf("%s: '%s'\n", descr, value);
    }

}
