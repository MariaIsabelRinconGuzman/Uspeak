package co.edu.uco.uspeak.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.edu.uco.uspeak.Class.HolderMessage;
import co.edu.uco.uspeak.Class.MensajeRecibir;
import co.edu.uco.uspeak.R;

/**
 * Created by Isabel on 04/01/2018. 04
 */

public class AdapterMessages extends RecyclerView.Adapter<HolderMessage> implements MediaPlayer.OnCompletionListener{

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context c;
    private File mFileName;

    public AdapterMessages(Context c) {
        this.c = c;
    }

    public void addMensaje(MensajeRecibir m){
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }

    @Override
    public HolderMessage onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.card_view_mensajes,parent,false);
        return new HolderMessage(v);
    }

    @Override
    public void onBindViewHolder(HolderMessage holder, final int position) {
        holder.getNombre().setText(listMensaje.get(position).getNombre());
        Long codigoHora = listMensaje.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm");
        String fechaMensaje = sdf.format(d);
        if(listMensaje.get(position).getType_mensaje().equals("2")){
            holder.getHora().setText(fechaMensaje);
            String dataS = listMensaje.get(position).getMensaje();
            byte[] contenido = Base64.decode(dataS, Base64.DEFAULT);
            File path = new File(Environment.getExternalStorageDirectory().getPath());
            try {
                mFileName = new File(path, listMensaje.get(position).getName_audio());
                OutputStream outStream = new FileOutputStream(mFileName);
                outStream.write(contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
            final MediaPlayer mp = new MediaPlayer();
            mp.setOnCompletionListener(this);
            try {
                mp.setDataSource(mFileName.getAbsolutePath());
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.getMensaje().setVisibility(View.INVISIBLE);
            holder.getBtnPlay().setVisibility(View.VISIBLE);
            holder.getBtnPlay().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mp.start();
                }
            });
        }else if(listMensaje.get(position).getType_mensaje().equals("1")){
            holder.getHora().setText(fechaMensaje);
            holder.getMensaje().setVisibility(View.VISIBLE);
            holder.getBtnPlay().setVisibility(View.INVISIBLE);
            holder.getMensaje().setText(listMensaje.get(position).getMensaje());
        }
        if(listMensaje.get(position).getFotoPerfil().isEmpty()){
            holder.getFotoMensajePerfil().setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(c).load(listMensaje.get(position).getFotoPerfil()).into(holder.getFotoMensajePerfil());
        }
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
