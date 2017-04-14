package rhedox.gesahuvertretungsplan.ui.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

/**
 * Created by robin on 07.02.2017.
 */

public interface DrawerActivity {
	boolean getIsPermanentDrawer();
	@Nullable ActionBar getSupportActionBar();
	void setSupportActionBar(@Nullable Toolbar toolbar);
	void syncDrawer();
}
