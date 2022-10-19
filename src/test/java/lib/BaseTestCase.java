package lib;

import io.qameta.allure.Step;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    protected final ApiCoreRequest apiCoreRequest = new ApiCoreRequest();
    protected final String BASE_URL = "https://playground.learnqa.ru/api/user/";
    protected final int TEST_USER_ID = 2;
    protected final String URL_LOGIN = "https://playground.learnqa.ru/api/user/login";
    protected final String URL_AUTH = "https://playground.learnqa.ru/api/user/auth";
    @Step("Return header of response")
    //Проверяем, что в указанный заголовок пришло значение и возвращаем его
    protected String getHeader(Response response, String name) {
        Headers headers = response.getHeaders();

        assertTrue(
                headers.hasHeaderWithName(name),
                "Response doesn't have header with name" + name
                );
        return headers.getValue(name);
    }

    @Step("Return cookie of response")
    //Проверяем, что в указанную куку пришло значение и возвращаем его
    protected String getCookie(Response response, String name) {
        Map<String,String> cookies = response.getCookies();

        assertTrue(
                cookies.containsKey(name),
                "Response doesn't have cookie with name " + name
        );
        return cookies.get(name);
    }

    @Step("Return int value of field")
    //Метод, который проверяет наличие поля name и возвращает его значение
    protected int getIntFromJson(Response response, String name) {
        response
                .then()
                .assertThat()
                .body("$",hasKey(name) //$ - ищем поле в корне json
        );
        return response.jsonPath().getInt(name);
    }

    @Step("Return string value of field")
    //Метод, который проверяет наличие поля name и возвращает его значение
    protected String getStringFromJson(Response response, String name) {
        response
                .then()
                .assertThat()
                .body("$",hasKey(name) //$ - ищем поле в корне json
                );
        return response.jsonPath().getString(name);
    }

    protected Map<String,String> createUserAndLoginUser() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegisrationData();
        JsonPath responseCreateAuth = apiCoreRequest
                .makePostRequest(BASE_URL,userData)
                .jsonPath();

        String userId = responseCreateAuth.getString("id");
        userData.put("userId",userId);

        //LOGIN USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequest.makePostRequest(URL_LOGIN, authData);
        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        userData.put("token",token);
        userData.put("cookie",cookie);

        return userData;
    }
}
