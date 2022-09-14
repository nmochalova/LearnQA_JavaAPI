package Lessons;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/***
 * Cookies - специальные файлы, которые создает клиент (моб.прилож. или браузер) на основе ответа сервера.
 * Файлы куки имеют срок годности, после которого они клиентом удаляются.
 * Куки имеют имя, значение и домен.
 * Цели создания cookies:
 * 1) авторизация (узнает ранее авторизованного пользователя)
 * 2) доп.информация о пользователе (например, действия пользователя)
 */
public class CookiesTest {
    //Получение cookie
    @Test
    public void getCookiesTest() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        //пустой вывод говорит о том, что никакого текста получено не было
        System.out.println("\nPretty text:");
        response.prettyPrint();

        //в заголовке Set-Cookie сервер просит установить cookie. В нем хранится вся необходимая о cookie информация:
        //название, значение, дата, домент, путь
        System.out.println("\nHeaders:");
        System.out.println(response.getHeaders());

        System.out.println("\nCookies:");
        System.out.println(response.getCookies());

        //значение cookies. Если null, это значит что кука не была получена, например из-за неверного ввода пароля
        System.out.println("\nCookies:");
        System.out.println(response.getCookie("auth_cookie"));
    }

    //Передача cookies в другой метод
    @Test
    public void checkCookiesTest() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (responseCookie != null)
               cookies.put("auth_cookie", responseCookie);

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print(); //You are authorized
    }
}
