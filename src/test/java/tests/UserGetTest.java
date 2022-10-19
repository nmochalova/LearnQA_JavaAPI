package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Get user")
public class UserGetTest extends BaseTestCase {

    //просмотр данных для неавторизованного пользователя
    @Test
    @Story("This test get request by not authorization user")
    @Description("This test get request by not authorization user")
    @DisplayName("Negative: user is not authorization")
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequest.makeGetRequest(BASE_URL + TEST_USER_ID);

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
                URL_LOGIN,
                authData);

        //Для авторизованного пользователя получаем заголовок и куки
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        //запрашиваем данные по авторизованному пользователю
        Response responseUserData = apiCoreRequest.makeGetRequest(
                BASE_URL + TEST_USER_ID,
                header,
                cookie);

        //Проверяем, что json ответа содержит поля
        String[] expectedFields = {"username","firstName","lastName","email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);
    }

    //тест, который авторизовывается одним пользователем, но получает данные другого (т.е. с другим ID)
    @Test
    @Story("This test authorization one user but get details another user")
    @Description("This test authorization one user but get details another user")
    @DisplayName("Negative: get details not authorization user")
    public void testGetUserDetailsAuthAsOtherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        //авторизация для существующего пользователя
        Response responseGetAuth = apiCoreRequest.makePostRequest(
                URL_LOGIN,
                authData);

        //Для авторизованного пользователя получаем заголовок и куки
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");
        int id = this.getIntFromJson(responseGetAuth,"user_id");
        String url = BASE_URL + (id-1);

        //запрашиваем данные по другому пользователю, не авторизованному
        Response responseUserData = apiCoreRequest.makeGetRequest(
                url,
                header,
                cookie);

        //Проверяем, что json ответа содержит только поле username
        Assertions.assertJsonHasField(responseUserData,"username");           //поле есть
        Assertions.assertJsonHasNotField(responseUserData,"firstName");     //поля нет
        Assertions.assertJsonHasNotField(responseUserData,"lastName");      //поля нет
        Assertions.assertJsonHasNotField(responseUserData,"email");         //поля нет
    }
}
