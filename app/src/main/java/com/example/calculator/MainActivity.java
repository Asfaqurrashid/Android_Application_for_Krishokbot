package com.example.calculator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    EditText question;
    Button button;
    TextView textView;
    String url = "https://krishokbot.herokuapp.com/chatbot";
    ImageView ic_mic;
    TextToSpeech tts;
    String data = "আপনি কেমন আছেন";
    String audio = "";
    private  static final int REQ_CODE_SPEECH_INPUT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = (EditText) findViewById(R.id.question);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        ic_mic = (ImageView) findViewById(R.id.ic_mic);

        ic_mic.setTag("ic_mic_off");
        ic_mic.setImageResource(R.drawable.ic_mic_off);

        ic_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tts != null){
                    tts.stop();
                }
                String bg = String.valueOf(ic_mic.getTag());
                if(bg.equals("ic_mic_off")) {
                    ic_mic.setImageResource(R.drawable.ic_mic);
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"bn-BD");
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "কথা বলতে চাপুন");
                    try {
                        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                        ic_mic.setImageResource(R.drawable.ic_mic_off);
                    }
                    catch (Exception e) {
                        Toast.makeText(MainActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    ic_mic.setImageResource(R.drawable.ic_mic_off);
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String decide = "";
               if(!TextUtils.isEmpty(question.getText().toString()) && !audio.equals("")){
                   decide = audio;
               }
               else if(!TextUtils.isEmpty(question.getText().toString()) && audio.equals("")){
                   decide = question.getText().toString();
               }
               else {
                   decide = audio;
               }

               Map<String,String> params = new HashMap<String,String>();
               params.put("question", decide);
               JSONObject jsonBody = new JSONObject(params);
               JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,jsonBody ,new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       try {
                           data = response.getString("response");
                           textView.setText(data);
                           textView.setMovementMethod(new ScrollingMovementMethod());
                           tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                               @Override
                               public void onInit(int i) {
                                   if(i == TextToSpeech.SUCCESS){
                                       ic_mic.setImageResource(R.drawable.ic_mic);
                                       tts.setLanguage(Locale.ENGLISH);
                                       tts.setSpeechRate(1.0f);
                                       tts.speak(data,TextToSpeech.QUEUE_ADD,null);
                                       }

                                   }

                               });

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
               audio = "";
           }


       });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                audio = Objects.requireNonNull(result).get(0);
            }
        }
    }

}