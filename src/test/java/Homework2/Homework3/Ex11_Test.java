package Homework2.Homework3;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Необходимо написать тест, который делает запрос на метод: https://playground.learnqa.ru/api/homework_cookie
Этот метод возвращает какую-то cookie с каким-то значением.
Необходимо понять что за cookie и с каким значением, и зафиксировать это поведение с помощью assert.
 */
@Epic("Homework")
public class Ex11_Test {
    @Test
    public void testCookies() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookies =  response.getCookies();

        String cookieName = "HomeWork";
        String expectedValue = "hw_value";

        assertTrue(cookies.containsKey(cookieName),"Response doesn't have cookie with name " + cookieName);
        assertEquals(expectedValue,cookies.get(cookieName), "The value of cookie doesn't expected");
    }
}
