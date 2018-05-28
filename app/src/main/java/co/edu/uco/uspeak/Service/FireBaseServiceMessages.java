package co.edu.uco.uspeak.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;
import co.edu.uco.uspeak.ChatActivity;
import co.edu.uco.uspeak.Class.Mensaje;
import co.edu.uco.uspeak.Class.MensajeRecibir;

/**
 * Created by Isabel on 4/03/2018.
 */

public class FireBaseServiceMessages extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size() > 0 &&   remoteMessage.getData().get("typeService").equals("Message")){
            String mensajeR = remoteMessage.getData().get("mensaje");
            String nameAudio = remoteMessage.getData().get("nameAudio");
            String nombre = remoteMessage.getData().get("nombre");
            String fotoPerfil = remoteMessage.getData().get("fotoPerfil");
            String userC = remoteMessage.getData().get("userC");
            String userR = remoteMessage.getData().get("userR");
            String type =  remoteMessage.getData().get("type");
            long hora = Long.parseLong(remoteMessage.getData().get("hora").toString());
            Mensaje mensaje = new MensajeRecibir(nameAudio, mensajeR, nombre, fotoPerfil, type, hora, userC, userR);
          //  showNotification(nombre,mensajeR);
            Intent i = new Intent(ChatActivity.MENSAJE);
            i.putExtra("mensaje",mensaje.getMensaje());
            i.putExtra("nombre", mensaje.getNombre());
            i.putExtra("fotoPerfil", mensaje.getFotoPerfil());
            i.putExtra("type", mensaje.getType_mensaje());
            i.putExtra("hora", hora);
            i.putExtra("userC", mensaje.getUser_creator());
            i.putExtra("userR", mensaje.getUser_receptor());
            i.putExtra("nameAudio", mensaje.getName_audio());
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }else if(null != remoteMessage.getNotification()){
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        }
      }

    private void showNotification(String cabezera, String cuerpo){
        Intent i = new Intent(this,ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);

        Uri soundNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Builder builder = new Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle(cabezera);
        builder.setContentText(cuerpo);
        builder.setSound(soundNotification);
        builder.setTicker(cuerpo);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Random random = new Random();

        notificationManager.notify(random.nextInt(),builder.build());
    }
}