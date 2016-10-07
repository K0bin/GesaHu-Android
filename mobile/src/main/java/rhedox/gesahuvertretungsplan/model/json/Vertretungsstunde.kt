package rhedox.gesahuvertretungsplan.model.json

import com.squareup.moshi.FromJson
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.util.Html

/**
 * Created by robin on 01.10.2016.
 */
data class Vertretungsstunde(val Stundeanfang: String, val Stundeende: String, val Fach: String?, val Klasse: String?, val Lehrer: String?, val Vertretungslehrer: String?, val Raum: String?, val Hinweis: String?, val relevant: String?)

class VertretungsstundeAdapter() {
    @FromJson fun fromJson(vertretungsstunde: Vertretungsstunde): Substitute {

        val fach = if(!vertretungsstunde.Fach.isNullOrBlank()) Html.decode(vertretungsstunde.Fach!!.trim()) else "";
        val klasse = if(!vertretungsstunde.Klasse.isNullOrBlank()) Html.decode(vertretungsstunde.Klasse!!.trim()) else "";
        val lehrer = if(!vertretungsstunde.Lehrer.isNullOrBlank()) Html.decode(vertretungsstunde.Lehrer!!.trim()) else "";
        val vertretungslehrer = if(!vertretungsstunde.Vertretungslehrer.isNullOrBlank()) Html.decode(vertretungsstunde.Vertretungslehrer!!.trim()) else "";
        val hinweis = if(!vertretungsstunde.Hinweis.isNullOrBlank()) Html.decode(vertretungsstunde.Hinweis!!.trim()) else "";
        val raum = if(!vertretungsstunde.Raum.isNullOrBlank()) Html.decode(vertretungsstunde.Raum!!.trim()) else "";
        val isRelevant = if (vertretungsstunde.relevant?.toLowerCase() == "true") true else false;

        //Bindestrich workaround
        val anfangStr = vertretungsstunde.Stundeanfang.replace("-","").trim();
        val anfang = anfangStr.toInt();
        val endeStr = vertretungsstunde.Stundeende.replace("-","").trim();
        val ende = endeStr.toInt();

        return Substitute(anfang, ende, fach, klasse, lehrer, vertretungslehrer, raum, hinweis, isRelevant);
    }
}