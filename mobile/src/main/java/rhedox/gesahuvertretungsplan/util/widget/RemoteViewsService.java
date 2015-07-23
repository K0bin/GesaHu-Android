package rhedox.gesahuvertretungsplan.util.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Robin on 22.07.2015.
 */
public class RemoteViewsService extends android.widget.RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
    }
}
