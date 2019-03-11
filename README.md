# GesaHu-Android
[![](https://tokei.rs/b1/github/K0bin/GesaHu-Android)](https://github.com/K0bin/GesaHu-Android)

Erlaubt einen einfachen Zugriff auf viele Features der GesaHu Webseite.

## Vertretungsplan
Zeigt den Vertretungsplan in einer übersichtlichen Liste und hebt relevante Stunden hervor.

Optional kann die App täglich zu einer einstellbaren Uhrzeit oder nach jeder Stunde bei Vertretungsstunden benachrichtigen. Dazu wird ein Alarm erstellt, der zur angegebenen Zeit einen Broadcast Receiver ausführt.

## Kalender
Schreibt relevante Schultermine wie Klausuren und Prüfungen automatisch in den Gerätekalender.
Implementiert als SyncAdapter

## Boards
Zeigt Stunden und Noten eines Kursboards an.

## Code
Die App ist überwiegend in Kotlin geschrieben und auf dem Model-View-Presenter Entwurfsmuster aufgebaut.
Verwendete Libraries:
* [Room](https://developer.android.com/topic/libraries/architecture/room.html) & [LiveData](https://developer.android.com/topic/libraries/architecture/livedata.html) für die Datenbank
* [Dagger 2](https://google.github.io/dagger/) für Dependency Injection
* [OkHttp](http://square.github.io/okhttp/) & [Retrofit](http://square.github.io/retrofit/) für die GET API
* [Android KTX](https://github.com/android/android-ktx) & [Anko](https://github.com/Kotlin/anko) für nützliche Android Hilfsfunktionen in Kotlin
* [Android Support Libraries](http://developer.android.com/tools/support-library/index.html)
* [Joda-Time](http://www.joda.org/joda-time/) für immutable Datums- und Zeitklassen in Java 7
* [LeakCanary](https://github.com/square/leakcanary) um Memory Leaks zu erkennen
* [ErrorView](https://github.com/xiprox/ErrorView) falls eine Liste leer ist
* [AboutLibraries](https://github.com/mikepenz/AboutLibraries) für eine Übersicht der verwendeten Libraries und derer Lizensen
* [AppIntro](https://github.com/apl-devs/AppIntro) für die Einleitung beim ersten Start

## Playstore
<a href="https://play.google.com/store/apps/details?id=rhedox.gesahuvertretungsplan"><img alt="Play Store badge" src="https://play.google.com/intl/en_us/badges/images/generic/de-play-badge.png" width="185"></img></a>
