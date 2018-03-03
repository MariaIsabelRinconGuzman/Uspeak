package co.edu.uco.uspeak;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import co.edu.uco.uspeak.Class.User;

public class InitialActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    User user = new User();
                    user.setEmail(firebaseAuth.getCurrentUser().getEmail());
                    user.setName(firebaseAuth.getCurrentUser().getDisplayName());
                    user.setProfilePicture(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                    user.setUid(firebaseAuth.getCurrentUser().getUid());

                   /* Intent intent = new Intent(new Intent(InitialActivity.this, LoginActivity.class));
                    intent.putExtra("email",user.getEmail());
                    intent.putExtra("name",user.getName());
                    intent.putExtra("profilePicture",user.getProfilePicture());
                    intent.putExtra("uid",user.getUid());
                    startActivity(intent);*/
                }else{
                   // finish();
                    System.exit(0);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

   //  @Override
  /*  public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.initial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
               // FirebaseAuth.getInstance().signOut();
             /*   Intent intent_logout = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent_logout);*/

                Intent intentLogout = new Intent(Intent.ACTION_MAIN);
                finish();

                return true;
            case R.id.action_profiles:
                mAuth.getCurrentUser();
                if (mAuth.getCurrentUser() != null) {
                    User user = new User();
                    user.setEmail(mAuth.getCurrentUser().getEmail());
                    user.setName(mAuth.getCurrentUser().getDisplayName());
                    user.setProfilePicture(mAuth.getCurrentUser().getPhotoUrl().toString());
                    user.setUid(mAuth.getCurrentUser().getUid());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("name", user.getName());
                    intent.putExtra("profilePicture", user.getProfilePicture());
                    intent.putExtra("uid", user.getUid());

                    Intent intent_profile = new Intent(intent);
                    startActivity(intent_profile);
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
       // return false;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            startActivity(new Intent(InitialActivity.this, AccountActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(InitialActivity.this, ChatActivity.class));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
