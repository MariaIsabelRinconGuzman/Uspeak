package co.edu.uco.uspeak;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class AccountActivity extends InitialActivity {
    private Button conectBtn;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_account, contentFrameLayout);

        conectBtn = (Button) findViewById(R.id.conectBtn);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewInterest = (TextView) findViewById(R.id.textViewInterest);


        textViewInterest.setText("Maria Isabel");


    }
}
