package Homework2;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
@Epic("Homework")
public class Ex5_Test {
    /***
     * Ex5: Парсинг JSON
     * Сделать GET-запрос.
     * Вывести текст второго сообщения.
     */
    @Test
    public void ParsingJsonTest(){
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String message = response.get("messages.message[1]");
        System.out.println(message);
    }
}
