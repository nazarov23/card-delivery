package ru.netology.delivery.data;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

@UtilityClass
public class DataGenerator {
    private static Faker faker = new Faker(new Locale("ru"));
    private static Random random = new Random();

    public static String generateDate(int daysToAdd) {
        return LocalDate.now().plusDays(daysToAdd).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateCity() {
        String[] cities = {
                "Москва", "Санкт-Петербург", "Казань", "Екатеринбург",
                "Новосибирск", "Краснодар", "Владивосток", "Калининград"
        };
        return cities[random.nextInt(cities.length)];
    }

    public static String generateName() {
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public static String generatePhone() {
        // Генерируем валидный российский номер
        String[] operators = {"901", "902", "903", "904", "905", "906", "915", "916", "917", "919", "981", "982", "983", "984", "985", "986", "987", "988", "989"};
        String operator = operators[random.nextInt(operators.length)];
        String number = faker.numerify("#######");
        return "+7" + operator + number;
    }

    public static String generateInvalidPhone() {
        // Генерируем заведомо невалидный номер
        return "123";
    }

    public static UserInfo generateUser() {
        return new UserInfo(generateCity(), generateName(), generatePhone());
    }
}