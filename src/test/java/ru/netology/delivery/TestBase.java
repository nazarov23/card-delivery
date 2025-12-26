package ru.netology.delivery;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {

    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)      // Делать скриншоты при падении тестов
                .savePageSource(true)   // Сохранять HTML страниц при падении
        );

        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 15000;
    }
}