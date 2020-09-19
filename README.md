Демонстрационный проект на фрейморке Spring Boot на языке Kotlin - сервис коротких ссылок.

_Тестовый запуск сервиса_

В системе должны быть установлены: `git, docker, docker-compose`

Последовательно выполнить:

`~$ git clone https://github.com/puritanin/spring-webflux-short-links-service`

`~$ cd spring-webflux-short-links-service`

`~$ sudo chmod u+x gradlew`

`~$ ./gradlew bootJar`

`~$ sudo docker-compose up`

После запуска сервис доступен по адресу: http://localhost:8075
