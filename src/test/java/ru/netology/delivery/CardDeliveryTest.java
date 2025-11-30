package ru.netology.delivery;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {
    private UserInfo user;

    @BeforeEach
    void setup() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.headless = true;
        Configuration.timeout = 15000;

        open("http://localhost:9999");
        user = DataGenerator.generateUser();
    }

    @Test
    void shouldSuccessfullyPlanAndReplanMeeting() {
        // Первое планирование
        fillForm(DataGenerator.generateDate(4));
        checkSuccessNotification(DataGenerator.generateDate(4));

        // Перепланирование
        replanMeeting(DataGenerator.generateDate(7));
        checkSuccessNotification(DataGenerator.generateDate(7));
    }

    @Test
    void shouldShowErrorIfCityNotFromList() {
        fillFormWithInvalidData("Несуществующий Город", user.getName(), user.getPhone());
        checkFieldError("[data-test-id=city]", "Доставка в выбранный город недоступна");
    }

    @Test
    void shouldShowErrorIfNameInvalid() {
        fillFormWithInvalidData(user.getCity(), "John Doe", user.getPhone());
        checkFieldError("[data-test-id=name]", "Имя и Фамилия указаные неверно");
    }

    @Test
    void shouldShowErrorIfPhoneInvalid() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        $("[data-test-id=name] input").setValue(user.getName());

        // Невалидный телефон
        $("[data-test-id=phone] input").setValue("123");
        $("[data-test-id=agreement]").click();
        $x("//*[text()='Запланировать']").click();

        // Проверяем что форма не отправилась
        $("[data-test-id=success-notification]").shouldNotBe(visible, Duration.ofSeconds(10));
    }

    @Test
    void shouldShowErrorIfAgreementNotChecked() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        // Чекбокс НЕ отмечаем
        $x("//*[text()='Запланировать']").click();

        $("[data-test-id=agreement].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    private void fillForm(String date) {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $x("//*[text()='Запланировать']").click();
    }

    private void fillFormWithInvalidData(String city, String name, String phone) {
        $("[data-test-id=city] input").setValue(city);
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $("[data-test-id=agreement]").click();
        $x("//*[text()='Запланировать']").click();
    }

    private void checkFieldError(String fieldSelector, String expectedError) {
        $(fieldSelector + ".input_invalid")
                .shouldBe(visible, Duration.ofSeconds(10));
        $(fieldSelector + " .input__sub")
                .shouldBe(visible)
                .shouldHave(text(expectedError));
    }

    private void replanMeeting(String newDate) {
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(newDate);
        $x("//*[text()='Запланировать']").click();

        // Увеличиваем время ожидания для перепланирования
        $("[data-test-id=replan-notification]")
                .shouldBe(visible, Duration.ofSeconds(20));
        $x("//*[text()='Перепланировать']").click();
    }

    private void checkSuccessNotification(String expectedDate) {
        // Увеличиваем время ожидания и делаем проверку более надежной
        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(20));
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + expectedDate));
    }
}