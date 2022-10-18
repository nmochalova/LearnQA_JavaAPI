package Lessons;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

//Заголовки http-запроса - это служебная иформация. Есть как у запроса, так и у ответа
//F12 - Network - Headers

@Epic("Lessons")
public class HeadersTest {
    //show_all_headers - метод, который возвращает заголовки запроса или ответа
    @Test
    public void getHeadersTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("MyHeader1", "MyValue1");
        headers.put("MyHeader2", "MyValue2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        //это ответ сервера, который напечатал те заголовки, которые получил от нас в запросе
        System.out.println("************** Header request *****************");
        response.prettyPrint();

        //Это заголовки, которые мы получили в ответе
        System.out.println("************** Header response *****************");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);
    }

    //Получаем заголовок location для запроса с кодом ответа 303
    @Test
    public void getHeadersLocationTest() {
        Map<String, String> headers = new HashMap<>();
        headers.put("MyHeader1", "MyValue1");
        headers.put("MyHeader2", "MyValue2");

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }
}
