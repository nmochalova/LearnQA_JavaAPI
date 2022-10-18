package lib;

import io.qameta.allure.Step;
import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    @Step("Return header of response")
    //Проверяем, что в указанный заголовок пришло значение и возвращаем его
    protected String getHeader(Response response, String name) {
        Headers headers = response.getHeaders();

        assertTrue(
                headers.hasHeaderWithName(name),
                "Response doesn't have header with name" + name
                );
        return headers.getValue(name);
    }

    @Step("Return cookie of response")
    //Проверяем, что в указанную куку пришло значение и возвращаем его
    protected String getCookie(Response response, String name) {
        Map<String,String> cookies = response.getCookies();

        assertTrue(
                cookies.containsKey(name),
                "Response doesn't have cookie with name " + name
        );
        return cookies.get(name);
    }

    @Step("Return int value of field")
    //Метод, который проверяет наличие поля name и возвращает его значение
    protected int getIntFromJson(Response response, String name) {
        response
                .then()
                .assertThat()
                .body("$",hasKey(name) //$ - ищем поле в корне json
        );
        return response.jsonPath().getInt(name);
    }
}
