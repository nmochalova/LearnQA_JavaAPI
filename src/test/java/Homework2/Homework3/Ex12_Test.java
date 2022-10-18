package Homework2.Homework3;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Необходимо написать тест, который делает запрос на метод: https://playground.learnqa.ru/api/homework_header
Этот метод возвращает headers с каким-то значением. Необходимо понять что за headers и с каким значением,
и зафиксировать это поведение с помощью assert
 */
@Epic("Homework")
public class Ex12_Test {
    @Test
    public void testCookies() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headers = response.getHeaders();
        System.out.println(headers.toString());

        String headerName = "x-secret-homework-header";
        String headerValue = "Some secret value";

        assertTrue(headers.hasHeaderWithName(headerName),"Response doesn't have header with name " + headerName);
        assertEquals(headerValue,headers.getValue(headerName), "The value of header doesn't expected");
    }
}
