package co.edu.uco.uspeak;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import co.edu.uco.uspeak.Class.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends InitialActivity {

    private CircleImageView fotoPerfil;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private ImageButton btnRecord;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewInterest;
    private TextView textViewPoints;
    private ImageButton btnAdd;

    private MediaRecorder mRecorder;

    private String mFileName = null;

    private final String LOG_TAG = "Record Log";

    private StorageReference mStorage;
    private ProgressDialog mProgress;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private String fotoPerfilCadena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_chat, contentFrameLayout);

        mStorage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);
        fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewInterest = (TextView) findViewById(R.id.textViewInterest);
        textViewPoints = (TextView) findViewById(R.id.textViewPoints);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnRecord = (ImageButton) findViewById(R.id.btnRecord);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);

        final User user = new User();
        user.setEmail(getIntent().getExtras().getString("emailF"));
        user.setName(getIntent().getExtras().getString("nameF"));
        user.setProfilePicture(getIntent().getExtras().getString("profilePictureF"));
        user.setUid(getIntent().getExtras().getString("uidF"));
        user.setPoints(null != getIntent().getExtras().getString("pointsF") ? Integer.parseInt(getIntent().getExtras().getString("pointsF")) :
         0 );
        user.setInterest(getIntent().getExtras().getString("interestF"));
        fotoPerfilCadena = user.getProfilePicture();
        Glide.with(ChatActivity.this).load(fotoPerfilCadena).into(fotoPerfil);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/"+user.getUid()+".3gp";

        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    startRecording();
                    txtMensaje.setText("Recording Start");
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                    txtMensaje.setText("Recording Stop");
                }
                return false;
            }
        });

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("mensaje");
        storage = FirebaseStorage.getInstance();

        adapter = new AdapterMensajes(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adapter);

        textViewName.setText(user.getName());
        textViewEmail.setText(user.getEmail());
        textViewInterest.setText(user.getInterest());
        textViewPoints.setText(String.valueOf(user.getPoints())+" points");

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtMensaje.getText().length() > 0){
                    databaseReference.push().setValue(new MensajeEnviar(txtMensaje.getText().toString(),user.getName().toString(),fotoPerfilCadena,"1", ServerValue.TIMESTAMP));
                    txtMensaje.setText("");
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MeetingActivity.class);
                intent.putExtra("emailF",user.getEmail());
                intent.putExtra("nameF",user.getName());
                intent.putExtra("profilePictureF",user.getProfilePicture());
                intent.putExtra("uidF",user.getUid());
                intent.putExtra("interestF",user.getInterest());
                intent.putExtra("pointsF",user.getPoints());
                startActivity(intent);
            }
        });

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
                MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                adapter.addMensaje(m);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void uploadAudio(){
        mProgress.setMessage("Uploading audio");
        mProgress.show();
        StorageReference filepath = mStorage.child("Audio").child(mFileName);

        Uri uri = Uri.fromFile(new File(mFileName));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                txtMensaje.setText("");
                MensajeEnviar m = new MensajeEnviar("",fotoPerfilCadena,textViewName.getText().toString(),fotoPerfilCadena,"2",ServerValue.TIMESTAMP, mFileName);
                databaseReference.push().setValue(m);
            }
        });
    }

    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }
}
