package co.edu.uco.uspeak.Class;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import co.edu.uco.uspeak.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 04/09/2017. 04
 */

public class HolderAppointments extends RecyclerView.ViewHolder {

    private TextView nombreUsuario;
    private TextView date;
    private TextView status;
    private TextView qualification;

    public HolderAppointments(View itemView) {
        super(itemView);
        nombreUsuario = (TextView) itemView.findViewById(R.id.nombreUsuario);
        date = (TextView) itemView.findViewById(R.id.date);
        status = (TextView) itemView.findViewById(R.id.status);
        qualification = (TextView) itemView.findViewById(R.id.qualification);
    }

    public TextView getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(TextView nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public TextView getDate() {
        return date;
    }

    public void setDate(TextView date) {
        this.date = date;
    }

    public TextView getStatus() {
        return status;
    }

    public void setStatus(TextView status) {
        this.status = status;
    }

    public TextView getQualification() {
        return qualification;
    }

    public void setQualification(TextView qualification) {
        this.qualification = qualification;
    }
}
