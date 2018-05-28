package co.edu.uco.uspeak;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import co.edu.uco.uspeak.Class.MensajeRecibir;

public class StatisticsActivity extends ItemsActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ImageButton datepickerdialogbutton;
    private ImageButton timepickerdialogbutton;
    private ImageButton btnCreateMeeting;
    private Button buttonRefresh;
    private TextView textViewResultQuery;
    private TextView textView9;
    private TextView textView10;
    private static String date1;
    private static String date2;
    static HashMap<String, String> datos = new HashMap();
    static final Calendar calendar = Calendar.getInstance();
    Date fecha1;
    Date fecha2;
    int contador = 0;
    static boolean bdate1 = false;
    static boolean bdate2 = false;
    static boolean bconsulta = false;
    int contadormensajesconsultados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("mensaje");


        datepickerdialogbutton = (ImageButton) findViewById(R.id.btnDatePicker1);
        timepickerdialogbutton = (ImageButton) findViewById(R.id.btnDatePicker2);
        btnCreateMeeting = (ImageButton) findViewById(R.id.btnSearch);
        buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
        textViewResultQuery = (TextView) findViewById(R.id.textViewResultQuery);
        textView9 = (TextView) findViewById(R.id.textView9);
        textView10 = (TextView) findViewById(R.id.textView10);


        datepickerdialogbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bdate1 = true;
                DialogFragment dialogfragment = new StatisticsActivity.DatePickerDialogClass();
                dialogfragment.show(getFragmentManager(), "Date Picker 1 Dialog");
            }
        });

        timepickerdialogbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bdate2 = true;
                DialogFragment dialogfragment = new StatisticsActivity.DatePickerDialogClass();
                dialogfragment.show(getFragmentManager(), "Date Picker 2 Dialog");
            }
        });

        btnCreateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    contador = 0;
                    generateQuery(datos);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contador = 0;
                bconsulta = false;
                bdate1 = false;
                bdate2 = false;
                textView9.setText("");
                textView10.setText("");
                textViewResultQuery.setText("");
                datos.remove("dato1");
                datos.remove("dato2");
            }
        });
    }

    private void consultar(){
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                bconsulta = true;
                MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                contadormensajesconsultados ++;
                if(null != fecha1 && null != fecha2 &&
                        (m.getUser_receptor().equals("KwotoTI4LbhyviEt8yM6nVfTfn53") ||
                                m.getUser_creator().equals("KwotoTI4LbhyviEt8yM6nVfTfn53"))){
                    Long codigoHora = m.getHora();
                    Date d = new Date(codigoHora);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String fechaMensaje = sdf.format(d);
                    try{
                        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaM;
                        fechaM = formatoDelTexto.parse(fechaMensaje);
                        if(fecha1.before(fechaM) && fecha2.after(fechaM)){
                            contador ++;
                        }
                    }catch (ParseException ex){
                        ex.printStackTrace();
                    }
                }
                textViewResultQuery.setText(String.valueOf(contador));
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

    private void generateQuery(HashMap<String, String> data) throws ParseException {

        if(null != data.get("date1") && null != data.get("date2")) {
            SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
            String strFecha1 = data.get("date1");
            String strFecha2 = data.get("date2");
            fecha1 = formatoDelTexto.parse(strFecha1);
            fecha2 = formatoDelTexto.parse(strFecha2);
            if (fecha1.before(fecha2)) {//La fecha de la cita es futura
                textViewResultQuery.setText("Wait please!!");
                consultar();
              /*  while(!bconsulta){
                    textViewResultQuery.setText("Wait please!!");
                }*/
              //  textViewResultQuery.setText(String.valueOf(contador));
            }else{
                textViewResultQuery.setText("Select a valid range date");
            }
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
            TextView textView9 = (TextView) getActivity().findViewById(R.id.textView9);
            TextView textView10 = (TextView) getActivity().findViewById(R.id.textView10);
            if(bdate1 && !bdate2){
                date1 = day + "/" + (month+1) + "/" + year;
                datos.put("date1", date1);
                textView9.setText(date1);
            }else if(bdate2){
                date2 = day + "/" + (month+1) + "/" + year;
                datos.put("date2", date2);
                textView10.setText(date2);
            }
        }
    }
}
