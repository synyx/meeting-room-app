package de.synyx.android.meetingroom.screen.login;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AppCompatActivity;

import de.synyx.android.meetingroom.R;
import de.synyx.android.meetingroom.config.Config;
import de.synyx.android.meetingroom.config.Registry;
import de.synyx.android.meetingroom.domain.CalendarMode;
import de.synyx.android.meetingroom.screen.main.MainActivity;


public class LoginActivity extends AppCompatActivity implements LoginListener {

    private static final int REQUEST_LOBBY = 0;
    private static final String FRAGMENT_TAG = "login-fragment";
    private Config config;

    /**
     * Get an intent to create a new instance of this activity.
     *
     * @param  context  the package context
     *
     * @return  intent to call this activity
     */
    public static Intent getIntent(@NonNull Context context) {

        return new Intent(context, LoginActivity.class);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        config = Config.getInstance(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (fragment == null) {
            fragment = new LoginFragment();
            fragmentManager.beginTransaction().replace(R.id.relativeLayout1, fragment).commit();
        }

        LoginPresenterFactory presenterFactory = Registry.get(LoginPresenterFactory.class);
        presenterFactory.createPresenter((LoginContract.LoginView) fragment, this, config.getPreferencesService());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        finish();
    }


    @Override
    public void onErrorCloseButtonClick() {

        finish();
    }


    @Override
    public void onCalenderModeClick(CalendarMode calendarMode) {

        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, REQUEST_LOBBY);
    }
}
