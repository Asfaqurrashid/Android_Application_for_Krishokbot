package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText question;
    Button btn;
    EditText textView;
    Button reset;
    String url = "https://krishokbot.herokuapp.com/chatbot";
    ImageView iv_mic;
    TextToSpeech tts;
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = (EditText) findViewById(R.id.question);
        btn = (Button) findViewById(R.id.btn);
        textView = (EditText) findViewById(R.id.textView);
        reset = (Button) findViewById(R.id.reset);
        iv_mic = (ImageView) findViewById(R.id.iv_mic);
       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Map<String,String> params = new HashMap<String,String>();
               params.put("question", question.getText().toString());
               JSONObject jsonBody = new JSONObject(params);
               JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,jsonBody ,new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       try {
                           data = response.getString("response");
                           textView.setText(data);

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {

                       NetworkResponse networkResponse = error.networkResponse;
                       if (networkResponse != null) {
                           Log.e("Volley", "Error. HTTP Status Code:" + networkResponse.statusCode);
                       }

                       if (error instanceof TimeoutError) {
                           Log.e("Volley", "TimeoutError");
                       } else if (error instanceof NoConnectionError) {
                           Log.e("Volley", "NoConnectionError");
                       } else if (error instanceof AuthFailureError) {
                           Log.e("Volley", "AuthFailureError");
                       } else if (error instanceof ServerError) {
                           Log.e("Volley", "ServerError");
                       } else if (error instanceof NetworkError) {
                           Log.e("Volley", "NetworkError");
                       } else if (error instanceof ParseError) {
                           Log.e("Volley", "ParseError");
                       }
                       Log.d("Maps:", " Error: " + error.getMessage());

                   }
               });


               RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
               queue.add(jsonRequest);
           }


       });

       reset.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               question.setText("");
               textView.setText("");
               question.setSelection(0);
           }
       });

       iv_mic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                   @Override
                   public void onInit(int i) {
                       if(i == TextToSpeech.SUCCESS){
                           tts.setLanguage(Locale.ENGLISH);
                           tts.setSpeechRate(1.0f);
                           tts.speak(data,TextToSpeech.QUEUE_ADD,null);
                       }
                   }
               });
           }
       });


    }

}