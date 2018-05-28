package co.edu.uco.uspeak.Class;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import co.edu.uco.uspeak.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 04/09/2017. 04
 */

public class HolderUsers extends RecyclerView.ViewHolder {

    private TextView nombre;
    private TextView email;
    private TextView intereses;
    private CircleImageView fotoMensajePerfil;
    private ImageView fotoMensaje;
    private TextView points;
    private ImageButton conect;

    public HolderUsers(View itemView) {
        super(itemView);
        nombre = (TextView) itemView.findViewById(R.id.nombre);
        email = (TextView) itemView.findViewById(R.id.email);
        intereses = (TextView) itemView.findViewById(R.id.intereses);
        fotoMensajePerfil = (CircleImageView) itemView.findViewById(R.id.fotoPerfilMensaje);
        fotoMensaje = (ImageView) itemView.findViewById(R.id.mensajeFoto);
        points = (TextView) itemView.findViewById(R.id.points);
        conect = (ImageButton) itemView.findViewById(R.id.conect);
        conect.setVisibility(View.VISIBLE);
    }

    public TextView getNombre() {
        return nombre;
    }

    public void setNombre(TextView nombre) {
        this.nombre = nombre;
    }

    public CircleImageView getFotoMensajePerfil() {
        return fotoMensajePerfil;
    }

    public void setFotoMensajePerfil(CircleImageView fotoMensajePerfil) {
        this.fotoMensajePerfil = fotoMensajePerfil;
    }

    public TextView getEmail() {
        return email;
    }

    public void setEmail(TextView email) {
        this.email = email;
    }

    public TextView getIntereses() {
        return intereses;
    }

    public void setIntereses(TextView intereses) {
        this.intereses = intereses;
    }

    public ImageView getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(ImageView fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
    }

    public ImageButton getConect() {
        return conect;
    }

    public void setConect(ImageButton conect) {
        this.conect = conect;
    }

    public TextView getPoints() {
        return points;
    }

    public void setPoints(TextView points) {
        this.points = points;
    }
}
