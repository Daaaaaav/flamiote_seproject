package com.example.flamiotefirealarmapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatActivity extends AppCompatActivity {
    private TextView chatLog;
    private EditText userInput;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private com.example.flamiotefirealarmapp.ChatAdapter chatAdapter;
    private ArrayList<com.example.flamiotefirealarmapp.ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendButton);
        chatLog = findViewById(R.id.chatLog);

        chatMessages = new ArrayList<>();
        chatAdapter = new com.example.flamiotefirealarmapp.ChatAdapter(chatMessages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String userMessage = userInput.getText().toString();
            if (!userMessage.isEmpty()) {
                userInput.setText("");
                chatMessages.add(new com.example.flamiotefirealarmapp.ChatMessage(userMessage, ChatMessage.TYPE_USER));
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                queryBotResponse(userMessage);
            }
        });
    }

    private void queryBotResponse(String message) {
        chatLog.append("\nYou: " + message);
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String userMessage = params[0];
                try {
                    URL url = new URL("http://10.0.2.2:5000/chat");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    JsonObject json = new JsonObject();
                    json.addProperty("message", userMessage);
                    json.addProperty("session_id", "default");
                    OutputStream os = conn.getOutputStream();
                    os.write(json.toString().getBytes("UTF-8"));
                    os.close();
                    Scanner input = new Scanner(conn.getInputStream());
                    StringBuilder response = new StringBuilder();
                    while (input.hasNextLine()) {
                        response.append(input.nextLine());
                    }
                    input.close();

                    JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                    return jsonResponse.get("response").getAsString();

                } catch (Exception e) {
                    Log.e("ChatActivity", "Error querying bot", e);
                    return "Error: Unable to connect to server.";
                }
            }

            @Override
            protected void onPostExecute(String botResponse) {
                chatMessages.add(new com.example.flamiotefirealarmapp.ChatMessage(botResponse, ChatMessage.TYPE_BOT));
                chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            }
        }.execute(message);
    }
}
