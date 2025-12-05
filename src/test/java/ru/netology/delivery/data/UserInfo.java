package ru.netology.delivery.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {
    private String city;
    private String name;
    private String phone;
    private String date; // Добавляем поле date
}