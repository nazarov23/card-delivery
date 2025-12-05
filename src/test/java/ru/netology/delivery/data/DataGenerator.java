package ru.netology.delivery.data;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class DataGenerator {
    private static Faker faker = new Faker(new Locale("ru"));

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
        return faker.phoneNumber().phoneNumber().replaceAll("[^0-9+]", "");
    }

    public static String generateInvalidPhone() {
        // Генерируем ЗАВЕДОМО невалидный телефон - слишком короткий номер
        // Это гарантирует предсказуемый результат теста
        return faker.numerify("####"); // Всегда 4 цифры - заведомо невалидно
    }
}