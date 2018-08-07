package rhedox.gesahuvertretungsplan.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by robin on 07.02.2017.
 */

public interface DrawerActivity {
	boolean getIsPermanentDrawer();
	@Nullable ActionBar getSupportActionBar();
	void setSupportActionBar(@Nullable Toolbar toolbar);
	void syncDrawer();
}
