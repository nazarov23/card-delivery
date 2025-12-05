package ru.netology.delivery.data;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class DataGenerator {
    private static final Faker faker = new Faker(new Locale("ru"));

    public static String generateDate(int daysToAdd) {
        return LocalDate.now().plusDays(daysToAdd)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateCity() {
        String[] cities = {
                "Москва", "Санкт-Петербург", "Казань", "Екатеринбург",
                "Новосибирск", "Краснодар", "Владивосток", "Калининград"
        };
        return cities[faker.random().nextInt(cities.length)];
    }

    public static String generateName() {
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public static String generatePhone() {
        // Генерируем валидный телефон в формате +7XXXXXXXXXX
        return "+7" + faker.numerify("#########");
    }

    public static String generateInvalidPhone() {
        // Генерируем ЗАВЕДОМО невалидный телефон - всегда слишком короткий
        // Это позволяет точно знать, какая ошибка должна появиться в тесте
        return faker.numerify("###"); // Всегда 3 цифры - заведомо невалидно
    }
}