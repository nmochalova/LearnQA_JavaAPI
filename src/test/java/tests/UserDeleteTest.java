package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

  //Авторизоваться пользователем id=2, пробовать удалить его. Убедиться, что система не даст его удалить.
  @Test
  @Story("Delete user ID 2")
  @Description("Cannot delete user ID 2")
  @DisplayName("Negative: delete user ID 2")
  public void deleteTestUserTest() {
    //AUTHORIZATION USER ID 2
    Map<String,String> authData = authTestUser();
    String cookie = authData.get("cookie");
    String token = authData.get("token");
    String userId = authData.get("userId");

    //DELETE
    Response responseDelete = apiCoreRequest.makeDeleteRequest(BASE_URL+userId,token,cookie);

    Assertions.assertResponseCodeEquals(responseDelete,400);
    Assertions.assertResponseTextEquals(responseDelete,"Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
  }

  //Создать пользователя, авторизоваться из-под него, удалить,
  //затем попробовать получить его данные по ID и убедиться, что пользователь действительно удален.
  @Test
  @Story("This test deletes the authorized user")
  @Description("This test deletes the authorized user")
  @DisplayName("Positive: delete authorized user")
  public void deleteAuthUserTest() {
    //GENERATE AND LOGIN USER
    Map<String, String> firstUser = createUserAndLoginUser();
    String userId = firstUser.get("userId");
    String token = firstUser.get("token");
    String cookie = firstUser.get("cookie");

    //DELETE
    Response responseDelete = apiCoreRequest.makeDeleteRequest(BASE_URL+userId,token,cookie);
    Assertions.assertResponseCodeEquals(responseDelete,200);

    //GET
    Response responseUserData = apiCoreRequest.makeGetRequest(BASE_URL+userId,token,cookie);
    Assertions.assertResponseCodeEquals(responseUserData,404);
    Assertions.assertResponseTextEquals(responseUserData,"User not found");
  }

  //Удалить пользователя, будучи авторизованными другим пользователем.
  @Test
  @Story("Delete user while being logged in by another user")
  @Description("This test deletes user while being logged in by another user")
  @DisplayName("Negative: delete user by authorized user")
  public void deleteUserByAuthUserTest() {
    //GENERATE FIRST USER (NOT LOGIN)
    Map<String, String> userData = DataGenerator.getRegisrationData();
    JsonPath responseCreateAuth = apiCoreRequest
            .makePostRequest(BASE_URL,userData)
            .jsonPath();
    responseCreateAuth.prettyPrint();
    String firstUserId = responseCreateAuth.getString("id");

    //GENERATE AND LOGIN SECOND USER
    Map<String, String> firstUser = createUserAndLoginUser();
    String secondUserId = firstUser.get("userId");
    String token = firstUser.get("token");
    String cookie = firstUser.get("cookie");

    //DELETE FIRST USER
    Response responseDelete = apiCoreRequest.makeDeleteRequest(BASE_URL+firstUserId,token,cookie);

    //GET FIRST USER
    Response responseFirstUserData = apiCoreRequest.makeGetRequest(BASE_URL+firstUserId,token,cookie);
    Assertions.assertJsonHasField(responseFirstUserData,"username");

    //GET SECOND USER
    Response responseSecondUserData = apiCoreRequest.makeGetRequest(BASE_URL+secondUserId,token,cookie);
    Assertions.assertJsonHasField(responseSecondUserData,"username");
  }
}
