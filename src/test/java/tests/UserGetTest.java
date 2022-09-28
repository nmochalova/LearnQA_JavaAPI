package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {
    //просмотр данных для неавторизованного пользователя
    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserData,"username");           //поле есть
        Assertions.assertJsonHasNotField(responseUserData,"firstName");     //поля нет
        Assertions.assertJsonHasNotField(responseUserData,"lastName");      //поля нет
        Assertions.assertJsonHasNotField(responseUserData,"email");         //поля нет
    }

    //просмотр данных для авторизованного пользователя
    @Test
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        //авторизация для существующего пользователя
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //Для авторизованного пользователя получаем заголовок и куки
        String header = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie = this.getCookie(responseGetAuth,"auth_sid");

        //запрашиваем данные по авторизованному пользователю
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid",cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        //Проверяем, что json ответа содержит поля
        String[] expectedFields = {"username","firstName","lastName","email"};
        Assertions.assertJsonHasFields(responseUserData,expectedFields);
    }
}
