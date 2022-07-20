bookmarkr -- reactive bookmark management
=========================================
![build status](https://github.com/FilipDisvolvas/bookmarkr/actions/workflows/test-coverage-master.yml/badge.svg?branch=master)
![test status](https://raw.githubusercontent.com/FilipDisvolvas/bookmarkr/master/.github/badges/jacoco.svg)

Filips kleine Spielwiese. Bookmarks sollen mobile first zentral verwaltet werden.
Tech Stack:
* Gradle
* Kotlin / Java 17
* Spring Boot mit Webflux (reactive-Kram)
* verschiedene Security Foo, die interessant sein könnten
* JUnit 5
* MongoDB
* [Bootstrap](https://getbootstrap.com/)
* Vielleicht verwende ich AngularJS, vielleicht auch einfach nur [PJAX](https://github.com/defunkt/jquery-pjax). Mal sehen...

Später soll noch eine Volltest-Suche und eine automatische, verzeichnisübergreifende Gruppierung eingebaut werden.

Das Projekt fängt gerade erst an und wenn es nachher wirklich interessant wird, dann wird es offline genommen. ;-)

Frontend ist noch nicht wirklich da. Dafür ist die Test-Abdeckung schön.

Ein [kleiner Workflow in GitHub Actions](https://github.com/FilipDisvolvas/bookmarkr/actions) ist auch vorhanden.
Lässt immerhin die Tests durchlaufen und benachrichtigt, wenn sich der master- oder feature-Branch nicht bauen lässt.
Die Badges in diesem README.md werden automagisch generiert.

Lokales Testen
--------------

Aktuell ist hauptsächlich das Test-Profil im Spring-Projekt von Bedeutung.
Da die In-Memory-Datenbank von MongoDB manchmal nicht sauber herunterfährt,
greife ich auf einen Docker-Container zurück.

```bash
docker pull mongo
docker pull mongo-express
docker run -d --network host -e MONGO_INITDB_ROOT_USERNAME=mongoadmin -e MONGO_INITDB_ROOT_PASSWORD=secret mongo
docker run -d -p 9081:8081 -e ME_CONFIG_MONGODB_URL=mongodb://mongoadmin:secret@172.17.0.1:27017  mongo-express
```
<sup><sup>(Echte Passwörter würde ich natürlich niemals versionieren... ;-) )</sup></sup>

```bash
sudo archlinux-java set java-17-openjdk
SPRING_PROFILES_ACTIVE=test ./gradlew clean build test jacocoTestReport
...
Test Coverage:
    - Class Coverage: 86.2%
    - Method Coverage: 65%
    - Branch Coverage: 78.6%
    - Line Coverage: 87.8%
    - Instruction Coverage: 79.9%
    - Complexity Coverage: 64.7%
```
<br /><br />

**Work in progress. To be continued...**