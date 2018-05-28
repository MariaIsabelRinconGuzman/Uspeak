package co.edu.uco.uspeak;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.edu.uco.uspeak.Adapter.AdapterAppointments;
import co.edu.uco.uspeak.Class.Appointment;
import co.edu.uco.uspeak.Class.User;

public class PendingActivity extends ItemsActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private RecyclerView rvAppointments;
    private AdapterAppointments adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_qualification, contentFrameLayout);
        rvAppointments = (RecyclerView) findViewById(R.id.rvAppointments);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("citas");

        adapter = new AdapterAppointments(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvAppointments.setLayoutManager(l);
        rvAppointments.setAdapter(adapter);

        final User user = new User();
        user.setEmail(getIntent().getExtras().getString("emailF"));
        user.setName(getIntent().getExtras().getString("nameF"));
        user.setProfilePicture(getIntent().getExtras().getString("profilePictureF"));
        user.setUid(getIntent().getExtras().getString("uidF"));
        user.setPoints(null != getIntent().getExtras().getString("pointsF") ? Integer.parseInt(getIntent().getExtras().getString("pointsF")) :
                0);
        user.setInterest(getIntent().getExtras().getString("interestF"));
        user.setToken(getIntent().getExtras().getString("tokenF"));
        user.setKeyFirebase(getIntent().getExtras().getString("keyFirebaseF"));

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Appointment appointment = dataSnapshot.getValue(Appointment.class);
                if(null != appointment && (appointment.getUser_creator().equals(user.getUid()) || appointment.getUser_receptor().equals(user.getUid()))){
                    adapter.addAppointment(appointment);
                }
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
    }

    private void setScrollbar(){
        rvAppointments.scrollToPosition(adapter.getItemCount()-1);
    }
}
