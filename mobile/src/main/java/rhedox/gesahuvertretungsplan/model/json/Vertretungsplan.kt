package rhedox.gesahuvertretungsplan.model.json

import com.squareup.moshi.FromJson
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstitutesList

/**
 * Created by robin on 01.10.2016.
 */
data class Vertretungsplan(val Hinweise: String?, val Stunden: List<Vertretungsstunde>?) {

    class Adapter() {
        @FromJson fun fromJson(vertretungsplan: Vertretungsplan): SubstitutesList {
            val list = mutableListOf<Substitute>();
            if(vertretungsplan.Stunden != null) {
                for (item in vertretungsplan.Stunden) {
                    list.add(Vertretungsstunde.Adapter().fromJson(item));
                }
            }
            return SubstitutesList(vertretungsplan.Hinweise ?: "", list);
        }
    }
}