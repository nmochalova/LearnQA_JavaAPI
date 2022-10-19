package tests;

import io.qameta.allure.Story;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

/*
https://playground.learnqa.ru/api/map
Вкладка Open user API

Для запуска Allure-отчетов во вкладке Terminal: allure serve allure-results/
 */
@Epic("User cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser() {
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        //Логинимся (POST-метод /user/login/)
        Response responseGetAuth = apiCoreRequest
                .makePostRequest(URL_LOGIN,authData);

        //Авторизационная куки, с которой сервер свяжет нашего пользователя.
        //Ко всем дальнейшим запросам нужно прикладывать эту куки, чтобы сервер понимал
        //что запросы идут от нашего пользователя и являются авторизованными.
        this.cookie = this.getCookie(responseGetAuth,"auth_sid");

        //Заголовок, который играет ключевую роль в безопасности пользователя
        //и не позволяет подделывать запросы от имени пользователя злоумышленникам.
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");

        //id пользователя, под которым мы авторизовались
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    //Позитивный сценарий авторизации
    @Test
    @Story("This test successfully authorize user by email and password")
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    public void testAuthUser() {
        //Проверяем, что логирование успешно. Если передаем верные значения заголовка 'x-csrf-token' и куки 'auth_sid',
        //то вернет id пользователя (т.е. запрос считается авторизованным)
        //Иначе id = 0
        Response responseCheckAuth = apiCoreRequest
                .makeGetRequest(URL_AUTH,
                        this.header,
                        this.cookie);

        Assertions.assertJsonByName(responseCheckAuth,"user_id",this.userIdOnAuth);
    }

    //Негативные параметризованные тесты: в метод проверки авторизации будем передавать только один из параметров:
    // headers или cookies.
    @Story("Test checks authorization status")
    @Description("This test checks authorization status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie","headers"})
    public void testNegativeAuthUser(String condition) {
        //добавляет только одну часть к запросу в зависимости от параметров теста
        if(condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequest.makeGetRequestWithCookie(
                    URL_AUTH,
                    this.cookie
            );
            //Убеждаем что в ответе 0, т.е. ошибка авторизации
            Assertions.assertJsonByName(responseForCheck,"user_id",0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequest.makeGetRequestWithToken(
                    URL_AUTH,
                    this.header
            );
            Assertions.assertJsonByName(responseForCheck,"user_id",0);
        } else {
            throw new IllegalArgumentException("Condition values is know: " + condition);
        }
    }
}
