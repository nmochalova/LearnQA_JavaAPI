package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequest;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("User cases")
@Feature("Edit user")
public class UserEditTest extends BaseTestCase {
  private final ApiCoreRequest apiCoreRequest = new ApiCoreRequest();
  private final String BASE_URL = "https://playground.learnqa.ru/api/user/";
  private final String URL_LOGIN = "https://playground.learnqa.ru/api/user/login";

  // Попытаемся изменить данные пользователя, будучи неавторизованными
  @Test
  @Story("This test change the data of an unauthorized user")
  @Description("This test change the data of an unauthorized user")
  @DisplayName("Negative: edit of an unauthorized user")
  public void editNameByNotAuthUserTest() {
    //EDIT WITHOUT AUTH
    String newName = "Changed name";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", newName);

    String url = BASE_URL + 2;

    Response responseEditUser = apiCoreRequest.makePutRequest(url,editData);

    Assertions.assertResponseCodeEquals(responseEditUser,400);
    Assertions.assertResponseTextEquals(responseEditUser,"Auth token not supplied");
  }

  //Попытаемся изменить данные пользователя, будучи авторизованными другим пользователем
  @Test
  @Story("This test change the data of a user who has logged in by another user")
  @Description("This test change the data of a user who has logged in by another user")
  @DisplayName("Negative: edit user who has logged in by another user")
  public void editNameByAuthAnotherUserTest() {
    //GENERATE AND LOGIN FIRST USER
    Map<String, String> firstUser = createUserAndLoginUser();
    String firstUserId = firstUser.get("userId");
    String token = firstUser.get("token");
    String cookie = firstUser.get("cookie");

    //GET SECOND USER BEFORE EDIT
    String changeField = "username";
    int secondIdUser = Integer.parseInt(firstUserId)-1;
    Response responseUserDataBeforeEdit = apiCoreRequest.makeGetRequest(BASE_URL + secondIdUser,token,cookie);
    String nameAfter = this.getStringFromJson(responseUserDataBeforeEdit,changeField);

    //EDIT SECOND USER
    String newName = "Changed name";
    Map<String, String> editData = new HashMap<>();
    editData.put(changeField, newName);
    apiCoreRequest.makePutRequest(BASE_URL + secondIdUser,token,cookie,editData);

    //GET SECOND USER AFTER EDIT
    Response responseUserDataAfterEdit = apiCoreRequest.makeGetRequest(BASE_URL + secondIdUser,token,cookie);
    String nameBefore = this.getStringFromJson(responseUserDataAfterEdit,changeField);

    assertEquals(nameAfter,nameBefore);
  }

  //Попытаемся изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @
  @Test
  @Story("This test changes the email to an incorrect one")
  @Description("This test changes the email to an incorrect one")
  @DisplayName("Negative: edit email to an incorrect one")
  public void editEmailByAuthUserTest() {
    //GENERATE AND LOGIN USER
    Map<String, String> firstUser = createUserAndLoginUser();
    String userId = firstUser.get("userId");
    String token = firstUser.get("token");
    String cookie = firstUser.get("cookie");
    String email = firstUser.get("email");

    //EDIT
    String newEmail = "newEmail";
    Map<String, String> editData = new HashMap<>();
    editData.put("email", newEmail);
    String url = BASE_URL + userId;
    Response responseEditData = apiCoreRequest.makePutRequest(url,token,cookie,editData);

    Assertions.assertResponseCodeEquals(responseEditData,400);
    Assertions.assertResponseTextEquals(responseEditData,"Invalid email format");

    //GET
    Response responseUserData = apiCoreRequest.makeGetRequest(url,token,cookie);

    Assertions.assertJsonByName(responseUserData, "email", email);
  }

  //Попытаемся изменить firstName пользователя, будучи авторизованными тем же пользователем,
  //на очень короткое значение в один символ
  @Test
  @Story("This test changes the email to an incorrect one")
  @Description("This test changes the email to an incorrect one")
  @DisplayName("Negative: edit email to an incorrect one")
  public void shortNameByAuthUserTest() {
    String fieldName = "firstName";

    //GENERATE AND LOGIN USER
    Map<String, String> firstUser = createUserAndLoginUser();
    String userId = firstUser.get("userId");
    String token = firstUser.get("token");
    String cookie = firstUser.get("cookie");
    String fieldValue = firstUser.get(fieldName);

    //EDIT
    Map<String, String> editData = new HashMap<>();
    editData.put(fieldName, "1");
    String url = BASE_URL + userId;
    Response responseEditData = apiCoreRequest.makePutRequest(url,token,cookie,editData);

    Assertions.assertResponseCodeEquals(responseEditData,400);
    Assertions.assertJsonByName(responseEditData,"error","Too short value for field " + fieldName);

    //GET
    Response responseUserData = apiCoreRequest.makeGetRequest(url,token,cookie);

    Assertions.assertJsonByName(responseUserData, fieldName, fieldValue);
  }

  //Тест создает пользователя, авторизуется под этим пользователем и меняет параметр firstName на новое.
  //В конце проверяем, что имя было изменено корректно.
  @Test
  @Story("This test changes name by authorization user")
  @Description("This test changes name by authorization user")
  @DisplayName("Positive: edit name of authorization user")
  public void editNameByAuthUserTest() {
    //GENERATE AND LOGIN USER
    Map<String, String> firstUser = createUserAndLoginUser();
    String userId = firstUser.get("userId");
    String token = firstUser.get("token");
    String cookie = firstUser.get("cookie");

    //EDIT
    String newName = "Changed name";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", newName);
    String url = BASE_URL + userId;
    apiCoreRequest.makePutRequest(url,token,cookie,editData);

    //GET
    Response responseUserData = apiCoreRequest.makeGetRequest(url,token,cookie);

    Assertions.assertJsonByName(responseUserData, "firstName", newName);
  }

  private Map<String,String> createUserAndLoginUser() {
    //GENERATE USER
    Map<String, String> userData = DataGenerator.getRegisrationData();
    JsonPath responseCreateAuth = apiCoreRequest
            .makePostRequest(BASE_URL,userData)
            .jsonPath();

    String userId = responseCreateAuth.getString("id");
    userData.put("userId",userId);

    //LOGIN USER
    Map<String, String> authData = new HashMap<>();
    authData.put("email", userData.get("email"));
    authData.put("password", userData.get("password"));
    Response responseGetAuth = apiCoreRequest.makePostRequest(URL_LOGIN, authData);
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    userData.put("token",token);
    userData.put("cookie",cookie);

    return userData;
  }
}
