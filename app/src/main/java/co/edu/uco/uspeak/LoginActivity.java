package co.edu.uco.uspeak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.uco.uspeak.Class.User;
import co.edu.uco.uspeak.Class.Utils;

public class LoginActivity extends AppCompatActivity {
    private SignInButton googleBtn;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "LOGIN_ACTIVITY";
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String idToken;
    private HashMap<String, String> keysFirebase = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            ((TextView) googleBtn.getChildAt(0)).setText(R.string.googleBtn);
        } catch (ClassCastException | NullPointerException e) {
            e.printStackTrace();
        }
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("usuario");
        final User user = new User();
        final List<User> users = new ArrayList<User>();
        googleBtn = (SignInButton) findViewById(R.id.googleBtn);
         View view = findViewById(android.R.id.content);

        try {
            if(Utils.isNetworkAvailable(getApplicationContext())){
                if(null != getIntent().getExtras() && getIntent().getExtras().getString("logout") == "true"){
                    mAuth.signOut();
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
                LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));

                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User userData = dataSnapshot.getValue(User.class);
                        users.add(userData);
                        keysFirebase.put(userData.getEmail(), dataSnapshot.getKey());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mAuth = FirebaseAuth.getInstance();

                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            user.setEmail(firebaseAuth.getCurrentUser().getEmail());
                            user.setName(firebaseAuth.getCurrentUser().getDisplayName());
                            user.setProfilePicture(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                            user.setUid(firebaseAuth.getCurrentUser().getUid());
                            if(null != idToken){
                                user.setToken(idToken);
                            }
                            if (users.size() > 0 && consultarSiNoEsta(users, user)) {
                                databaseReference.push().setValue(user);
                            }else{
                                consultarToken(users, user);
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("name", user.getName());
                            intent.putExtra("profilePicture", user.getProfilePicture());
                            intent.putExtra("uid", user.getUid());
                            if (user.getToken() != null) {
                                intent.putExtra("token", user.getToken());
                            }
                            startActivity(intent);
                        } else {
                            if(null != getIntent().getExtras() && getIntent().getExtras().getString("logout") == "true"){
                                mAuth.signOut();
                                FirebaseAuth.getInstance().signOut();
                                finish();
                            }
                        }
                    }
                };
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_LONG).show();

                        }
                    }).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            googleBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    signIn();
                }
            });

        } else {
                Toast.makeText(view.getContext(), "Check your internet connection. Try it later", Toast.LENGTH_SHORT).show();
                finish();
        }
        } catch (Exception e) {
            System.out.println("Exception " + e);
            finish();
        }
    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra("token");
            if(token != null){ idToken = token;}
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            // ...
                        }
                    }
                });
    }

    private boolean consultarSiNoEsta(List<User> users, User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(user.getEmail())) {
                return false;
            }
        }
        return true;
    }

    private void consultarToken(List<User> users, User user){
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(user.getEmail())) {
                String tokenDb = users.get(i).getToken();
                String tokenNew = user.getToken();
                actualizarToken(tokenDb, tokenNew, user.getEmail());
            }
        }
    }

    private void actualizarToken(String tokenDb, String tokenNew, String email) {
        if(null != tokenDb && null != tokenNew && !tokenDb.equals(tokenNew) && !keysFirebase.isEmpty()){//se actualizo
            String keyfirebase = keysFirebase.get(email);

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(keyfirebase + "/" + "token", tokenNew);
            databaseReference.updateChildren(childUpdates);
        }
    }
}
