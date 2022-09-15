package Homework2;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class Ex8_Test {
    /***
     * Ex8*: Токены
     * 1) создать задачу
     * 2) сделать один запрос с token ДО того, как задача готова
     * 3) подождать нужное количество секунд
     * 4) сделать один запрос c token ПОСЛЕ того, как задача готова, убедиться в правильности поля status и наличии поля result
     */
      @Test
    public void LongtimeJobTestV1() throws InterruptedException {
        String baseURL = "https://playground.learnqa.ru/ajax/api/longtime_job";

        Response response = RestAssured  //вызов без token
                .get(baseURL)
                .andReturn();

        Assertions.assertEquals(200,response.getStatusCode()); //проверяем что код ответа 200

        JsonPath responseBody = response.getBody().jsonPath(); //извлекаем json
        String token = responseBody.get("token");
        int seconds = responseBody.get("seconds");

        Thread.sleep(seconds*1000); //ожидаем пока выполнится job

        Response respToken = RestAssured //вызов с token
                .given()
                .queryParam("token",token)
                .get(baseURL)
                .andReturn();

        Assertions.assertEquals(200,respToken.getStatusCode()); //проверяем что код ответа 200

        JsonPath respTokenBody = respToken.getBody().jsonPath(); //извлекаем json
        Assertions.assertEquals("Job is ready",respTokenBody.get("status"), "Status is not correct.");
        Assertions.assertEquals("42",respTokenBody.get("result"),"Result is not correct.");
    }

    /***
     * Ex8*: Токены (Второй вариант решения)
     */
    @Test
    public void LongtimeJobTestV2() throws InterruptedException {
         String baseURL = "https://playground.learnqa.ru/ajax/api/longtime_job";

        ValidatableResponse response = RestAssured  //вызов без token
                .given()
                .log().all()
                .when()
                .get(baseURL)
                .then()
                .statusCode(200)
                .log().all();

        JsonPath responseBody = response.extract().jsonPath(); //извлекаем json
        String token = responseBody.get("token");
        int seconds = (int) responseBody.get("seconds") * 1000; //переводим в милисекунды

        Thread.sleep(seconds); //ожидаем пока выполнится job

        ValidatableResponse respToken = RestAssured //вызов с token
                .given()
                .queryParam("token",token)
                .log().all()
                .when()
                .get(baseURL)
                .then()
                .statusCode(200)
                .log().all();

        respToken
                .body("status",equalTo("Job is ready"))
                .body("result",equalTo("42"));
    }
}
