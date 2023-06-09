# HappyDay

HappyDay - Монолитное приложение на spring boot. Создано для поднятия настроения пользователям посредством отправки им случайной мотивирующей фразы.

---

<details><summary><b>Описание функционала программы</b></summary>
   Функционал разделен на команды бота:
   
1) Для пользователей:

   - «/start» - В ответ пользователь получает приветственное сообщение с кнопкой для получения фразы. На этом этапе пользователь сохраняется в БД.
   
   - «/happy» - В ответ на комананду программа отправляет случайную фразу из БД пользователю. В случае, если база данных не заполненна, 
   происходит считывание файла с фразами и заполнение БД.

   - «/info» - В ответ пользователь получает сообщение с информацией о боте.
2) Для администратора:

   - "/sendAllTheUsers #" - Администратор отправляет всем пользователям сообщение. Вместо # админ пишет сообщение, которое хочет отправить.
   
   - "/update" - Администратор данной командой обновляет базу данных. Программа проверяет на наличие новых фраз в файле 
   Phrases.txt(Каждая фраза пишется на новой строке). Если новых фраз нет, администратор получает соответствующее уведомление.
   
   - Так же администратор может отправить боту контакт любого пользователя. Если его нет в бд, программа его сохранит.
</details>

#### Стек-технологий, используемый в приложении:
- [Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
- [Spring Boot 2.7.9](https://docs.spring.io/spring-boot/docs/2.7.9/api/)
- [Maven](https://maven.apache.org/)
- [Lombok](https://projectlombok.org/)
- Для тестирования приложения [DBMS H2](http://www.h2database.com/html/main.html)
- [DBMS PostgreSQL](https://www.postgresql.org/)
- [telegrambots 5.6.0](https://github.com/rubenlagus/TelegramBots)
