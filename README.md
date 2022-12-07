# SearchEngineWeb
Стэк технологий: springBoot , RESTapi , Crud , Hibernate , JPA , Thymeleaf  , MySQL , ForkJoinPool , JUnit , Maven.
Приложение индексирует несколько сайтов (которые указаны в кнофигурационном файле) и записывает в бд данные. Преборазует все слова на странице в лемму(Им.п ед.ч) и считает кол-во леммы на странице(нужно для определения релевантности стр.). Выдает список ссылок по запросу сортированных по релевантности.
1) Статистика 
![image](https://user-images.githubusercontent.com/85135441/204812579-ac58d88c-7633-4950-91c3-0e52565cec13.png)
![image](https://user-images.githubusercontent.com/85135441/205484720-53003abb-394c-4c01-98ec-e6c7bd31ee54.png)
2) Функция запуска/принудительной остановки индексации и функция добавления страницы, если страница есть на сайте.
![image](https://user-images.githubusercontent.com/85135441/205484377-db3a1aef-f772-474c-b3a3-c48597f0364a.png)
![image](https://user-images.githubusercontent.com/85135441/205484379-75c7a0b0-0933-4533-afcf-d607b3db10e5.png)
![image](https://user-images.githubusercontent.com/85135441/205484834-6d6d2396-528c-4970-8e8f-d1723649d522.png)
3) Поиск с подсветкой ключевых слов , с кликабельно ссылкой и с открывком из текста .
![image](https://user-images.githubusercontent.com/85135441/205484774-d001083a-9dc5-4608-bd73-a402aac85953.png)
![image](https://user-images.githubusercontent.com/85135441/205484775-7a74c6e1-fd9d-4ef2-b685-8a5e3bc039ee.png)

