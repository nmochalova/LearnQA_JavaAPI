package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
https://playground.learnqa.ru/api/map
Вкладка Open user API
 */
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
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

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
    public void testAuthUser() {
        //Проверяем, что логирование успешно (GET-метод /user/auth/). Если передаем верные значения,
        //то вернет id пользователя (т.е. запрос считается авторизованным)
        //Иначе id = 0 (запрос считается не авторизованным, т.е. переданы неверные заголовок и куки или не переданы вовсе.)
        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token",this.header)                 //Только в случае правильной передачи
                .cookie("auth_sid",this.cookie)                     //заголовка 'x-csrf-token' и куки 'auth_sid'
                .get("https://playground.learnqa.ru/api/user/auth")     //запросы будут считаться авторизованными.
                .andReturn();

        Assertions.assertJsonByName(responseCheckAuth,"user_id",this.userIdOnAuth);
    }

    //Негативные параметризованные тесты: в метод проверки авторизации будем передавать только один из параметров: headers или cookies.
    @ParameterizedTest
    @ValueSource(strings = {"cookie","headers"})
    public void testNegativeAuthUser(String condition) {
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");  //объявляем куда будем слать запрос

        if(condition.equals("cookie")) {                            //добавляет только одну часть к запросу в зависимости от параметров теста
            spec.cookie("auth_sid",this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.header);
        } else {
            throw new IllegalArgumentException("Condition values is know: " + condition);
        }

        Response responseForCheck = spec.get().andReturn();                           //делаем get-запрос
        Assertions.assertJsonByName(responseForCheck,"user_id",0);  //Убеждаем что в ответе 0, т.е. ошибка авторизации
    }
}
