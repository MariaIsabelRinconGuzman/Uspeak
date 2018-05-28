package co.edu.uco.uspeak;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import co.edu.uco.uspeak.Class.User;

public class QualificationActivity extends ItemsActivity {
    final User user = new User();
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferenceChat;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewInterest;
    private TextView textViewPoints;
    private ImageView fotoPerfil;
    private String keyFirebaseDate = "";
    private String keyChat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualification);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_qualification, contentFrameLayout);
        ImageButton imageButtonCalificate = (ImageButton) findViewById(R.id.imageButtonCalificate);


        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("citas");
        databaseReferenceUsers = database.getReference("usuario");
        databaseReferenceChat = database.getReference("chat");

        final RatingBar ratingBarPuntuality = (RatingBar) findViewById(R.id.ratingBarPuntuality);
        final RatingBar ratingBarFluency = (RatingBar) findViewById(R.id.ratingBarFluency);
        final RatingBar ratingBarAttitude = (RatingBar) findViewById(R.id.ratingBarAttitude);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewInterest = (TextView) findViewById(R.id.textViewInterest);
        textViewPoints = (TextView) findViewById(R.id.textViewPoints);
        fotoPerfil = (ImageView) findViewById(R.id.fotoPerfil);

        user.setEmail(getIntent().getExtras().getString("emailF"));
        user.setName(getIntent().getExtras().getString("nameF"));
        user.setProfilePicture(getIntent().getExtras().getString("profilePictureF"));
        user.setUid(getIntent().getExtras().getString("uidF"));
        user.setPoints(null != getIntent().getExtras().getString("pointsF") ? Integer.parseInt(getIntent().getExtras().getString("pointsF")) :
                0);
        user.setInterest(getIntent().getExtras().getString("interestF"));
        user.setToken(getIntent().getExtras().getString("tokenF"));
        user.setKeyFirebase(getIntent().getExtras().getString("keyFirebaseF"));

        keyFirebaseDate = getIntent().getExtras().getString("keyFirebaseDate");
        keyChat = getIntent().getExtras().getString("keyChat");

        textViewName.setText(user.getName());
        textViewEmail.setText(user.getEmail());
        textViewInterest.setText(user.getInterest());
        textViewPoints.setText(String.valueOf(user.getPoints()) + " points");
        Glide.with(this).load(user.getProfilePicture()).into(fotoPerfil);

        imageButtonCalificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float appointmentPunctuation = ratingBarPuntuality.getRating() + ratingBarFluency.getRating() + ratingBarAttitude.getRating();
                updatePoints(appointmentPunctuation, user.getEmail());
                textViewPoints.setText(String.valueOf(user.getPoints()) + " points");
                updateAudioCount();
                Toast.makeText(view.getContext(), "Punctuation for " + user.getName() + " is " + appointmentPunctuation , Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateAudioCount() {
        if(!keyChat.equals("false")) {
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(keyChat + "/" + "audios_counter", 0);
            databaseReferenceChat.updateChildren(childUpdates);
        }
    }

    private void updatePoints(float appointmentPunctuation, String email){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(keyFirebaseDate + "/" + "qualification", appointmentPunctuation);
        childUpdates.put(keyFirebaseDate + "/" + "status", "Q");
        databaseReference.updateChildren(childUpdates);

        Map<String, Object> childUpdatesUser = new HashMap<>();
        childUpdatesUser.put(user.getKeyFirebase() + "/" + "points", user.getPoints() + appointmentPunctuation);
        user.setPoints(Integer.parseInt(String.valueOf(user.getPoints() + Math.round(appointmentPunctuation))));
        databaseReferenceUsers.updateChildren(childUpdatesUser);
    }
}
