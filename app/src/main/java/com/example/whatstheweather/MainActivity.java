
package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextViewId);
    }

    public void getWeather(View view) {
        if (editText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Could not find weather!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                DownloadTask task = new DownloadTask();
                String encodedCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
                task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=174483a67626d4899af647aabb873ff6");
                //for disable keyboard
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
                resultTextView.setText(R.string.valid_name);
            }
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection urlConnection;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                resultTextView.setText(R.string.valid_name);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);
                StringBuilder message = new StringBuilder();
                String main = "";
                String description = "";
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                    if (!main.equals("") && !description.equals("")) {
                        message.append(main).append(": ").append(description).append("\r\n");
                    }
                }
                if (!main.equals("") && !description.equals("")) {
                    resultTextView.setText(message.toString());
                } else {
                    resultTextView.setText(R.string.valid_name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                resultTextView.setText(R.string.valid_name);

            }
        }
    }
}