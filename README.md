# АВТОМАТИЗАЦИЯ ТЕСТИРОВАНИЯ REST API НА JAVA
**Содержание проекта:** 
- API-тесты с использованием RestAssured. [Тестовое API.](https://playground.learnqa.ru/api/map)  
- Allure-отчеты
- Запуск тестов в docker-compose

## Ресурсы:

[Online JSON Viewer](http://jsonviewer.stack.hu/)

[Мои лекции](https://software-testing.ru/lms/course/view.php?id=2499#section-4)

[Документация по установке Allure](https://docs.qameta.io/allure-report/#_get_started)

[Документация по установке Docker](https://docs.docker.com/engine/install/)

## Настройка docker-compose

1. Загружаем docker-образ

> docker pull maven:3.6.3-openjdk-14

В моем проекте используются версии:
- maven 3.8.4
- openjdk-18

2. Создаем в корне проекта файл Dockerfile для создания собственного образа для запуска тестов внутри контейнера

````
FROM maven:3.6.3-openjdk-14
WORKDIR /tests
COPY . .
CMD mvn clean test
````
где
- Создаем свой образ из образа maven:3.6.3-openjdk-14
- Определяем рабочую директорию внутри образа /tests
- Внутрь образа копируем все содержимое проекта: все из текущей директории host-машины в текущую директорию нового образа (. .)
- Далее запускаем команду mvn clean test для скачивания нужных библиотек в наш образ и запуска самих тестов.

3. Создаем нужный нам образ в Terminal. Выполняем команду в корне нашего проекта:

> docker build -t java_api_tests .

4. Запускаем созданный образ командой

> docker run --rm --mount type=bind,src=$(pwd),target=/tests/ java_api_tests

где
- docker run -  команда запуска контейнера
- --rm - удалени контейнера после того как тесты отработают
- --mount type=bind,src=$(pwd),target=/tests/ - подтягиваем тесты из текущего проекта прямо в контейнер. При следующем запуске будет запускаться уже новая версия тестов.
- java_api_tests - название образа, от которого мы стартуем контейнер

5. В корне проекта создадим конфигурационный файл docker-compose.yml, который позволит генерировать образ и запускать контейнер одной командой.
````
version: "3"

services:
test_runner:
build: .
image: java_api_tests
container_name: java_runner_works
volumes:
- .:/tests/
````
где
- build: . - создаем образ docker-файла в той же директории
- image: java_api_tests - название образа
- container_name: java_runner_works -  название контейнера
volumes:
- .:/tests/ - дублируем переменную окружения: откуда внутри конейнера брать тесты и куда потом класть внутри контейнера, т.е. из брать текущей директории (.) и класть в /tests/

6. Запускаем

> docker-compose up --build

--build - означает, что даже если обораз существует его надо создать заново.

В итоге : создается образ, скачиваются зависимости и запускаются тесты.

7. Если у нас не менялся образ и не менялись зависимости, то можно запустить образ еще раз командой:

> docker-compose up

В этом случае новый образ создаваться не будет, а просто запустятся тесты.

8. В корне проекта создается папака allure-result с копией отчетов.
   Для запуска Allure-отчетов во вкладке Terminal: 
> allure serve allure-results/







