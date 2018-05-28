package co.edu.uco.uspeak.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.uco.uspeak.ChatActivity;
import co.edu.uco.uspeak.Class.Chat;
import co.edu.uco.uspeak.Class.HolderUsers;
import co.edu.uco.uspeak.Class.User;
import co.edu.uco.uspeak.R;


/**
 * Created by Isabel on 04/12/2017.
 */

public class AdapterUsers extends RecyclerView.Adapter<HolderUsers>{

    private List<User> listUser = new ArrayList<>();
    private Context c;
    private int countAudios;
    private Chat chat;
    private FirebaseDatabase databaseChat = FirebaseDatabase.getInstance();
    private DatabaseReference  databaseReferenceChat = databaseChat.getReference("chat");
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private Map<String, Chat> mapCount = new HashMap<>();

    public AdapterUsers(Context c) {
        this.c = c;
    }

    public void addUser(User m, Chat chatR){
        countAudios = null != chat ? chat.getAudios_counter() : -1;
        if(null != chatR){
            mapCount.put(m.getUid(), chatR);
        }
        listUser.add(m);
        notifyItemInserted(listUser.size());
    }

    @Override
    public HolderUsers onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_users,parent,false);
        return new HolderUsers(v);
    }

    @Override
    public void onBindViewHolder(HolderUsers holder, final int position) {
        holder.getNombre().setText(listUser.get(position).getName());
        holder.getEmail().setText(listUser.get(position).getEmail());
        holder.getIntereses().setText(listUser.get(position).getInterest());
        holder.getPoints().setText(String.valueOf(listUser.get(position).getPoints())+" points");
        if(listUser.get(position).getProfilePicture().isEmpty()){
            holder.getFotoMensajePerfil().setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(c).load(listUser.get(position).getProfilePicture()).into(holder.getFotoMensajePerfil());
        }
        holder.getConect().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ChatActivity.class);
                if(mapCount.containsKey(listUser.get(position).getUid())){
                    String keyFirebase = null != mapCount.get(listUser.get(position).getUid()).getKeyFirebase() ? mapCount.get(listUser.get(position).getUid()).getKeyFirebase() : "";
                    intent.putExtra("keyChat",keyFirebase);
                    intent.putExtra("countAudio",String.valueOf(mapCount.get(listUser.get(position).getUid()).getAudios_counter()));
                }else{
                    databaseReferenceChat.push().setValue(new Chat("", mAuth.getUid(), listUser.get(position).getUid(), 0));
                    intent.putExtra("keyChat","false");
                    intent.putExtra("countAudio",String.valueOf(0));
                }
                User user = new User();
                user.setName(listUser.get(position).getName());
                user.setEmail(listUser.get(position).getEmail());
                user.setUid(listUser.get(position).getUid());
                user.setProfilePicture(listUser.get(position).getProfilePicture());
                user.setInterest(listUser.get(position).getInterest());
                user.setPoints(listUser.get(position).getPoints());
                user.setToken(listUser.get(position).getToken());
                user.setKeyFirebase(listUser.get(position).getKeyFirebase());
                intent.putExtra("emailF",user.getEmail());
                intent.putExtra("nameF",user.getName());
                intent.putExtra("profilePictureF",user.getProfilePicture());
                intent.putExtra("uidF",user.getUid());
                intent.putExtra("interestF",user.getInterest());
                intent.putExtra("pointsF",String.valueOf(user.getPoints()));
                intent.putExtra("tokenF", user.getToken());
                intent.putExtra("keyFirebaseF", user.getKeyFirebase());
                c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public User getUserForUid(String uid, User userComplete){
        User user = new User();
        if(uid.equals(userComplete.getUid())){
            user.setName(userComplete.getName());
            user.setEmail(userComplete.getEmail());
            user.setInterest(userComplete.getInterest());
            user.setPoints(userComplete.getPoints());
            user.setProfilePicture(userComplete.getProfilePicture());
            user.setUid(userComplete.getUid());
            user.setToken(userComplete.getToken());
        }
        return user;
    }
}
