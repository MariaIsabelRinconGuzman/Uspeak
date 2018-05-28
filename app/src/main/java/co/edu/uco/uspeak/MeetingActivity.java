package co.edu.uco.uspeak;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.edu.uco.uspeak.Class.Appointment;
import co.edu.uco.uspeak.Class.User;
import co.edu.uco.uspeak.Class.Utils;
import co.edu.uco.uspeak.Service.PostServices;
import de.hdodenhof.circleimageview.CircleImageView;


public class MeetingActivity extends ItemsActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String fotoPerfilCadena;
    private CircleImageView fotoPerfil;
    private TextView textViewMeetingUser;
    private ImageButton datepickerdialogbutton;
    private ImageButton timepickerdialogbutton;
    private ImageButton btnCreateMeeting;
    private static String time;
    private static String timeComplete;
    private static String date;
    private static String dateComplete;
    static HashMap<String, String> datos = new HashMap();
    private String tokenUser = FirebaseInstanceId.getInstance().getToken();
    private FirebaseUser mAuth;
    private String keyfirebase = "";
    private String keyChat = "";

    static final Calendar calendar = Calendar.getInstance();
    static PostServices messageService = new PostServices();
    final User user = new User();
    java.util.Date fecha = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        if(Utils.isNetworkAvailable(getApplicationContext()) ){
            mAuth = FirebaseAuth.getInstance().getCurrentUser();
            FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
            getLayoutInflater().inflate(R.layout.activity_chat, contentFrameLayout);

            fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
            textViewMeetingUser = (TextView) findViewById(R.id.textViewMeetingUser);
            datepickerdialogbutton = (ImageButton) findViewById(R.id.btnDatePicker);
            timepickerdialogbutton = (ImageButton) findViewById(R.id.btnTimePicker);
            btnCreateMeeting = (ImageButton) findViewById(R.id.btnCreateMeeting);

            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("citas");

            user.setEmail(getIntent().getExtras().getString("emailF"));
            user.setName(getIntent().getExtras().getString("nameF"));
            user.setProfilePicture(getIntent().getExtras().getString("profilePictureF"));
            user.setUid(getIntent().getExtras().getString("uidF"));
            user.setPoints(null != getIntent().getExtras().getString("pointsF") ? Integer.parseInt(getIntent().getExtras().getString("pointsF")) :
                    0);
            user.setInterest(getIntent().getExtras().getString("interestF"));
            user.setToken(getIntent().getExtras().getString("tokenF"));
            user.setKeyFirebase(getIntent().getExtras().getString("keyFirebaseF"));
            keyChat = getIntent().getExtras().getString("keyChat");
            fotoPerfilCadena = user.getProfilePicture();
            Glide.with(MeetingActivity.this).load(fotoPerfilCadena).into(fotoPerfil);
            datos.put("userR", user.getToken());
            textViewMeetingUser.setText(textViewMeetingUser.getText().toString() + user.getName());
            datepickerdialogbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialogfragment = new DatePickerDialogClass();
                    dialogfragment.show(getFragmentManager(), "Date Picker Dialog");
                }
            });

            timepickerdialogbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment dialogfragment = new TimePickerDialogClass();
                    dialogfragment.show(getFragmentManager(), "Time Picker Dialog");
                }
            });

            btnCreateMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        generateNotification(getApplicationContext(), datos, view);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Appointment a = dataSnapshot.getValue(Appointment.class);
                    try {
                    if(a.getStatus().equals("P")){
                        if((a.getUser_creator().equals(user.getUid()) && a.getUser_receptor().equals(mAuth.getUid())) || (a.getUser_creator().equals(mAuth.getUid()) && a.getUser_receptor().equals(user.getUid()))){
                            keyfirebase = dataSnapshot.getKey();
                                SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                                String strFecha = a.getDate() + " " + a.getTime();
                                Date fechaCita;
                                fechaCita = formatoDelTexto.parse(strFecha);
                                //if (fecha.compareTo(fechaCita) == -1) {//La fecha de la cita es futura
                                if (fecha.before(fechaCita)) {//La fecha de la cita es futura
                                    Intent intent = new Intent(MeetingActivity.this, PendingActivity.class);
                                    intent.putExtra("emailF", user.getEmail());
                                    intent.putExtra("nameF", user.getName());
                                    intent.putExtra("profilePictureF", user.getProfilePicture());
                                    intent.putExtra("uidF", user.getUid());
                                    intent.putExtra("interestF", user.getInterest());
                                    intent.putExtra("pointsF", String.valueOf(user.getPoints()));
                                    intent.putExtra("tokenF", user.getToken());
                                    intent.putExtra("keyFirebaseF", user.getKeyFirebase());
                                    startActivity(intent);
                                } else if (a.getQualification() == 0) {//La cita ya se puede calificar
                                    Intent intent = new Intent(MeetingActivity.this, QualificationActivity.class);
                                    intent.putExtra("emailF", user.getEmail());
                                    intent.putExtra("nameF", user.getName());
                                    intent.putExtra("profilePictureF", user.getProfilePicture());
                                    intent.putExtra("uidF", user.getUid());
                                    intent.putExtra("interestF", user.getInterest());
                                    intent.putExtra("pointsF", String.valueOf(user.getPoints()));
                                    intent.putExtra("tokenF", user.getToken());
                                    intent.putExtra("keyFirebaseF", user.getKeyFirebase());
                                    intent.putExtra("keyFirebaseDate", keyfirebase);
                                    intent.putExtra("keyChat", keyChat);
                                    startActivity(intent);
                                }
                            }
                        }
                    }catch (ParseException ex) {
                        ex.printStackTrace();
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

    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            TextView textview = (TextView)getActivity().findViewById(R.id.textViewSelectDate);
            date = day + ", " + (month+1) + " of " + year;
            dateComplete = day + "/" + (month+1) + "/" + year;
            textview.setText("Date: " + date);
            datos.put("date", date);
        }
    }

    public static class TimePickerDialogClass extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            int hourOfDay = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timepickerdialog = new TimePickerDialog(getActivity(),this,hourOfDay, minute, false);
            return timepickerdialog;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            TextView textview = (TextView)getActivity().findViewById(R.id.textViewSelectTime);
            time = hourOfDay + ":" + minute;
            textview.setText("Hour: " + time);
            datos.put("time", time);
        }
    }

    private void generateNotification(Context c, HashMap data, View view) throws ParseException {
        if(null != data.get("time") && null != data.get("date") && null != dateComplete){
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String strFecha = dateComplete + " " + data.get("time");
        Date fechaCita;
        fechaCita = formatoDelTexto.parse(strFecha);
        if (fecha.before(fechaCita)) {//La fecha de la cita es futura
            if(!keyfirebase.isEmpty()){//Ya tiene una cita Pendiente
                Toast.makeText(view.getContext(), "You can not create an appointment. You have a meeting pending", Toast.LENGTH_SHORT).show();
            }else{
                databaseReference.push().setValue(new Appointment(dateComplete, time, 0, mAuth.getUid(), user.getUid(), "P", mAuth.getDisplayName(), user.getName()));
                messageService.createAlarm(c, view, datos, tokenUser, user.getName());
                messageService.createAlarm(c, view, datos, data.get("userR").toString(), mAuth.getDisplayName());
            }
        }else{
            Toast.makeText(view.getContext(), "Select a correct date", Toast.LENGTH_SHORT).show();
        }
       }
    }
}
