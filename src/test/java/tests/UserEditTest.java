package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequest;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Edit user")
public class UserEditTest extends BaseTestCase {
  private final ApiCoreRequest apiCoreRequest = new ApiCoreRequest();
  private final String BASE_URL = "https://playground.learnqa.ru/api/user/";
  private final String URL_LOGIN = "https://playground.learnqa.ru/api/user/login";

  //Тест создает пользователя, авторизуется под этим пользователем и меняет параметр firstName на новое.
  //В конце проверяем, что имя было изменено корректно.
  @Test
  public void testEditJustCreatedTest() {
    //GENERATE USER
    Map<String, String> userData = DataGenerator.getRegisrationData();

    JsonPath responseCreateAuth = apiCoreRequest
                   .makePostRequest(BASE_URL,userData)
                   .jsonPath();

    String userId = responseCreateAuth.getString("id");

    //LOGIN
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userData.get("email"));
    authData.put("password", userData.get("password"));

    Response responseGetAuth = apiCoreRequest.makePostRequest(
            URL_LOGIN,
            authData);

    //EDIT
    String newName = "Changed name";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", newName);

    String url = BASE_URL + userId;
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    Response responseEditUser = apiCoreRequest.makePutRequest(url,token,cookie,editData);

    //GET
    Response responseUserData = apiCoreRequest.makeGetRequest(url,token,cookie);

    Assertions.assertJsonByName(responseUserData, "firstName", newName);
  }
}
