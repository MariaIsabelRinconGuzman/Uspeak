package co.edu.uco.uspeak;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.uco.uspeak.Adapter.AdapterUsers;
import co.edu.uco.uspeak.Class.Chat;
import co.edu.uco.uspeak.Class.User;
import co.edu.uco.uspeak.Class.Utils;

public class MainActivity extends ItemsActivity {
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewInterest;
    private TextView textViewPoints;
    private ImageView fotoPerfil;
    private RecyclerView rvUsers;
    private AdapterUsers adapter;
    private ImageButton btnAddInterest;

    private FirebaseDatabase database;
    private DatabaseReference databaseReferenceChat;
    private DatabaseReference databaseReference;

    private String keyfirebase;

    private Map<String, String> countAudio = new HashMap<String, String>();
    private List<Chat> listChat = new ArrayList<>();

    final Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() != null && Utils.isNetworkAvailable(getApplicationContext())) {
            FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
            getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);

            textViewName = (TextView) findViewById(R.id.textViewName);
            textViewEmail = (TextView) findViewById(R.id.textViewEmail);
            textViewInterest = (TextView) findViewById(R.id.textViewInterest);
            textViewPoints = (TextView) findViewById(R.id.textViewPoints);
            fotoPerfil = (ImageView) findViewById(R.id.fotoPerfil);
            rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
            btnAddInterest = (ImageButton) findViewById(R.id.btnAddInterest);

            database = FirebaseDatabase.getInstance();
            databaseReferenceChat = database.getReference("chat");
            databaseReference = database.getReference("usuario");

            adapter = new AdapterUsers(this);
            LinearLayoutManager l = new LinearLayoutManager(this);
            rvUsers.setLayoutManager(l);
            rvUsers.setAdapter(adapter);

            final User user = new User();
            user.setEmail(getIntent().getExtras().getString("email"));
            user.setName(getIntent().getExtras().getString("name"));
            user.setProfilePicture(getIntent().getExtras().getString("profilePicture"));
            user.setUid(getIntent().getExtras().getString("uid"));
            user.setToken(getIntent().getExtras().getString("token") != null ? getIntent().getExtras().getString("token") : "");

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    setScrollbar();
                }
            });

            databaseReferenceChat.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getUser_creator().equals(user.getUid())) {
                        chat.setKeyFirebase(dataSnapshot.getKey());
                        listChat.add(chat);
                        countAudio.put(chat.getUser_receptor(), chat.getKeyFirebase());
                    } else if (chat.getUser_receptor().equals(user.getUid())) {
                        chat.setKeyFirebase(dataSnapshot.getKey());
                        listChat.add(chat);
                        countAudio.put(chat.getUser_creator(), chat.getKeyFirebase());
                    } else {
                        countAudio.put("false", null);
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

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User userData = dataSnapshot.getValue(User.class);
                    if (null == adapter.getUserForUid(userData.getUid(), user).getUid()) {
                        userData.setKeyFirebase(dataSnapshot.getKey());
                        Chat chat = consultarSiNoEsta(listChat, countAudio.get(userData.getUid()));
                        if (null != chat) {
                            adapter.addUser(userData, chat);
                        } else {
                            adapter.addUser(userData, null);
                        }
                    } else {
                        keyfirebase = dataSnapshot.getKey();
                        User userReturn = adapter.getUserForUid(user.getUid(), userData);
                        textViewInterest.setText(null != userReturn.getInterest() ? userReturn.getInterest() : "Add your interest");
                        textViewPoints.setText(String.valueOf(userReturn.getPoints()) + " points");
                        user.setToken(userReturn.getToken());
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

            if (null != getIntent().getExtras()) {
                textViewName.setText(user.getName());
                textViewEmail.setText(user.getEmail());
                Glide.with(this).load(user.getProfilePicture()).into(fotoPerfil);
            }

            btnAddInterest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                    View mView = layoutInflaterAndroid.inflate(R.layout.user_interest_input, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                    alertDialogBuilderUserInput.setView(mView);

                    final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    textViewInterest.setText(userInputDialogEditText.getText().toString());
                                    User userUpdate = new User();
                                    userUpdate.setInterest(userInputDialogEditText.getText().toString());
                                    Map<String, Object> childUpdates = new HashMap<>();
                                    childUpdates.put(keyfirebase + "/" + "interest", userUpdate.getInterest());
                                    databaseReference.updateChildren(childUpdates);
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();
                }
            });
        } else {
            FirebaseAuth.getInstance().signOut();
            finish();
        }
    }

    private void setScrollbar() {
        rvUsers.scrollToPosition(adapter.getItemCount() - 1);
    }

    private Chat consultarSiNoEsta(List<Chat> listChat, String uid) {
        for (int i = 0; i < listChat.size(); i++) {
            if (listChat.get(i).getKeyFirebase().toString().equals(uid) || listChat.get(i).getUser_creator().equals(uid)) {
                return listChat.get(i);
            }
        }
        return null;
    }
}

