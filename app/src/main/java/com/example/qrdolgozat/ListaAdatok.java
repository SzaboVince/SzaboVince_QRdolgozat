package com.example.qrdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaAdatok extends AppCompatActivity {

    private LinearLayout linear;
    private EditText nevecske;
    private EditText jegyecske;
    private Button modosit;
    private Button megse;
    private ListView listaview;
    private String url;
    private List<Person> people = new ArrayList<>();
    private EditText idecske;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_adatok);
        init();
        RequestTask task = new RequestTask(url, "GET");
        task.execute();

        megse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaAdatok.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        modosit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emberModositas();
            }
        });

    }

    public void init() {
        linear = findViewById(R.id.linearistemacska);
        nevecske = findViewById(R.id.etnev);
        jegyecske = findViewById(R.id.etjegy);
        modosit = findViewById(R.id.modositgomb);
        megse = findViewById(R.id.megsegomb);
        listaview = findViewById(R.id.listViewData);
        listaview.setAdapter(new PersonAdapter());
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        url = sharedPreferences.getString("a", "");
        idecske=findViewById(R.id.etid);
    }

    private void emberModositas() {
        String name = nevecske.getText().toString();
        String gradetext = jegyecske.getText().toString();
        String idtext = idecske.getText().toString();
        boolean valid = validacio();
        if (valid) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
        } else {
            int grade = Integer.parseInt(gradetext);
            int id = Integer.parseInt(idtext);
            Person person = new Person(id, name, grade);
            Gson jsonConverter = new Gson();
            RequestTask task = new RequestTask(url + "/" + id, "PUT", jsonConverter.toJson(person));
            task.execute();
        }
    }

    private boolean validacio() {
        if (nevecske.getText().toString().isEmpty() || jegyecske.getText().toString().isEmpty())
            return true;
        else return false;
    }

    private class PersonAdapter extends ArrayAdapter<Person> {
        public PersonAdapter() {
            super(ListaAdatok.this, R.layout.person_list_adapter, people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_adapter, null, false);

            Person actualPerson = people.get(position);
            TextView textViewName = view.findViewById(R.id.tvnev);
            TextView textViewAge = view.findViewById(R.id.tvjegy);
            TextView textViewModify = view.findViewById(R.id.tvmodosit);

            textViewName.setText(actualPerson.getName());
            textViewAge.setText(String.valueOf(actualPerson.getGrade()));

            textViewModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linear.setVisibility(View.VISIBLE);
                    nevecske.setText(actualPerson.getName());
                    jegyecske.setText(String.valueOf(actualPerson.getGrade()));
                    modosit.setVisibility(View.VISIBLE);
                    megse.setVisibility(View.VISIBLE);
                }
            });
            return view;
        }
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(ListaAdatok.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(ListaAdatok.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getResponseMessage());
            }
            switch (requestType) {
                case "GET":
                    Person[] peopleArray = converter.fromJson(response.getResponseMessage(), Person[].class);
                    people.clear();
                    people.addAll(Arrays.asList(peopleArray));
                    break;
            }
        }
    }
}