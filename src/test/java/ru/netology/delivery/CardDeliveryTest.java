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
        $("[data-test-id=city] input").setValue("Несуществующий Город");
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $x("//*[text()='Запланировать']").click();

        // Проверяем ошибку города
        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldShowErrorIfNameInvalid() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(DataGenerator.generateDate(4));
        $("[data-test-id=name] input").setValue("John Doe"); // Латинские буквы
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $x("//*[text()='Запланировать']").click();

        // Проверяем ошибку имени
        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
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

        // Ждем и проверяем, что успеха нет
        sleep(2000);
        $("[data-test-id=success-notification]").shouldNotBe(visible);

        // Форма должна остаться visible
        $("[data-test-id=city]").shouldBe(visible);
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

        // Проверяем ошибку согласия
        $("[data-test-id=agreement].input_invalid .checkbox__text")
                .shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
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

    private void replanMeeting(String newDate) {
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(newDate);
        $x("//*[text()='Запланировать']").click();

        // Проверяем уведомление о перепланировании
        $("[data-test-id=replan-notification]")
                .shouldBe(visible, Duration.ofSeconds(15));
        $x("//*[text()='Перепланировать']").click();
    }

    private void checkSuccessNotification(String expectedDate) {
        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + expectedDate));
    }
}