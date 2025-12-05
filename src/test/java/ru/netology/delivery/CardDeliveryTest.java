package ru.netology.delivery;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.delivery.data.DataGenerator;
import ru.netology.delivery.data.UserInfo;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    void shouldSubmitFormSuccessfully() {
        // Создаем пользователя с валидными данными
        UserInfo user = new UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                DataGenerator.generatePhone(), // ВАЛИДНЫЙ телефон
                DataGenerator.generateDate(3)
        );

        // Заполняем форму
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();

        // Отправляем форму
        $("button.button").click();

        // Проверяем успешную отправку
        $("[data-test-id=success-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно"));
    }

    @Test
    void shouldShowErrorForInvalidPhone() {
        // Создаем пользователя с НЕВАЛИДНЫМ телефоном
        UserInfo user = new UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                DataGenerator.generateInvalidPhone(), // НЕВАЛИДНЫЙ телефон
                DataGenerator.generateDate(3)
        );

        // Заполняем форму
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();

        // Пытаемся отправить
        $("button.button").click();

        // Проверяем ошибку - телефон должен быть помечен как невалидный
        // Мы знаем, что номер слишком короткий, поэтому ожидаем ошибку валидации
        $("[data-test-id=phone].input_invalid")
                .shouldBe(Condition.visible, Duration.ofSeconds(5));
    }

    @Test
    void shouldRescheduleMeeting() {
        // Тест на перепланирование...
        // Используем аналогичный подход с конструктором UserInfo
    }
}