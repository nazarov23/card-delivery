# Автотесты для сервиса доставки карт

[![Java CI with Gradle](https://github.com/nazarov23/card-delivery/actions/workflows/appveyor.yml/badge.svg)](https://github.com/nazarov23/card-delivery/actions/workflows/appveyor.yml)

## Описание
Автоматизированные тесты для проверки функциональности заказа доставки банковской карты.

## Технологии
- Java 11
- JUnit 5
- Selenide
- Lombok
- Faker
- Gradle

## Запуск тестов

### Локальный запуск
1. Убедитесь, что установлены Java 11 и Gradle
2. Запустите SUT: `java -jar artifacts/app-replan-delivery.jar`
3. Запустите тесты: `./gradlew test`

### Запуск в CI
Тесты автоматически запускаются при пуше в main/master ветку через GitHub Actions.

## Тестовые сценарии
- Успешное планирование и перепланирование встречи
- Валидация города из списка доступных
- Валидация имени (только кириллица)
- Валидация номера телефона
- Проверка обязательности согласия с условиями