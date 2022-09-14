package Lessons;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ParamsTest {
    //Передача параметра в get-запрос
    @Test
    public void getSomeParamTest(){
        Response response = RestAssured
                .given()
                .queryParam("name","Nataly")
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    //Передача нескольких параметров в get-запрос
    //+ парсинг ответа json
    @Test
    public void getAnyParamTest(){
        Map<String,String> params = new HashMap<>();
        params.put("name","John");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer");

        //Проверка наличия нужных ключей
        if (name == null)
            System.out.println("The key is absent");
        else
            System.out.println(name);
    }

    //Задание параметров в методе get
    @Test
    public void getCheckTypeTest(){
        Response response = RestAssured
                .given()
                .queryParam("param1","value1")
                .queryParam("param2","value2")
                .get("https://playground.learnqa.ru/api/check_type") //метод возвращает тип запроса, который его вызвал
                .andReturn();

        System.out.println("************** Print ****************");
        response.print();
        System.out.println("************** prettyPrint (wrapper html) ****************");
        response.prettyPrint(); //функция оборачивает простой текст html-тегами
    }

    //Задание параметров в методе post
    @Test
    public void postCheckTypeTest(){
        //Вариант 1. Простой строкой с разделитем & между парой ключ=значение
        System.out.println("************** Variant 1 *****************");
        Response response = RestAssured
                .given()
                .body("param1=value1&param2=value2")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();

        //Вариант 2. В виде json с экранированием двойных ковычек
        System.out.println("************** Variant 2 *****************");
        response = RestAssured
                .given()
                .body("{\"param1\":\"value1\",\"param2\":\"value2\"}")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();

        //Вариант 3. С помощью map. Преобразование из map в json-формат происходит в RestAssured
        System.out.println("************** Variant 3 *****************");
        Map<String,String> params = new HashMap<>();
        params.put("param1","value1");
        params.put("param2","value2");

        response = RestAssured
                .given()
                .body(params)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }
}
