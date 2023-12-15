package com.example.qrdolgozat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    private Button scan;
    private Button lista;
    private TextView szoveg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        lista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, ListaAdatok.class);
                startActivity(intent);
                finish();
            }
        });
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setPrompt("QRCode");
                intentIntegrator.setCameraId(0);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Vissza léptél az alkalmazásban", Toast.LENGTH_SHORT).show();
            } else {
                szoveg.setText(result.getContents());
                Uri uri = Uri.parse(result.getContents());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                SharedPreferences sharedPref = getSharedPreferences("MyData",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("a",result.getContents());
                editor.commit();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void init(){
        scan=findViewById(R.id.scangomb);
        lista=findViewById(R.id.listazasgomb);
        szoveg=findViewById(R.id.szoveg);
    }
}