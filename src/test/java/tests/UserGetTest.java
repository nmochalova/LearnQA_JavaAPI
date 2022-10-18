package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequest;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Get user")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequest apiCoreRequest = new ApiCoreRequest();

    //просмотр данных для неавторизованного пользователя
    @Test
    @Story("This test get request by not authorization user")
    @Description("This test get request by not authorization user")
    @DisplayName("Negative: user is not authorization")
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequest.makeGetRequest("https://playground.learnqa.ru/api/user/2");

        Assertions.assertJsonHasField(responseUserData,"username");           //поле есть
        Assertions.assertJsonHasNotField(responseUserData,"firstName");     //поля нет
        Assertions.assertJsonHasNotField(responseUserData,"lastName");      //поля нет
        Assertions.assertJsonHasNotField(responseUserData,"email");         //поля нет
    }

    //просмотр данных для авторизованного пользователя
    @Test
    @Story("This test get request by authorization user")
    @Description("This test get request by authorization user")
    @DisplayName("Positive: get authorization user")
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        //авторизация для существующего пользователя
        Response responseGetAuth = apiCoreRequest.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        //Для авторизованного пользователя получаем заголовок и куки
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        //запрашиваем данные по авторизованному пользователю
        Response responseUserData = apiCoreRequest.makeGetRequest(
                "https://playground.learnqa.ru/api/user/2",
                header,
                cookie);

        //Проверяем, что json ответа содержит поля
        String[] expectedFields = {"username","firstName","lastName","email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);
    }
}
