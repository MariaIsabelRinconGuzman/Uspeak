package co.edu.uco.uspeak;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import co.edu.uco.uspeak.Class.HolderUsuario;
import co.edu.uco.uspeak.Class.User;

/**
 * Created by Isabel on 04/12/2017.
 */

public class AdapterUsuarios extends RecyclerView.Adapter<HolderUsuario>{

    private List<User> listUser = new ArrayList<>();
    private Context c;

    public AdapterUsuarios(Context c) {
        this.c = c;
    }

    public void addUsuario(User m){
        listUser.add(m);
        notifyItemInserted(listUser.size());
    }
    View v;

    @Override
    public HolderUsuario onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_users,parent,false);
        return new HolderUsuario(v);
    }

    @Override
    public void onBindViewHolder(HolderUsuario holder, final int position) {
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
                User user = new User();
                user.setName(listUser.get(position).getName());
                user.setEmail(listUser.get(position).getEmail());
                user.setUid(listUser.get(position).getUid());
                user.setProfilePicture(listUser.get(position).getProfilePicture());
                user.setInterest(listUser.get(position).getInterest());
                intent.putExtra("emailF",user.getEmail());
                intent.putExtra("nameF",user.getName());
                intent.putExtra("profilePictureF",user.getProfilePicture());
                intent.putExtra("uidF",user.getUid());
                intent.putExtra("interestF",user.getInterest());
                intent.putExtra("pointsF",user.getPoints());
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
        }
        return user;
    }
}
