package co.edu.uco.uspeak;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import co.edu.uco.uspeak.Class.User;

public class ItemsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
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

                   /* Intent intent = new Intent(new Intent(ItemsActivity.this, LoginActivity.class));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                Intent intent_logout = new Intent(getApplicationContext(), LoginActivity.class);
                intent_logout.putExtra("logout", "true");
                startActivity(intent_logout);
                mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                return true;
            case R.id.action_statistics:
                Intent intent_statistics = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent_statistics);
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
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
