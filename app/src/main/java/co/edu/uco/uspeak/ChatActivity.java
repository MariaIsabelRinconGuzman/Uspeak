package co.edu.uco.uspeak;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.edu.uco.uspeak.Adapter.AdapterMessages;
import co.edu.uco.uspeak.Class.Chat;
import co.edu.uco.uspeak.Class.MensajeEnviar;
import co.edu.uco.uspeak.Class.MensajeRecibir;
import co.edu.uco.uspeak.Class.User;
import co.edu.uco.uspeak.Class.Utils;
import co.edu.uco.uspeak.Service.PostServices;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends ItemsActivity{

    private CircleImageView fotoPerfil;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMessages adapter;
    private ImageButton btnRecord;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewInterest;
    private TextView textViewPoints;
    private ImageButton btnAdd;
    private MediaRecorder mRecorder;
    private final String LOG_TAG = "Record Log";
    private BroadcastReceiver broadcastReceiver;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    public static final String MENSAJE = "MENSAJE";
    private FirebaseDatabase databaseChat;
    private DatabaseReference databaseReferenceChat;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String fotoPerfilCadena;
    private FirebaseUser mAuth;
    private File mFileName;
    private int contadorAudios = 0;
    private String keyChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_chat, contentFrameLayout);

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver,
                new IntentFilter(MENSAJE));

        if (Utils.isNetworkAvailable(getApplicationContext())) {
            mAuth = FirebaseAuth.getInstance().getCurrentUser();
            mStorage = FirebaseStorage.getInstance().getReference();
            final String tokenUser = FirebaseInstanceId.getInstance().getToken();

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
            user.setPoints(null != getIntent().getExtras().getString("pointsF") ? Integer.parseInt(getIntent().getExtras().getString("pointsF")) : 0);
            user.setInterest(getIntent().getExtras().getString("interestF"));
            user.setToken(getIntent().getExtras().getString("tokenF"));
            user.setKeyFirebase(getIntent().getExtras().getString("keyFirebaseF"));
            keyChat =  getIntent().getExtras().getString("keyChat");
            contadorAudios =  Integer.parseInt(getIntent().getExtras().getString("countAudio"));
            fotoPerfilCadena = user.getProfilePicture();
            Glide.with(ChatActivity.this).load(fotoPerfilCadena).into(fotoPerfil);

            final String uid1 = mAuth.getUid();
            final String uid2 = user.getUid();

            final String token1 = tokenUser;
            final String token2 = user.getToken();

            btnRecord.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        startRecording();
                        txtMensaje.setText("Recording Start");
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        txtMensaje.setText("Recording Stop");
                        stopRecording(uid1, uid2, token1, token2, view);
                    }
                    return false;
                }
            });

            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("mensaje");
            storage = FirebaseStorage.getInstance();

            databaseChat = FirebaseDatabase.getInstance();
            databaseReferenceChat = databaseChat.getReference("chat");

            adapter = new AdapterMessages(this);
            LinearLayoutManager l = new LinearLayoutManager(this);
            l.setStackFromEnd(true);
            rvMensajes.setLayoutManager(l);
            rvMensajes.setAdapter(adapter);

            textViewName.setText(user.getName());
            textViewEmail.setText(user.getEmail());
            textViewInterest.setText(user.getInterest());
            textViewPoints.setText(String.valueOf(user.getPoints()) + " points");

            btnEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (txtMensaje.getText().length() > 0) {
                        MensajeEnviar mensajeHora = new MensajeEnviar("", txtMensaje.getText().toString(), mAuth.getDisplayName().toString(), mAuth.getPhotoUrl().toString(), "1", ServerValue.TIMESTAMP, uid1, uid2);
                        HashMap<String, String> datos = new HashMap();
                        datos.put("nameAudio", "");
                        datos.put("mensaje", txtMensaje.getText().toString());
                        datos.put("nombre", mAuth.getDisplayName().toString());
                        datos.put("fotoPerfil", mAuth.getPhotoUrl().toString());
                        datos.put("type", "1");
                        datos.put("hora", "1521719764479");
                        datos.put("userC", uid1);
                        datos.put("userR", uid2);
                        //PostServices.sendMessage(getApplicationContext(), view, datos, token2);
                        PostServices.createNotificationMessage(getApplicationContext(), view, datos, token2);
                        databaseReference.push().setValue(new MensajeEnviar("", txtMensaje.getText().toString(), mAuth.getDisplayName().toString(), mAuth.getPhotoUrl().toString(), "1", ServerValue.TIMESTAMP, uid1, uid2));
                        txtMensaje.setText("");
                    }
                }
            });

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChatActivity.this, MeetingActivity.class);
                    intent.putExtra("emailF", user.getEmail());
                    intent.putExtra("nameF", user.getName());
                    intent.putExtra("profilePictureF", user.getProfilePicture());
                    intent.putExtra("uidF", user.getUid());
                    intent.putExtra("interestF", user.getInterest());
                    intent.putExtra("pointsF", String.valueOf(user.getPoints()));
                    intent.putExtra("tokenF", user.getToken());
                    intent.putExtra("keyFirebaseF", user.getKeyFirebase());
                    intent.putExtra("keyChat", keyChat);
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
                    if (m.getUser_creator().equals(user.getUid()) && m.getUser_receptor().equals(mAuth.getUid()) || m.getUser_creator().equals(mAuth.getUid()) && m.getUser_receptor().equals(user.getUid())) {
                        adapter.addMensaje(m);
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

            databaseReferenceChat.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if((chat.getUser_creator().equals(user.getUid()) && chat.getUser_receptor().equals(mAuth.getUid())) || (chat.getUser_creator().equals(mAuth.getUid()) && chat.getUser_receptor().equals(user.getUid()))){
                        keyChat = dataSnapshot.getKey();
                        contadorAudios = chat.getAudios_counter();
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
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            File path = new File(Environment.getExternalStorageDirectory()
                    .getPath());
            mFileName = File.createTempFile("temporal", ".mp4", path);
            mRecorder.setOutputFile(mFileName.getAbsolutePath());
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    private void stopRecording(String uid1, String uid2, String token1, String token2, View view) {
        mRecorder.stop();
        mRecorder.release();
        if(contadorAudios < 20){
            uploadAudio(uid1, uid2, token1, token2, view);
        }else {
            Toast.makeText(view.getContext(), "You can not send more audios. It's time to have a meeting", Toast.LENGTH_SHORT).show();
            txtMensaje.setText("");
        }
    }

    private void uploadAudio(final String uid1, final String uid2, final String token1, final String token2, final View view) {
        mProgress.setMessage("Uploading audio");
        mProgress.show();
        StorageReference filepath = mStorage.child("Audio").child(mFileName.getName());

        Uri uri = Uri.fromFile(new File(mFileName.getAbsolutePath()));

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                txtMensaje.setText("");
                String contenidoFicheroStream = convertFile(mFileName);
                if(null != contenidoFicheroStream && !contenidoFicheroStream.isEmpty()){
                    HashMap<String, String> datos = new HashMap();
                    datos.put("nameAudio", mFileName.getName());
                    datos.put("mensaje", contenidoFicheroStream);
                    datos.put("nombre", mAuth.getDisplayName().toString());
                    datos.put("fotoPerfil", mAuth.getPhotoUrl().toString());
                    datos.put("hora", ServerValue.TIMESTAMP.toString());
                    datos.put("userC", uid1);
                    datos.put("userR", uid2);
                    datos.put("type", "2");
                    //PostServices.sendMessage(getApplicationContext(), view, datos, token2);
                    PostServices.createNotificationMessage(getApplicationContext(), view, datos, token2);
                    MensajeEnviar m = new MensajeEnviar(mFileName.getName(), contenidoFicheroStream, mAuth.getDisplayName().toString(), mAuth.getPhotoUrl().toString(), "2", ServerValue.TIMESTAMP, uid1, uid2);
                    if(!keyChat.equals("false")) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(keyChat + "/" + "audios_counter", (contadorAudios++));
                        databaseReferenceChat.updateChildren(childUpdates);
                    }
                    databaseReference.push().setValue(m);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String convertFile(File mFileName) {
        String contenidoR = "";
        java.io.File fichero = new java.io.File(mFileName.getAbsolutePath());
        FileInputStream ficheroStream = null;
        byte contenido[] = new byte[(int)fichero.length()];
        try {
            ficheroStream = new FileInputStream(fichero);
            ficheroStream.read(contenido);
            contenidoR = new String(Base64.encodeToString(contenido, Base64.DEFAULT));
            ficheroStream.close();
            return contenidoR;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenidoR;
    }

    private void setScrollbar(){
        rvMensajes.scrollToPosition(adapter.getItemCount()-1);
    }

    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter(MENSAJE));
    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");
            if(type.equals("1")){
                String mensajeR = intent.getStringExtra("mensaje");
                String nombre = intent.getStringExtra("nombre");
                String fotoPerfil = intent.getStringExtra("fotoPerfil");
                Long hora = 1521719764479L;
                String userC = intent.getStringExtra("userC");
                String userR = intent.getStringExtra("userR");
                if(mensajeR != null){
                    MensajeRecibir mensaje = new MensajeRecibir("" ,mensajeR,nombre,fotoPerfil,type, hora, userC, userR);
                    adapter.addMensaje(mensaje);
                }
            }
            else{
                String nameAudio = intent.getStringExtra("nameAudio");
                String mensajeR = intent.getStringExtra("mensaje");
                String nombre = intent.getStringExtra("nombre");
                String fotoPerfil = intent.getStringExtra("fotoPerfil");
                Long hora = Long.parseLong(intent.getStringExtra("hora"));
                String userC = intent.getStringExtra("userC");
                String userR = intent.getStringExtra("userR");
                if(mensajeR != null){
                    MensajeRecibir mensaje = new MensajeRecibir(nameAudio, mensajeR, nombre, fotoPerfil, type, hora, userC, userR);
                 //   adapter.addMensaje(mensaje);
                }
            }
        }
    };
}
