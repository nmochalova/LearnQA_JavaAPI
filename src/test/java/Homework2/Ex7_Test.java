package Homework2;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@Epic("Homework")
public class Ex7_Test {
    /***
     * Ex7:* Долгий редирект
     * Сделать GET-запрос.
     * Написать цикл, который будет создавать запросы в цикле, каждый раз читая URL для редиректа из нужного заголовка.
     * И так, пока мы не дойдем до ответа с кодом 200.
     */
    @Test
    public void LongRedirectTest(){
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode = 0;

        while (statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(url)
                    .andReturn();

            statusCode = response.getStatusCode();
            url = response.getHeader("Location");
            System.out.println(String.format("Status code: %d, URL: %s", statusCode, url));
        }
    }
}
