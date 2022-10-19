package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Create user")
public class UserRegisterTest extends BaseTestCase {
    @Test
    @Story("This test create user with incorrect email")
    @Description("This test create user with incorrect email")
    @DisplayName("Negative: with incorrect email")
    public void testCreateUserWithIncorrectEmail() {
        Map<String,String> userData = new HashMap<>();  //создание пользователя с некорректным email - без символа @
        String email = "vinkotovexample.com";
        userData.put("email",email);
        userData = DataGenerator.getRegisrationData(userData);

        Response responseCreateAuth = apiCoreRequest.makePostRequest(
                BASE_URL,
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Invalid email format");
    }

    @Story("This test create user without one parameter")
    @Description("This test create user without one parameter")
    @DisplayName("Negative: without one parameter")
    @ParameterizedTest
    @ValueSource(strings = {"username","firstName","lastName","email","password"})
    public void testCreateUserWithoutOneParameter(String parameter) {
        Map<String,String> userData = DataGenerator.getRegisrationData(parameter); //Создание пользователя без указания одного из полей

        Response responseCreateAuth = apiCoreRequest.makePostRequest(
                BASE_URL,
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The following required params are missed: " + parameter);
    }

    @Story("This test create user with short name")
    @Description("This test create user with short name")
    @DisplayName("Negative: with short name")
    @ParameterizedTest
    @ValueSource(strings = {"username","firstName","lastName"})
    public void testCreateUserWithShortName(String parameter) {
        Map<String,String> userData = new HashMap<>();  //Создание пользователя с очень коротким именем в один символ
        String shortName = "1";
        userData.put(parameter,shortName);
        userData = DataGenerator.getRegisrationData(userData);

        Response responseCreateAuth = apiCoreRequest.makePostRequest(
                BASE_URL,
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of '" + parameter + "' field is too short");
    }

    @Story("This test create user with long name")
    @Description("This test create user with long name")
    @DisplayName("Negative: with long name")
    @ParameterizedTest
    @ValueSource(strings = {"username","firstName","lastName"})
    public void testCreateUserWithLongName(String parameter) {
        Map<String,String> userData = new HashMap<>();  //Создание пользователя с очень длинным именем - длиннее 250 символов

        String longName = "The ability to find search keys keys in the text and determine their number is useful both for " +
                "writing a new text and for optimizing an existing one. The arrangement of keywords by groups and by frequency " +
                "will make key navigation convenient and fasting.";
        userData.put(parameter,longName);
        userData = DataGenerator.getRegisrationData(userData);

        Response responseCreateAuth = apiCoreRequest.makePostRequest(
                BASE_URL,
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of '" + parameter + "' field is too long");
    }

    @Test
    @Story("This test create user with existing email")
    @Description("This test create user with existing email")
    @DisplayName("Negative: with existing email")
    public void testCreateUserWithExistingEmail() {
        Map<String,String> userData = new HashMap<>();  //данные с существующим email
        String email = "vinkotov@example.com";
        userData.put("email",email);
        userData = DataGenerator.getRegisrationData(userData);

        Response responseCreateAuth = apiCoreRequest.makePostRequest(
                BASE_URL,
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");
    }

    @Test
    @Story("This test successfully create user")
    @Description("This test successfully create user")
    @DisplayName("Positive: create user")
    public void testCreateUserSuccessfully() {
        Map<String,String> userData = DataGenerator.getRegisrationData(); //данные с рандомным email

        Response responseCreateAuth = apiCoreRequest.makePostRequest(
                BASE_URL,
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }
}
