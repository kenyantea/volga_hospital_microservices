# volga_hospital_microservices

Отборочное задание на Волга-ИТ'24

[Задание](task.pdf)

## Основное задание
1) Account URL: http://localhost:8080/swagger-ui.html#/
2) Hospital URL: http://localhost:8081/swagger-ui/index.html
3) Timetable URL: http://localhost:8082/swagger-ui/index.html
4) Document URL: http://localhost:8083/swagger-ui/index.html

Примечание. Вводить JWT-токен в Swagger для микросервиса аккаунтов необходимо в формате "Bearer <ваш_токен>". 
В Swagger для остальных микросервисов — просто в формате "<ваш_токен>". Это связано с разными версиями Swagger.

## Развертывание
Командой `docker-compose up -d`. 
Если вдруг выдаются ошибки, тогда сначала попробовать выполнить команду `mvn clean package` 
для каждого из микросервисов (другими словами, пересобрать), а затем снова выполнить `docker-compose up -d` или `docker-compose up -d --build`.

## Примечания

Указанные в ТЗ пользователи добавляются в БД по умолчанию. Таблица логинов и паролей приведена в таблице ниже.

|         Роль         |  Логин  | Пароль  |
|:--------------------:|:-------:|:-------:|
|    Администратор     |  admin  |  admin  |
|       Менеджер       | manager | manager |
|         Врач         | doctor  | doctor  |
| Обычный пользователь |  user   |  user   |

При внесенных в код изменениях стоит пересобрать микросервис(ы) с помощью `mvn clean package`, 
а затем запустить контейнеры командой `docker compose -d --build` 
