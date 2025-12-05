package ru.netology.delivery;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
        Configuration.browserSize = "1920x1080";
        open("http://localhost:9999");

        // Создаем тестового пользователя с валидными данными
        user = new UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                DataGenerator.generatePhone(),
                DataGenerator.generateDate(3)
        );
    }

    @Test
    void shouldSubmitFormSuccessfully() {
        // Заполняем форму
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();

        // Отправляем форму
        $("button.button").click();

        // Проверяем успешную отправку
        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Успешно"));
    }

    @Test
    void shouldRescheduleMeeting() {
        // Первая запись
        String firstDate = DataGenerator.generateDate(3);

        fillForm(firstDate);
        checkSuccessNotification(firstDate);

        // Перепланирование на другую дату
        open("http://localhost:9999");

        String secondDate = DataGenerator.generateDate(5);

        fillForm(secondDate);

        // Проверяем предложение перепланировать
        $("[data-test-id=replan-notification]")
                .shouldBe(visible, Duration.ofSeconds(15));

        // Нажимаем "Перепланировать"
        $("[data-test-id=replan-notification] button").click();

        // Проверяем успешное перепланирование
        checkSuccessNotification(secondDate);
    }

    @Test
    void shouldShowErrorForInvalidCity() {
        // Заполняем форму с невалидным городом
        $("[data-test-id=city] input").setValue("Несуществующий Город");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();

        // Пытаемся отправить
        $("button.button").click();

        // Проверяем ошибку города
        $("[data-test-id=city].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    @Test
    void shouldShowErrorForInvalidName() {
        // Заполняем форму с именем на латинице
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(user.getDate());
        $("[data-test-id=name] input").setValue("John Doe");
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();

        // Пытаемся отправить
        $("button.button").click();

        // Проверяем ошибку имени
        $("[data-test-id=name].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    @Test
    void shouldShowErrorForUncheckedAgreement() {
        // Заполняем форму без чекбокса
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        // Чекбокс НЕ ставим!

        // Пытаемся отправить
        $("button.button").click();

        // Проверяем ошибку чекбокса
        $("[data-test-id=agreement].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    @Test
    @Disabled("Баг приложения: валидация номера телефона не работает. " +
            "Приложение принимает невалидные номера (например, '1234'). " +
            "Создан issue: #1")
    void shouldShowErrorForInvalidPhone() {
        // Создаем пользователя с невалидным телефоном
        UserInfo invalidPhoneUser = new UserInfo(
                user.getCity(),
                user.getName(),
                DataGenerator.generateInvalidPhone(), // Например, "1234"
                user.getDate()
        );

        // Заполняем форму
        $("[data-test-id=city] input").setValue(invalidPhoneUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(invalidPhoneUser.getDate());
        $("[data-test-id=name] input").setValue(invalidPhoneUser.getName());
        $("[data-test-id=phone] input").setValue(invalidPhoneUser.getPhone());
        $("[data-test-id=agreement]").click();

        // Пытаемся отправить
        $("button.button").click();

        // Ожидаем ошибку (но из-за бага приложения ее нет)
        $("[data-test-id=phone].input_invalid")
                .shouldBe(visible, Duration.ofSeconds(5));
    }

    // Вспомогательные методы

    private void fillForm(String date) {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();
    }

    private void checkSuccessNotification(String expectedDate) {
        $("[data-test-id=success-notification]")
                .shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + expectedDate));
    }
}