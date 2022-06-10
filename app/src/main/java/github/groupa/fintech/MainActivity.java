package github.groupa.fintech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.opencsv.CSVWriter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

import github.groupa.fintech.add.AddFragment;
import github.groupa.fintech.drive.DriveServiceHelper;
import github.groupa.fintech.statistics.StatisticsModelClass;
import github.groups.fintech.R;

public class MainActivity extends AppCompatActivity {
    public DriveServiceHelper driveServiceHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MoneyManager);//change theme after splash screen
        setContentView(R.layout.activity_main);
        requestSignIn();
        //Configure add button from bottom app bar
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_add);
        fab.setOnClickListener(view -> {
            DialogFragment addDialog = new AddFragment();
            addDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
            addDialog.show(getSupportFragmentManager(), "dialog");
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_overview, R.id.navigation_statistics, R.id.navigation_scope,
                R.id.navigation_savings, R.id.floating_add)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }
    private void requestSignIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(client.getSignInIntent(), 400);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 400:
                if (resultCode == RESULT_OK) {
                    handleSignInIntent(data);
                }
        }
    }
    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data).addOnSuccessListener(this, account -> {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(account.getAccount());
            Drive googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("money_manager").build();
            driveServiceHelper = new DriveServiceHelper(googleDriveService);

        }).addOnFailureListener(this, e -> {
            Log.e("MainActivity", "Unable to sign in.", e);
        });
    }




}