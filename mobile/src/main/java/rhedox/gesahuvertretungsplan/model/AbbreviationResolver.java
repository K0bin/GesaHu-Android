package rhedox.gesahuvertretungsplan.model;

import android.content.Context;

public class AbbreviationResolver {
    private Context context;
    private String packageName;

    public AbbreviationResolver(Context context) {
        if(context == null)
            throw new IllegalArgumentException("Context must not be null.");

        this.context = context.getApplicationContext();
        packageName = context.getPackageName();
    }

    public String resolveTeacher(String shortName) {
        String stringsShortName = shortName.toLowerCase().replace(' ','_').replace('-','_').replace("ß","ss").replace("ä","ae").replace("ö","oe").replace("ü","ue").trim();

        int id = context.getResources().getIdentifier("teachers_"+stringsShortName, "string", packageName);

        if(id == 0)
            return shortName;
        else
            return context.getString(id);
    }

    public String resolveSubject(String shortName) {
        String stringsShortName = shortName.toLowerCase().replace('-','_').replace("ß", "ss").replace("ä", "ae").replace("ö", "oe").replace("ü","ue").trim();

        int id = context.getResources().getIdentifier("subjects_" + stringsShortName, "string", packageName);
        if(id == 0)
            return shortName;
        else
            return context.getString(id);
    }
}

