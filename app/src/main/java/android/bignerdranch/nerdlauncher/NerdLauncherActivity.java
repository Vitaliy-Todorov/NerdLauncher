package android.bignerdranch.nerdlauncher;

import androidx.fragment.app.Fragment;

public class NerdLauncherActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new NerdLauncherFragment();
    }

}
