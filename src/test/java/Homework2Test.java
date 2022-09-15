import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Homework2Test {
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

    /***
     * Ex6:* Редирект
     * Сделать GET-запрос.
     * Распечатать адрес, на который редиректит указанные URL.
     */
    @Test
    public void RedirectTest(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        System.out.println(response.getHeader("Location"));
    }

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
