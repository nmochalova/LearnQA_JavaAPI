package Homework2.Homework3;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Написать тест, который проверяет длину какое-то переменной типа String.
Если текст длиннее 15 символов, то тест должен проходить успешно. Иначе падать с ошибкой.
 */
@Epic("Homework")
public class Ex10_Test {
    @ParameterizedTest
    @ValueSource(strings = {"Abracadabra_Abracadabra","Anna"})
    public void checkLengthOfString(String name) {
            Map<String,String> queryParams = new HashMap<>();
            queryParams.put("name",name);

            JsonPath response = RestAssured
                    .given()
                    .queryParams(queryParams)
                    .get("https://playground.learnqa.ru/api/hello")
                    .jsonPath();

            String answer = response.getString("answer");

            assertEquals(answer.length(),("Hello, " + name).length(),"The text of response is not equals name");
            //assertTrue(answer.length() > 15,"The length of the text response is less than 15 characters");
    }
}
