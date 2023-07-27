package com.example.geniethevirtualassistant;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
public class contacts2 extends AppCompatActivity {
ListView l1;
String phoneNo;
TextToSpeech textToSpeech;
Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts2);
        l1 = findViewById(R.id.listview);
        textToSpeech = new TextToSpeech(this, status -> {});
        if (ContextCompat.checkSelfPermission(contacts2.this,
                Manifest.permission.READ_CONTACTS)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(contacts2.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            con();
        }
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phoneNo = cursor.getString(num);
                whatsapp(phoneNo);
                //Toast.makeText(contacts2.this, phoneNo, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void speak(String audio) {
        textToSpeech.speak(audio, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    private boolean isAppInstalled(String s) {
        PackageManager packageManager = getPackageManager();
        boolean is_installed;
        try {
            packageManager.getPackageInfo(s, PackageManager.GET_ACTIVITIES);
            is_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            is_installed = false;
            e.printStackTrace();
        }
        return is_installed;
    }
    public void whatsapp(String num) {
        String text2 = "";
        boolean installed3 = isAppInstalled("com.whatsapp");
        if (installed3) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + num + "&text=" + text2));
            startActivity(intent);
            speak("You can send message as per your choice");
        } else {
            speak("please install the WhatsApp app on your phone");
        }
    }



    public void con() {
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);
        String[] from = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
        l1.setAdapter(simpleCursorAdapter);
        l1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                con();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}