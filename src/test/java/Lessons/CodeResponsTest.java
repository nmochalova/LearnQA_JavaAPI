package Lessons;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class CodeResponsTest {
    //Коды запросов
    @Test
    public void getStatusCodeTest(){
        //StatusCode = 200
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        System.out.println(response.getStatusCode());

        //StatusCode = 500
        response = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();

        System.out.println(response.getStatusCode());

        //StatusCode = 404 (несуществующий url)
        response = RestAssured
                .get("https://playground.learnqa.ru/api/something")
                .andReturn();

        System.out.println(response.getStatusCode());

        //StatusCode = 303 (стоп на первом редиректе)
        response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        System.out.println(response.getStatusCode());

        //StatusCode = 303 (следуем за редиректом до конца - в примере до 200)
        response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        System.out.println(response.getStatusCode());
    }
}
