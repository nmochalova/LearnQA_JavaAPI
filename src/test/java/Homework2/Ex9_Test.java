package Homework2;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/***
 * Ex9*: Подбор пароля
Задача - написать тест и указать в нем login=super_admin и все пароли из Википедии в виде списка:
 https://en.wikipedia.org/wiki/List_of_the_most_common_passwords

 Программа должна делать следующее:
 1. Брать очередной пароль и вместе с логином вызывать первый метод get_secret_password_homework.
 В ответ метод будет возвращать авторизационную cookie с именем auth_cookie и каким-то значением.
 2. Далее эту cookie мы должны передать во второй метод check_auth_cookie. Если в ответ вернулась фраза "You are NOT authorized",
 значит пароль неправильный. В этом случае берем следующий пароль и все заново.
 Если же вернулась другая фраза - нужно, чтобы программа вывела верный пароль и эту фразу.

 Ответом к задаче должен быть верный пароль
 */
@Epic("Homework")
public class Ex9_Test {
    @Test
    public void AuthTest() {
        String[] passwords = new String[] {"123456","123456789","qwerty","password","1234567","12345678","12345","iloveyou","111111",
                                          "123123","abc123","qwerty123","1q2w3e4r","admin","qwertyuiop","654321","555555","lovely",
                                           "7777777","welcome","888888","princess","dragon","password1","123qwe"};
        String login = "super_admin";

        for (int i = 0; i < passwords.length; i++) {
            Response responseForGet = RestAssured
                    .given()
                    .queryParam("login",login)
                    .queryParam("password", passwords[i])
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = responseForGet.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            if (responseCookie != null)
                cookies.put("auth_cookie", responseCookie);

            Response responseForCheck = RestAssured
                    .given()
                    .queryParam("login",login)
                    .queryParam("password", passwords[i])
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String result = responseForCheck.print();

            if(result.equals("You are authorized"))
                System.out.println(passwords[i]);
        }
    }
}
