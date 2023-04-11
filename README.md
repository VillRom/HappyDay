# HappyDay

HappyDay - Монолитное приложение на spring boot. Создано для поднятия настроения пользователям по средством отправки им случайной мотивирующей фразы.

---

<details><summary><b>Описание функционала программы</b></summary>
   
1) Функционал разделен на команды бота:

   - «/start» - В ответ пользователь получает приветственное сообщение с кнопкой для получения фразы. На этом этапе пользователь сохраняется в БД.
   
   - «/happy» - В ответ на комананду программа отправляет случайную фразу из БД пользователю. В случае, если база данных не заполненна, 
   происходит считывание файла с фразами и заполнение БД.

   - «/info» - В ответ пользователь получает сообщение с информацией о боте. 
</details>

#### Стек-технологий, используемый в приложении:
- [Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
- [Spring Boot 2.7.9](https://docs.spring.io/spring-boot/docs/2.7.9/api/)
- [Maven](https://maven.apache.org/)
- [Lombok](https://projectlombok.org/)
- Для тестирования приложения [DBMS H2](http://www.h2database.com/html/main.html)
- [DBMS PostgreSQL](https://www.postgresql.org/)
- [telegrambots 5.6.0](https://github.com/rubenlagus/TelegramBots)
