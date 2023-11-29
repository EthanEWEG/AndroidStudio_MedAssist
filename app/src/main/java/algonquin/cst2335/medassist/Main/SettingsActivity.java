package algonquin.cst2335.medassist.Main;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import algonquin.cst2335.medassist.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch pushNotificationsSwitch, calendarNotificationsSwitch, themeSwitch;
    private Button deleteAccountBtn, privacyStatementBtn;
    private RadioGroup radiofontsize;
    private TextView fontSizeLabel;
    private ViewGroup rootView;

    private static final int SMALL_FONT_SIZE = R.dimen.small_font_size;
    private static final int MEDIUM_FONT_SIZE = R.dimen.medium_font_size;
    private static final int LARGE_FONT_SIZE = R.dimen.large_font_size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Dark Theme.
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean("ThemeDark", false);
        if (isDarkTheme && getDelegate().getLocalNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.Theme_MedAssistDarkTheme);
        } else if (!isDarkTheme && getDelegate().getLocalNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            setTheme(R.style.Theme_MedAssistLightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        initializeViews();
        setUpListeners();

        boolean isPushNotificationsEnabled = preferences.getBoolean("PushNotificationsEnabled", true);
        boolean isCalendarNotificationEnabled = preferences.getBoolean("CalendarNotificationsEnabled", true);

        pushNotificationsSwitch.setChecked(isPushNotificationsEnabled);
        calendarNotificationsSwitch.setChecked(isCalendarNotificationEnabled);
        themeSwitch.setChecked(isDarkTheme);

        int selectedFontSize = preferences.getInt("FontSize", R.dimen.medium_font_size);
        if (selectedFontSize == R.dimen.small_font_size) {
            radiofontsize.check(R.id.smallFontSizeRadioButton);
        } else if (selectedFontSize == R.dimen.medium_font_size) {
            radiofontsize.check(R.id.mediumFontSizeRadioButton);
        } else if (selectedFontSize == R.dimen.large_font_size) {
            radiofontsize.check(R.id.largeFontSizeRadioButton);
        }
    }

    private void initializeViews() {
        pushNotificationsSwitch = findViewById(R.id.pushNotificationsSwitch);
        calendarNotificationsSwitch = findViewById(R.id.calendarNotificationsSwitch);
        themeSwitch = findViewById(R.id.themeSwitch);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);
        privacyStatementBtn = findViewById(R.id.privacyStatementBtn);
        radiofontsize = findViewById(R.id.fontSizeRadioGroup);
        fontSizeLabel = findViewById(R.id.fontSizeLabel);
        rootView = findViewById(android.R.id.content);
    }

    private void setUpListeners() {
        // Example listener for Privacy Statement button
        privacyStatementBtn.setOnClickListener(v -> {
            // TODO: Implement what happens when privacy statement is clicked
        });

        // Listeners for switches
        pushNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Implement push notification enable/disable logic
            SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("PushNotificationsEnabled", isChecked);
            editor.apply();

            //the push notification is called.
            setPushNotificationsEnabled(isChecked);
        });

        calendarNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("CalendarNotificationsEnabled", isChecked);
            editor.apply();

            // Call method to enable/disable calendar notifications
            setCalendarNotificationsEnabled(isChecked);
        });

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
            boolean isCurrentlyDarkTheme = preferences.getBoolean("ThemeDark", false);

            if (isChecked != isCurrentlyDarkTheme) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("ThemeDark", isChecked);
                editor.apply();

                recreate(); // Recreate the activity only if the theme preference has changed
            }
        });

        // Listener for Delete Account button
        deleteAccountBtn.setOnClickListener(v -> {
            // TODO: Implement account deletion logic
        });

        // Listener for radiogroup
        radiofontsize.setOnCheckedChangeListener((group, checkedId) -> {
            int newSize;

            if (checkedId == R.id.smallFontSizeRadioButton) {
                newSize = R.dimen.small_font_size;
            } else if (checkedId == R.id.mediumFontSizeRadioButton) {
                newSize = R.dimen.medium_font_size;
            } else if (checkedId == R.id.largeFontSizeRadioButton) {
                newSize = R.dimen.large_font_size;
            } else {
                // Default to medium font size if none of the radio buttons are selected
                newSize = R.dimen.medium_font_size;
            }

            // Apply the selected font size to all TextView elements in the layout
            applyFontSizesToAllViews(findViewById(android.R.id.content), newSize);

            // Save the selected font size preference
            SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("FontSize", newSize);
            editor.apply();
        });
    }



    private void setCalendarNotificationsEnabled(boolean isEnabled) {
        if (isEnabled) {
            // Enable calendar notifications using alarm manager.
            // Schedule alarms or notifications related to the calendar events
        } else {
            // Disable calendar notifications
            // Cancel scheduled alarms or notifications
        }
    }

    private void setPushNotificationsEnabled(boolean isEnabled) {
        if (isEnabled) {
            // Enable notifications
            // If using FCM, inform your server to resume sending notifications to this device.
        } else {
            // Disable notifications
            // If using FCM, inform your server to stop sending notifications to this device.
        }
    }

    private void applyFontSizesToAllViews(View view, int newSize) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                applyFontSizesToAllViews(child, newSize);
            }
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(newSize));
        }
    }

}
