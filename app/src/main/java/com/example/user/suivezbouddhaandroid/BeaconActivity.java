package com.example.user.suivezbouddhaandroid;

import android.app.Activity;
import com.estimote.sdk.SystemRequirementsChecker;

/**
 * Created by Arnaud on 13/01/17.
 */

public class BeaconActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

}
