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
        UserInfo user = new UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                DataGenerator.generatePhone(),
                DataGenerator.generateDate(3) // Добавляем дату как 4-й параметр
        );

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=success-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно"));
    }

    @Test
    void shouldShowErrorForInvalidPhone() {
        UserInfo user = new UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                DataGenerator.generateInvalidPhone(),
                DataGenerator.generateDate(3) // Добавляем дату
        );

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=phone].input_invalid")
                .shouldBe(Condition.visible, Duration.ofSeconds(5));
    }

    @Test
    void shouldRescheduleMeeting() {
        // Первая запись
        UserInfo firstUser = new UserInfo(
                DataGenerator.generateCity(),
                DataGenerator.generateName(),
                DataGenerator.generatePhone(),
                DataGenerator.generateDate(3)
        );

        $("[data-test-id=city] input").setValue(firstUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(firstUser.getDate());
        $("[data-test-id=name] input").setValue(firstUser.getName());
        $("[data-test-id=phone] input").setValue(firstUser.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=success-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));

        // Попытка перепланировать
        open("http://localhost:9999");

        UserInfo secondUser = new UserInfo(
                firstUser.getCity(),
                firstUser.getName(),
                firstUser.getPhone(),
                DataGenerator.generateDate(5) // Другая дата
        );

        $("[data-test-id=city] input").setValue(secondUser.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(secondUser.getDate());
        $("[data-test-id=name] input").setValue(secondUser.getName());
        $("[data-test-id=phone] input").setValue(secondUser.getPhone());
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=replan-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));

        $("[data-test-id=replan-notification] button").click();

        $("[data-test-id=success-notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));
    }
}