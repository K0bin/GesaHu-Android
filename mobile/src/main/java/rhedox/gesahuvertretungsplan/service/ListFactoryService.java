package rhedox.gesahuvertretungsplan.service;

import android.content.Intent;

import rhedox.gesahuvertretungsplan.ui.adapters.ListRemoteViewsFactory;

/**
 * Created by Robin on 22.07.2015.
 */
public class ListFactoryService extends android.widget.RemoteViewsService {

    public static final String EXTRA_DATE = "extra_date";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(getApplicationContext(), intent);
    }
}
