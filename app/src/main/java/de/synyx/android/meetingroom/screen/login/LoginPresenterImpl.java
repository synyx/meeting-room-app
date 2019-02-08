package de.synyx.android.meetingroom.screen.login;

import de.synyx.android.meetingroom.business.account.AccountService;
import de.synyx.android.meetingroom.business.calendar.CalendarModeService;
import de.synyx.android.meetingroom.config.Registry;
import de.synyx.android.meetingroom.domain.CalendarMode;
import de.synyx.android.meetingroom.preferences.PreferencesService;
import de.synyx.android.meetingroom.screen.login.LoginContract.LoginPresenter;
import de.synyx.android.meetingroom.screen.login.LoginContract.LoginView;
import de.synyx.android.meetingroom.util.proxy.PermissionManager;

import java.util.List;


/**
 * @author  Julian Heetel - heetel@synyx.de
 */
public class LoginPresenterImpl implements LoginPresenter {

    private final LoginListener listener;
    private final PreferencesService preferencesService;
    private final PermissionManager permissionManager;
    private LoginView view;
    private CalendarMode prefCalendarMode;
    private CalendarModeService calendarModeService;

    public LoginPresenterImpl(LoginView view, LoginListener listener, PreferencesService preferencesService) {

        this.view = view;
        this.view.setPresenter(this);
        this.listener = listener;
        this.preferencesService = preferencesService;
        this.calendarModeService = Registry.get(CalendarModeService.class);
        this.prefCalendarMode = calendarModeService.getPrefCalenderMode();
        this.permissionManager = Registry.get(PermissionManager.class);
    }

    @Override
    public void askForPermissionsAgain() {

        askForPermissions();
    }


    @Override
    public void onAllPermissionsGranted() {

        if (hasFatalError()) {
            view.showErrorDialog();
        } else if (!preferencesService.isLoggedIn()) {
            checkAccounts();
        } else {
            calenderModeSelection();
        }
    }


    @Override
    public void onAccountSelected(String account) {

        saveSelectedAccount(account);
        calenderModeSelection();
    }


    @Override
    public void onCalendarModeSelected(String calendarMode) {

        preferencesService.saveCalendarMode(calendarMode);

        listener.onCalenderModeClick(prefCalendarMode);
    }


    @Override
    public void onErrorDialogCloseButtonClicked() {

        listener.onErrorCloseButtonClick();
    }


    @Override
    public void start() {

        view.showProgress();

        askForPermissions();
    }


    @Override
    public void stop() {

        // not needed
    }


    private void askForPermissions() {

        if (!view.isAdded()) {
            return;
        }

        List<String> neededPermissions = permissionManager.getNeededPermissions();

        if (neededPermissions.isEmpty()) {
            onAllPermissionsGranted();
        } else {
            view.askForPermissions(neededPermissions);
        }
    }


    private boolean hasFatalError() {

        return false;
    }


    private void checkAccounts() {

        String[] accountNames = Registry.get(AccountService.class).getAccountNames();

        if (accountNames.length == 1) {
            saveSelectedAccount(accountNames[0]);
        } else {
            view.showAccountSelection(accountNames);
        }
    }


    private void saveSelectedAccount(String account) {

        preferencesService.saveLoginAccountAndType(account, account, account.substring(account.indexOf("@") + 1));
    }


    private void calenderModeSelection() {

        if (prefCalendarMode == CalendarMode.NO_SELECTED_MODE) {
            view.showCalendarModeSelection(calendarModeService.getCalendarModes());
        } else {
            listener.onCalenderModeClick(prefCalendarMode);
        }
    }
}
