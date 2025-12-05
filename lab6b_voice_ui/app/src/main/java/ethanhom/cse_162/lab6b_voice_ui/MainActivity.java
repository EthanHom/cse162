package ethanhom.cse_162.lab6b_voice_ui;

import java.util.ArrayList;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import java.time.Instant;
import java.util.Date;
import java.util.List;

//text to speech imports
import android.speech.tts.TextToSpeech;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;
    private static final int REQUEST_PHONE_CALL = 1;
    protected static final int RESULT_SPEECH = 1;

    private ImageButton btnSpeak;
    private TextView txtText;


//    declaration
    private TextToSpeech textToSpeech;

    // initialization: request permissions, setup the initial calendar view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtText = (TextView) findViewById(R.id.txtText);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Oops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }

        calendarView = findViewById(R.id.calendarView);
        if (calendarView != null) {
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
                    String msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year;
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        displaySpeechRecognizer();

//        text to speech constructor from documentation
//        initializes text to speech
//        https://developer.android.com/reference/android/speech/tts/TextToSpeech
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
//                    set language from documentation
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });


    }

    private static final int SPEECH_REQUEST_CODE = 0;

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        // Exception Handling for phones without the Google App (com.google.android.googlequicksearchbox)
        try {
//            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
//            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);


            // Free-form voice-to-text conversion (the displaySpeechRecognizer function)
            //  - generate an intent to invoke the Android speech recognition module
            //  - RecognizerIntent
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // This starts the activity and populates the intent with the speech text.
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }
        catch (ActivityNotFoundException e)
        {
            String appPackageName = "com.google.android.googlequicksearchbox";
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse ("https://market.android.com/details?id=APP_PACKAGE_NAME"));
//            startActivity(browserIntent);
        }

    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {

                // Receive the texts converted from speech
                //  - This callback is invoked when the Speech Recognizer returns.
                //  - spokenText is what you have spoken.
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));
                    if (text.get(0).equals("today")) {
                        long unixTime = System.currentTimeMillis();
                        calendarView.setDate(unixTime);

                        // get date then change to string
                        Date date = new Date(unixTime);
                        String dateString = date.toString();

                        //  speak method from documentation
                        textToSpeech.speak(dateString, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (text.get(0).equals("tomorrow")) {
                        long unixTime = System.currentTimeMillis();
                        calendarView.setDate(unixTime + 86400000);

                        // get date then change to string
                        Date date = new Date(unixTime + 86400000);
                        String dateString = date.toString();

                        //  speak method from documentation
                        textToSpeech.speak(dateString, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (text.get(0).equals("day after tomorrow")) {
                        long unixTime = System.currentTimeMillis();
                        calendarView.setDate(unixTime + 172800000);

                        // get date then change to string
                        Date date = new Date(unixTime + 172800000);
                        String dateString = date.toString();

                        //  speak method from documentation
                        textToSpeech.speak(dateString, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (text.get(0).equals("call emergency")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < text.size(); i++) {
                            sb.append(text.get(i));
                        }
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + R.string.emergency_number));
                        startActivity(callIntent);
                        Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }

                    if (text.get(0).equals("take note")) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < text.size(); i++) {
                            sb.append(text.get(i));
                        }
                        Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }


/*
* we don't need anything else for onDestroy because framework handles calendar,
* imagebutton, and textview.
*
* The permissions requests: no cleanup needed
*
* Speech recognition Intent - no cleanup needed/one time use

 * */

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {

//            from documentation
            textToSpeech.stop();
            textToSpeech.shutdown();

        }
        super.onDestroy();
    }

}


//package ethanhom.cse_162.lab6b_voice_ui;
//
//public class MainActivity {
//}
//
//
