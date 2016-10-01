package rhedox.gesahuvertretungsplan.model.json

import com.squareup.moshi.FromJson
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 01.10.2016.
 */
data class Vertretungsstunde(val Stundeanfang: Int, val Stundeende: Int, val Fach: String?, val Klasse: String?, val Lehrer: String?, val Vertretungslehrer: String?, val Raum: String?, val Hinweis: String?)

class VertretungsstundeAdapter() {
    @FromJson fun fromJson(vertretungsstunde: Vertretungsstunde): Substitute {
        return Substitute(vertretungsstunde.Stundeanfang, vertretungsstunde.Stundeende, vertretungsstunde.Fach ?: "", vertretungsstunde.Klasse ?: "", vertretungsstunde.Lehrer ?: "", vertretungsstunde.Vertretungslehrer ?: "", vertretungsstunde.Raum ?: "", vertretungsstunde.Hinweis ?: "");
    }
}