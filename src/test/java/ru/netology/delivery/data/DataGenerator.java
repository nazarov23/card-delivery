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
        return faker.phoneNumber().phoneNumber().replaceAll("[^0-9+]", "");
    }

    public static String generateInvalidPhone() {
        // Случайная генерация невалидного телефона
        String[] invalidPatterns = {
                faker.numerify("####"),                           // слишком короткий
                faker.numerify("############"),                   // слишком длинный
                "8" + faker.numerify("##########"),              // начинается с 8 вместо +7
                "+8" + faker.numerify("##########"),             // +8 вместо +7
                "+" + faker.numerify("###########"),             // неправильный код страны
                faker.numerify("+7###abc####"),                  // содержит буквы
                "+7 " + faker.numerify("###") + " " + faker.numerify("###-##-##"), // с пробелами
                "+7(" + faker.numerify("###) ###-##-##")         // со скобками
        };

        return invalidPatterns[random.nextInt(invalidPatterns.length)];
    }

    public static UserInfo generateUser(boolean validPhone) {
        String phone = validPhone ? generatePhone() : generateInvalidPhone();
        return new UserInfo(generateCity(), generateName(), phone);
    }
}