package co.edu.uco.uspeak.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import co.edu.uco.uspeak.Class.Appointment;
import co.edu.uco.uspeak.Class.HolderAppointments;
import co.edu.uco.uspeak.R;

/**
 * Created by Isabel on 04/12/2017.
 */

public class AdapterAppointments extends RecyclerView.Adapter<HolderAppointments>{
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

    private List<Appointment> listAppointments = new ArrayList<>();
    private Context c;

    public AdapterAppointments(Context c) {
        this.c = c;
    }

    public void addAppointment(Appointment m){
        listAppointments.add(m);
        notifyItemInserted(listAppointments.size());
    }

    @Override
    public HolderAppointments onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_appointments,parent,false);
        return new HolderAppointments(v);
    }

    @Override
    public void onBindViewHolder(HolderAppointments holder, final int position) {
        if(listAppointments.get(position).getName_creator().equals(mAuth.getDisplayName())){
            holder.getNombreUsuario().setText(listAppointments.get(position).getName_receptor());
        }else if(listAppointments.get(position).getName_receptor().equals(mAuth.getDisplayName())){
            holder.getNombreUsuario().setText(listAppointments.get(position).getName_creator());
        }
        holder.getDate().setText(listAppointments.get(position).getDate() + " " + listAppointments.get(position).getTime());
        holder.getStatus().setText(getStatus(listAppointments.get(position).getStatus()));
        if(listAppointments.get(position).getStatus().equals("Pending")){
            holder.getQualification().setVisibility(View.INVISIBLE);
        }else{
            holder.getQualification().setText("Qualification: " + String.valueOf(listAppointments.get(position).getQualification())+" points");
        }
    }

    @Override
    public int getItemCount() {
        return listAppointments.size();
    }

    public String getStatus(String statusA){
        String status = "" ;
        if(statusA.equals("P")){
            status = "Pending";
        }else if(statusA.equals("Q")){
            status = "Qualified";
        }
        return status;
    }
}
