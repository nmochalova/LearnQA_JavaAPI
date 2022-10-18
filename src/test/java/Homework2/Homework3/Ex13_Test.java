package Homework2.Homework3;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/*
Написать параметризированный тест, который должен брать из дата-провайдера User Agent и ожидаемые значения,
делать GET-запрос с этим User Agent и убеждаться, что результат работы нашего метода правильный -
т.е. в ответе ожидаемое значение всех трех полей.
Список User Agent и ожидаемых значений можно найти по этой ссылке: https://gist.github.com/KotovVitaliy/138894aa5b6fa442163561b5db6e2e26
Ответом к задаче должен быть список из тех User Agent, которые вернули неправильным хотя бы один параметр,
с указанием того, какой именно параметр неправильный.
 */
@Epic("Homework")
public class Ex13_Test {
    @ParameterizedTest
    @MethodSource("getUserAgent")
    public void userAgentTest(String userAgent, String expectedPlatform, String expectedBrowser, String expectedDevice) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);

        JsonPath response = RestAssured
                .given()
                .headers(headers)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        String platform = response.getString("platform");
        String browser = response.getString("browser");
        String device = response.getString("device");

        if (!platform.equals(expectedPlatform))
            printError(userAgent,"platform",platform);

        if (!browser.equals(expectedBrowser))
            printError(userAgent,"browser",browser);

        if (!device.equals(expectedDevice))
            printError(userAgent,"device",device);
    }

    private void printError(String userAgent, String parameterName, String parameterValue) {
        System.out.println("Error User-Agent: " + userAgent);
        System.out.println(String.format("Error parameter: %s = %s \n" , parameterName, parameterValue));
    }

    private static Stream<Arguments> getUserAgent() {
        return Stream.of(
            arguments("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                    "Mobile","No","Android"),
                arguments("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile","Chrome","iOS"),
                arguments("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot","Unknown","Unknown"),
                arguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web","Chrome","No"),
                arguments("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile","No","iPhone")
        );
    }
}
