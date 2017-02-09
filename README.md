# GesaHu-Android
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
* [OkHttp](http://square.github.io/okhttp/)
* [Retrofit](http://square.github.io/retrofit/)
* [ANKO](https://github.com/Kotlin/anko)
* [Kodein](https://github.com/SalomonBrys/Kodein)
* [Android Support Libraries](http://developer.android.com/tools/support-library/index.html)
* [Joda-Time](http://www.joda.org/joda-time/)
* [Jsoup](http://jsoup.org/)
* [LeakCanary](https://github.com/square/leakcanary)
* [ErrorView](https://github.com/xiprox/ErrorView)
* [AboutLibraries](https://github.com/mikepenz/AboutLibraries)

## Playstore
<a href="https://play.google.com/store/apps/details?id=rhedox.gesahuvertretungsplan"><img alt="Play Store badge" src="https://play.google.com/intl/en_us/badges/images/generic/de-play-badge.png" width="185"></img></a>
