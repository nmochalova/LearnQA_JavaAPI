package Homework2;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex6_Test {
    /***
     * Ex6:* Редирект
     * Сделать GET-запрос.
     * Распечатать адрес, на который редиректит указанные URL.
     */
    @Test
    public void RedirectTest(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        System.out.println(response.getHeader("Location"));
    }
}
