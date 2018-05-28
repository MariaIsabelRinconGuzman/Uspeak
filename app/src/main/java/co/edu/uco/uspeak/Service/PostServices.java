package co.edu.uco.uspeak.Service;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Isabel on 8/03/2018.
 */

public class PostServices {
//    private static String YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE = "AAAAM13Y0ms:APA91bFBOBzMOSGpIVkT2-SBbsRs4-tqZVeVsEi8x7Rcz9N7dDL7XC5z2htdVXwItn9PfZVA3ymWmBTgcuMRjRsxw7QT9up9CfNO4L2wwWO_7Pn8lqWPXZj6Jd4xQvt9jmzYvijZXtyj";
    private static String YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE = "AIzaSyCf_gl00NEafFnvLVvtxeBxaHq4502Js-8";

    public static void sendMessage(final Context c, final View view, final HashMap dataValue, final String instanceIdToken) {
        try{
            final AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "key=" + YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject mensaje = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("to", instanceIdToken);
            mensaje.put("typeService", "Message");
            mensaje.put("nameAudio", dataValue.get("nameAudio"));
            mensaje.put("mensaje",dataValue.get("mensaje"));
            mensaje.put("fotoPerfil",dataValue.get("fotoPerfil"));
            mensaje.put("nombre", dataValue.get("nombre"));
            mensaje.put("userC",dataValue.get("userC"));
            mensaje.put("userR",dataValue.get("userR"));
            mensaje.put("type",dataValue.get("type"));
            mensaje.put("hora",dataValue.get("hora"));

            data.put("data", mensaje);
            StringEntity entity = new StringEntity(data.toString());
            client.post(c,url,entity,"application/json",new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == 200){

                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(view.getContext(), "It is not possible to send your message. Try it later", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(view.getContext(), "It is not possible to send your message. Try it later", Toast.LENGTH_SHORT).show();
            System.out.println("Exception "+e);
        }
    }

    public static void createNotificationMessage(final Context c, final View view, final HashMap dataValue, final String instanceIdToken) {
        try{
            final AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "key=" + YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject notification = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("to", instanceIdToken);

            if(dataValue.get("type").equals("1")){
                notification.put("title", dataValue.get("nombre").toString());
                notification.put("body", dataValue.get("mensaje"));
            }else{
                notification.put("title", dataValue.get("nombre").toString());
                notification.put("body", "You received an audio message");
            }
            data.put("notification", notification);
            StringEntity entity = new StringEntity(data.toString());
            client.post(c,url,entity,"application/json",new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == 200){
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(view.getContext(), "It is not possible to send your message. Try it later", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(view.getContext(), "It is not possible to send your message. Try it later", Toast.LENGTH_SHORT).show();
            System.out.println("Exception " + e);
        }
    }

    public void createAlarm(final Context c, final View view, final HashMap dataValue, final String instanceIdToken, String userF){
        try{
        final AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "key="+YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE);
        String url = "https://fcm.googleapis.com/fcm/send";

        JSONObject notification = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("to", instanceIdToken);

        notification.put("title", "You have a meeting with " + userF);
        notification.put("body", dataValue.get("date").toString() + " " + dataValue.get("time").toString());

        data.put("data", notification);
        StringEntity entity = new StringEntity(data.toString());
        client.post(c,url,entity,"application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    Toast.makeText(view.getContext(), "Meeting created", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(view.getContext(), "It is not possible to create your appointment. Try it later", Toast.LENGTH_SHORT).show();
            }
        });
        }catch (Exception e){
            Toast.makeText(view.getContext(), "It is not possible to create your appointment. Try it later", Toast.LENGTH_SHORT).show();
            System.out.println("Exception " + e);
        }
    }
}
