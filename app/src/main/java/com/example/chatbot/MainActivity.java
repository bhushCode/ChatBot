package com.example.chatbot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
private RecyclerView chatsRV;
private EditText userMsgEdt;
private FloatingActionButton sendMsgFAB;
private  FloatingActionButton user_mic;
private  final  String BOT_KEY="bot";
private  final  String USER_KEY="user";
private ArrayList<ChatsModel> chatsModelArrayList;
private  ChatRVAdapter chatRVAdapter;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatsRV = findViewById(R.id.idRVChats);
        userMsgEdt=findViewById(R.id.idEdtMessage);
        sendMsgFAB=findViewById(R.id.idFABsend);
        user_mic = findViewById(R.id.user_input_mic);
       chatsModelArrayList = new ArrayList<>();
       chatRVAdapter = new ChatRVAdapter(chatsModelArrayList,this);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    chatsRV.setLayoutManager(manager);
    chatsRV.setAdapter(chatRVAdapter);
    sendMsgFAB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(userMsgEdt.getText().toString().isEmpty())
            {
                Toast.makeText(MainActivity.this, "please enter your message", Toast.LENGTH_SHORT).show();
               return ;
            }
            getResponse(userMsgEdt.getText().toString());
            userMsgEdt.setText("");
        }
    });


    //user mic
    user_mic.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"speak now");
            try {
                startActivityForResult(intent,100);
            }
            catch (Exception e)
            {
                Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    });
    }

    private void getResponse(String message) {
    chatsModelArrayList.add(new ChatsModel(message,USER_KEY));
    chatRVAdapter.notifyDataSetChanged();
    String url ="http://api.brainshop.ai/get?bid=174895&key=O0xW3SOiFaHYyNz6&uid=[uid]&msg="+message;
    String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModel> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<MsgModel>() {
            @Override
            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                  if(response.isSuccessful())
                  {
                      MsgModel model = response.body();
                      chatsModelArrayList.add(new ChatsModel(model.getCnt(),BOT_KEY));
                      chatRVAdapter.notifyDataSetChanged();
                  }
            }

            @Override
            public void onFailure(Call<MsgModel> call, Throwable t) {
               chatsModelArrayList.add(new ChatsModel("please revert your question" +t,BOT_KEY));
               chatRVAdapter.notifyDataSetChanged();

            }
        });


}
//response from when user speak something
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     if(requestCode==100&&resultCode==RESULT_OK)
     {
         ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    //     userMsgEdt.setText(result.get(0));
         getResponse(result.get(0));
     }
}
}