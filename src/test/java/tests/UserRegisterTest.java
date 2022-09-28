package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    @Test
    public void testCreateUserWithExistingEmail() {
        Map<String,String> userData = new HashMap<>();  //данные с существующим email
        String email = "vinkotov@example.com";
        userData.put("email",email);
        userData = DataGenerator.getRegisrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        Map<String,String> userData = DataGenerator.getRegisrationData(); //данные с рандомным email

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }
}
