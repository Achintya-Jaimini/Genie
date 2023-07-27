package com.example.geniethevirtualassistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView editText, date, cal;
    int count = 0;
    Button button;
    ImageButton imageButton;
    Intent speechRecogniserIntent;
    SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cal = findViewById(R.id.textView);
        Calendar c = Calendar.getInstance();
        String d = DateFormat.getTimeInstance(android.icu.text.DateFormat.FULL).format(c.getTime());
        cal.setText(d);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        imageButton = findViewById(R.id.imageButton);
        textToSpeech = new TextToSpeech(this, status -> {});
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            int requestCode = 100;
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.RECORD_AUDIO}, requestCode);
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecogniserIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        imageButton.setOnClickListener(view -> {
            if (count == 0) {
                imageButton.setImageResource(R.drawable.ic_baseline_mic_24);
                speechRecognizer.startListening(speechRecogniserIntent);
                count = 1;
            } else {
                imageButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
                speechRecognizer.stopListening();
                count = 0;
            }
        });
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {}
            @Override
            public void onBeginningOfSpeech() {}
            @Override
            public void onRmsChanged(float v) {}
            @Override
            public void onBufferReceived(byte[] bytes) {}
            @Override
            public void onEndOfSpeech() {}
            @Override
            public void onError(int i) {}
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String string;
                editText.setText("");
                if (matches != null) {
                    string = matches.get(0).toLowerCase();
                    editText.setText(string);
                    if(string.contains("call")) {
                        newScreen(contacts.class);
                        speak("Please select any contact to make a call");
                    } else if(string.contains("mail")) {
                        speak("please enter the email I D, Subject and message.");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"));
                        startActivity(intent);
                    } else if(string.contains("message")) {
                        speak("Please type recipients name and your message");
                        sms("");
                    }
                }
            }
            @Override
            public void onPartialResults(Bundle bundle) {
            }
            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
        button.setOnClickListener(view -> speak
                ("Hello Achintya. I am Genie. please press the mike button to speak, turn it off and be precise."));
    }
    public void newScreen(Class c) {
        Intent intent1 = new Intent(MainActivity.this, c);
        startActivity(intent1);
    }
    public void sms(String num) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", num, null));
        intent.putExtra("sms_body", "");
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void speak(String audio) {
        textToSpeech.speak(audio, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void openapp(String w) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(w);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            speak("Please Install this app!");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}