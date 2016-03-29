# GesaHuVertretungsplan-Android
Lädt den Gesahu Vertretungsplan einer Woche aus der Webseite und zeigt diesen in einer Liste mit Tabs an.
Die Liste ist ein RecyclerView innerhalb eines Fragments, welches von einem ViewPager angezeigt wird.
Man kann auch ein bestimmtes Datum anzeigen, welches dann eine neue Activity mit Tabs öffnet.

Optional kann die App täglich zu einer einstellbaren Uhrzeit oder nach jeder Stunde bei Vertretungsstunden benachrichtigen. Dazu wird ein Alarm erstellt, der zur angegebenen Zeit einen Broadcast Receiver ausführt.

Verwendete Libraries:
* [OkHttp](http://square.github.io/okhttp/)
* [Retrofit](http://square.github.io/retrofit/)
* [Butterknife](http://jakewharton.github.io/butterknife/)
* [Android Support Libraries](http://developer.android.com/tools/support-library/index.html)
* [Joda-Time](http://www.joda.org/joda-time/)
* [Jsoup](http://jsoup.org/)
* [LeakCanary](https://github.com/square/leakcanary)
* [ErrorView](https://github.com/xiprox/ErrorView)
* [MaterialCab](https://github.com/afollestad/material-cab)
* [AboutLibraries](https://github.com/mikepenz/AboutLibraries)

[![Play Store badge](https://play.google.com/intl/en_us/badges/images/generic/de-play-badge.png)](https://play.google.com/store/apps/details?id=rhedox.gesahuvertretungsplan)