package com.qprovep.exlog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.qprovep.exlog.data.ExportImportUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        exportDataToUri(uri);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> importLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        confirmAndImport(uri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, 0, 0);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_more) {
                showMoreDialog();
                return false;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.sessionFragment) {
                bottomNav.getMenu().findItem(R.id.sessionStartFragment).setChecked(true);
            }
        });
    }

    private void showMoreDialog() {
        String[] options = { getString(R.string.export_data), getString(R.string.import_data) };
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.nav_more)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        launchExport();
                    } else if (which == 1) {
                        launchImport();
                    }
                })
                .show();
    }

    private void launchExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "exlog_backup.json");
        exportLauncher.launch(intent);
    }

    private void launchImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        importLauncher.launch(intent);
    }

    private void exportDataToUri(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String json = ExportImportUtil.exportToJson(this);
                OutputStream os = getContentResolver().openOutputStream(uri);
                if (os != null) {
                    os.write(json.getBytes());
                    os.close();
                }
                runOnUiThread(() -> Toast.makeText(this, R.string.export_success, Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.export_failed) + ": " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }

    private void confirmAndImport(Uri uri) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.import_confirm_title)
                .setMessage(R.string.import_confirm_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.import_data, (dialog, which) -> importDataFromUri(uri))
                .show();
    }

    private void importDataFromUri(Uri uri) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    ExportImportUtil.importFromJson(this, sb.toString());
                    runOnUiThread(() -> {
                        Toast.makeText(this, R.string.import_success, Toast.LENGTH_SHORT).show();
                        recreate();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.import_failed) + ": " + e.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }
}