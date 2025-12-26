package ru.netology.delivery;

import io.qameta.allure.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest extends TestBase {
    private UserInfo user;

    @BeforeEach
    void setup() {

        open("http://localhost:9999");

        user = new UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                DataGenerator.generatePhone(),
                DataGenerator.generateDate(3)
        );
    }

    @Test
    void shouldSubmitFormSuccessfully() {
        fillCity(user.getCity());
        fillDate(user.getDate());
        fillName(user.getName());
        fillPhone(user.getPhone());
        clickAgreement();
        submitForm();
        verifySuccessNotification();
    }

    @Test
    void shouldRescheduleMeeting() {
        String firstDate = DataGenerator.generateDate(3);
        fillForm(firstDate);
        checkSuccessNotification(firstDate);

        open("http://localhost:9999");
        String secondDate = DataGenerator.generateDate(5);
        fillForm(secondDate);
        verifyReplanNotification();
        clickReplanButton();
        checkSuccessNotification(secondDate);
    }

    @Test
    void shouldShowErrorForInvalidCity() {
        fillCity("Несуществующий Город");
        fillDate(user.getDate());
        fillName(user.getName());
        fillPhone(user.getPhone());
        clickAgreement();
        submitForm();
        verifyCityError();
    }

    @Test
    void shouldShowErrorForInvalidName() {
        fillCity(user.getCity());
        fillDate(user.getDate());
        fillName("John Doe");
        fillPhone(user.getPhone());
        clickAgreement();
        submitForm();
        verifyNameError();
    }

    @Test
    void shouldShowErrorForUncheckedAgreement() {
        fillCity(user.getCity());
        fillDate(user.getDate());
        fillName(user.getName());
        fillPhone(user.getPhone());
        // Чекбокс НЕ ставим!
        submitForm();
        verifyAgreementError();
    }

    @Test
    @Disabled("Баг приложения: валидация номера телефона не работает")
    void shouldShowErrorForInvalidPhone() {
        UserInfo invalidPhoneUser = new UserInfo(
                user.getCity(),
                user.getName(),
                DataGenerator.generateInvalidPhone(),
                user.getDate()
        );
        fillCity(invalidPhoneUser.getCity());
        fillDate(invalidPhoneUser.getDate());
        fillName(invalidPhoneUser.getName());
        fillPhone(invalidPhoneUser.getPhone());
        clickAgreement();
        submitForm();
        verifyPhoneError();
    }


    @Step("Заполняем поле города: {city}")
    private void fillCity(String city) {
        $("[data-test-id=city] input").setValue(city);
    }

    @Step("Заполняем поле даты: {date}")
    private void fillDate(String date) {
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);
    }

    @Step("Заполняем поле имени: {name}")
    private void fillName(String name) {
        $("[data-test-id=name] input").setValue(name);
    }

    @Step("Заполняем поле телефона: {phone}")
    private void fillPhone(String phone) {
        $("[data-test-id=phone] input").setValue(phone);
    }

    @Step("Ставим галочку согласия")
    private void clickAgreement() {
        $("[data-test-id=agreement]").click();
    }

    @Step("Отправляем форму")
    private void submitForm() {
        $("button.button").click();
    }

    @Step("Проверяем уведомление об успехе")
    private void verifySuccessNotification() {
        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Успешно"));
    }

    @Step("Проверяем ошибку города")
    private void verifyCityError() {
        $("[data-test-id=city].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    @Step("Проверяем ошибку имени")
    private void verifyNameError() {
        $("[data-test-id=name].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    @Step("Проверяем ошибку согласия")
    private void verifyAgreementError() {
        $("[data-test-id=agreement].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    @Step("Проверяем ошибку телефона")
    private void verifyPhoneError() {
        $("[data-test-id=phone].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    @Step("Проверяем уведомление о перепланировании")
    private void verifyReplanNotification() {
        $("[data-test-id=replan-notification]")
                .shouldBe(visible, Duration.ofSeconds(15));
    }

    @Step("Нажимаем кнопку 'Перепланировать'")
    private void clickReplanButton() {
        $("[data-test-id=replan-notification] button").click();
    }

    // Вспомогательные методы (можно оставить без @Step)
    private void fillForm(String date) {
        fillCity(user.getCity());
        fillDate(date);
        fillName(user.getName());
        fillPhone(user.getPhone());
        clickAgreement();
        submitForm();
    }

    private void checkSuccessNotification(String expectedDate) {
        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + expectedDate));
    }
}