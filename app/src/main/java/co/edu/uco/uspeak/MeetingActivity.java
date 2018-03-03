package co.edu.uco.uspeak;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;

import java.util.Calendar;

import co.edu.uco.uspeak.Class.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingActivity extends AppCompatActivity {
    private String fotoPerfilCadena;
    private CircleImageView fotoPerfil;
    private TextView textViewMeetingUser;
    private ImageButton datepickerdialogbutton;
    private ImageButton timepickerdialogbutton;
    private TextView selecteddatetime;
    private static String time;
    private static String date;

    static final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_chat, contentFrameLayout);

        fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
        textViewMeetingUser = (TextView) findViewById(R.id.textViewMeetingUser);
        datepickerdialogbutton = (ImageButton)findViewById(R.id.btnDatePicker);
        timepickerdialogbutton = (ImageButton)findViewById(R.id.btnTimePicker);
        selecteddatetime = (TextView)findViewById(R.id.textViewSelectDateTime);

        final User user = new User();
        user.setEmail(getIntent().getExtras().getString("emailF"));
        user.setName(getIntent().getExtras().getString("nameF"));
        user.setProfilePicture(getIntent().getExtras().getString("profilePictureF"));
        user.setUid(getIntent().getExtras().getString("uidF"));
        user.setPoints(null != getIntent().getExtras().getString("pointsF") ? Integer.parseInt(getIntent().getExtras().getString("pointsF")) :
                0 );
        user.setInterest(getIntent().getExtras().getString("interestF"));
        fotoPerfilCadena = user.getProfilePicture();
        Glide.with(MeetingActivity.this).load(fotoPerfilCadena).into(fotoPerfil);
        textViewMeetingUser.setText(textViewMeetingUser.getText().toString()+user.getName());
        selecteddatetime.setText(null != date ? date : "" + null != time ? time : "");
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
            TextView textview = (TextView)getActivity().findViewById(R.id.textViewSelectDateTime);
            date = day + " " + (month+1) + " of " + year;
            textview.setText(date + null != time ? time : "");
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
            TextView textview = (TextView)getActivity().findViewById(R.id.textViewSelectDateTime);
            time = hourOfDay + ":" + minute;
            textview.setText(null != date ? date : "" + time);
        }
    }

}
