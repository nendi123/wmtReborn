package nendi.wmtreborn;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import nendi.wmtreborn.panduan.PanduanActivity;
import nendi.wmtreborn.panduan.PrefManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPreferences sharedpreferences;
    boolean doubleBackToExitPressedOnce = false;

    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "username";
    private static final int CAMERA_REQUEST_CODE = 7777;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Halaman Utama");
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.CAMERA
                },CAMERA_REQUEST_CODE
        );

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        /**    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         if (drawer.isDrawerOpen(GravityCompat.START)) {
         drawer.closeDrawer(GravityCompat.START);
         } else {
         super.onBackPressed();
         }
         **/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        sharedpreferences = getSharedPreferences(Constant.my_shared_preferences, Context.MODE_PRIVATE);
        if (id == R.id.nav_profil) {
            Intent intent = new Intent(this, ProfilActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_meteran) {
            Intent intent = new Intent(this, PencarianIDActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_panduan) {
            PrefManager prefManager = new PrefManager(getApplicationContext());
            prefManager.setFirstTimeLaunch(true);
            Intent intent = new Intent(this, PanduanActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_tentang) {
            Intent intent = new Intent(this, TentangActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout){
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(Constant.session_status, false);
            editor.putString(TAG_ID, null);
            editor.putString(TAG_USERNAME, null);
            editor.commit();

            Intent intent = new Intent(MainActivity.this, LoginwmtActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
