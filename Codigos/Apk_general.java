package com.example.monitoreouao;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityElectro extends AppCompatActivity {
    public String url;
    public EditText ip;
    public int unico;
    private Button bip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electro);
        bip=(Button)findViewById(R.id.botip);
        ip=(EditText)findViewById(R.id.textip);



        //url= "http://192.168.1."+ipdir+":47622/Trabajo-Final/web/ws";

        bip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                unico=2;
                String ipdir=ip.getText().toString();
                url="http://192.168."+ipdir+":47622/Trabajo-Final/web/ws"; //La ip varia dependiendo la ubicacion del servidor
                //System.out.println(url);                                 // En este caso es un pc nuestro servidor
                new Consultarele().execute(url);

            }

        });


    }
    private class Consultarele extends AsyncTask<String ,Void ,String> {//Se debe cambiar el nombre de la clase dependiendo el lugar a monitorear


        @Override
        protected String doInBackground(String... strings) {
            try {
                return consultarUrl(strings[0]);
            }catch (IOException e){
                return "URL invalida.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //System.out.println(result);

            TableLayout table;
            TableRow row;
            Context contexto = getApplicationContext();
            JSONArray ja = null;
            try {
                ja = new JSONArray(result);
                //System.out.println(ja.toString());
                table = (TableLayout) findViewById(R.id.tabla);
                for (int h=ja.length()-1;h>=0;h--){
                    row = (TableRow) LayoutInflater.from(contexto).inflate(R.layout.filatabla, null);
                    JSONObject json1 = new JSONObject();
                    json1 = ja.getJSONObject(h);
                    String name = json1.getString("nombre");
                    String ocupacion = json1.getString("ocupacion");
                    String hora = json1.getString("hora");
                    System.out.println(name);
                    if ("Laboratorio de electronica".equals(name)&&unico>0) { //En esta linea de codigo se modifica dependiendo el lugar a monitorear

                        TextView textf1;
                        TextView textf2;
                        textf1 = (TextView) row.findViewById(R.id.textf1);
                        textf2 = (TextView) row.findViewById(R.id.textf2);

                        textf1.setText(ocupacion);
                        textf2.setText(hora);

                        table.addView(row);
                        unico--;
                    }else{
                        System.out.println("no hay registro");
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private String consultarUrl(String myurl) throws IOException {

        System.out.println(myurl);

        InputStream is = null;
        int len = 118500;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();

            is = conn.getInputStream();
            String res = readIt(is, len);
            return res;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }



}
