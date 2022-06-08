package com.flir.flironeexampleapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class resultsPreview extends AppCompatActivity {

    private static final String TAG = "resultsPreview";
    LineGraphSeries<DataPoint> series;
    int peakCount = 0;
    private ArrayList<DataPoint> rsltArr;

    String Click_on_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_preview);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Click_on_button = bundle.getString("CLICK");

            if (Click_on_button == null || Click_on_button.isEmpty()) {
                finish();
            }

        } else {
            finish();
        }

        rsltArr = (ArrayList<DataPoint>) getIntent().getSerializableExtra("resultsList");
        double x, y;
        x = 0;
        GraphView graph = (GraphView) findViewById(R.id.signalView);
        /*ImageView tickView = (ImageView) findViewById(R.id.tickView);*/
        series = new LineGraphSeries<DataPoint>();
        series.setTitle("Breath rate");

        ArrayList<Float> inArr = new ArrayList<>();

        for (int i = 0; i < rsltArr.size(); i++) {
            Float f = Float.parseFloat(rsltArr.get(i).getY() + "");
            inArr.add(f);
        }
        /*getVolumeFromNetwork(inArr);*/
        double sampleRate = rsltArr.size() / 30.0;
        DataPoint point;
        for (int i = 10; i < inArr.size(); i++) {
            point = new DataPoint(rsltArr.get(i).getX(), inArr.get(i));
            rsltArr.set(i, point);
            series.appendData(rsltArr.get(i), true, 270);
        }

        double nextTemp1, nextTemp2, nextTemp3, currentTemp, prevTemp1, prevTemp2, prevTemp3;

        for (int i = 10; i < rsltArr.size() - 6; i++) {

            prevTemp1 = rsltArr.get(i).getY();
            prevTemp2 = rsltArr.get(i + 1).getY();
            prevTemp3 = rsltArr.get(i + 2).getY();
            currentTemp = rsltArr.get(i + 3).getY();
            nextTemp1 = rsltArr.get(i + 4).getY();
            nextTemp2 = rsltArr.get(i + 5).getY();
            nextTemp3 = rsltArr.get(i + 6).getY();
            if (currentTemp > prevTemp1 && currentTemp > prevTemp2 && currentTemp > prevTemp3 && currentTemp > nextTemp1 && currentTemp > nextTemp2 && currentTemp > nextTemp3) {
                peakCount++;
            }
        }


        series.setColor(0xffe80202);
        graph.addSeries(series);


        graph.setTitle("Respiration Waveform");
        graph.setTitleTextSize(72);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Temperature (C)");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (seconds)");

        graph.setPadding(5, 5, 5, 5);
        graph.getViewport().setScalable(true);


        if (Click_on_button.equals("button1")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.breathRateView)).setText("Respiratory Rate = " + (peakCount * 2) + " breaths/min");
                }
            });
            if ((peakCount * 2) > 25 || (peakCount * 2) < 12) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your breathing is abnormal.");
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your breathing is normal.");
                    }
                });
            }
        }

        if (Click_on_button.equals("button2")) {

            getVolumeFromNetwork(inArr);

        }
        if (Click_on_button.equals("button3")) {

            getVolumeFromNetwork2(inArr);
        }
            if (Click_on_button.equals("button9")) {
            double fvc = getMaxValue(rsltArr)-getMinValue(rsltArr);
            double fev1 = get1Value(rsltArr)-getMinValue(rsltArr);
            double ratio=(fev1/fvc)*100;
            float f = (float)ratio;
            String fnew  = String.format("%.2f", f);


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.breathRateView)).setText("FEV1/FVC = " + fnew + " %");
                }
            });

            if((f)>79){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your FEV1/FVC is normal.");
                    }
                });
            }
            if((f>69) && (f<=79)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your FEV1/FVC is mildly abnormal.");
                    }
                });
            }
            if((f>59) && (f<=69)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your FEV1/FVC is moderately abnormal.");
                    }
                });
            }
            if((f>49) && (f<=59)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your FEV1/FVC is moderate to severely abnormal.");
                    }
                });
            }
            if((f>=35) && (f<=49)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your FEV1/FVC is severely abnormal.");
                    }
                });
            }
            if((f<35)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.tv_volume)).setText("Your FEV1/FVC is very severely abnormal.");
                    }
                });
            }
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return;
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users"); //Creating a sheet

        for (int i = 0; i < rsltArr.size(); i++) {

            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(rsltArr.get(i).getX());
            row.createCell(1).setCellValue(rsltArr.get(i).getY());
        }

        String fileName = "rrr.xlsx"; //Name of the file

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File folder = new File(extStorageDirectory, "Download");// Name of the folder you want to keep your file in the local storage.
        folder.mkdir(); //creating the folder
        File file = new File(folder, fileName);
        try {
            file.createNewFile(); // creating the file inside the folder
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(file); //Opening the file
            workbook.write(fileOut); //Writing all your row column inside the file
            fileOut.close(); //closing the file and done

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void getVolumeFromNetwork(ArrayList<Float> inArr) {
        new Thread(() -> {
            try {
                StringBuilder input = new StringBuilder();
                for (int i = 0; i < inArr.size(); i++) {
                    Float aFloat = inArr.get(i);
                    if (i == inArr.size() - 1) {
                        input.append(aFloat);
                    } else {
                        input.append(aFloat).append(",");
                    }
                }
                String jsonInput = "{\"input\":\"" + input.toString().concat("\"}");
                String url = "http://34.67.244.231:80/get_volume?key=AIzaSyDv5c2Os5OOP0CmofJKKGlhSqDxZkoQ9F0";
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonInput);
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                String result = jsonObject.getString("result");
                runOnUiThread(() -> ((TextView) findViewById(R.id.tv_volume))
                        .setText(String.format("Breathing Volume = %s" + " L", result)));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "getVolumeFromNetwork: ", e);
            }

        }).start();


    }

    private void getVolumeFromNetwork2(ArrayList<Float> inArr) {
        new Thread(() -> {
            try {
                StringBuilder input = new StringBuilder();
                for (int i = 0; i < inArr.size(); i++) {
                    Float aFloat = inArr.get(i);
                    if (i == inArr.size() - 1) {
                        input.append(aFloat);
                    } else {
                        input.append(aFloat).append(",");
                    }
                }
                String jsonInput = "{\"input\":\"" + input.toString().concat("\"}");
                String url = "http://34.67.244.231:80/get_volume?key=AIzaSyDv5c2Os5OOP0CmofJKKGlhSqDxZkoQ9F0";
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, jsonInput);
                Request request = new Request.Builder()
                        .url(url)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                String result = jsonObject.getString("result");
                runOnUiThread(() -> ((TextView) findViewById(R.id.tv_volume))
                        .setText(String.format("FVC = %s" + " L", result)));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "getVolumeFromNetwork: ", e);
            }

        }).start();


    }

    public double getMaxValue(ArrayList<DataPoint> rsltArr) {
        double maxValue = rsltArr.get(0).getY();
        for (int i = 1; i < rsltArr.size(); i++) {
            if (rsltArr.get(i).getY() > maxValue) {
                maxValue = rsltArr.get(i).getY();
            }
        }
        return maxValue;
    }

    public double getMinValue(ArrayList<DataPoint> rsltArr) {
        double minValue = rsltArr.get(0).getY();
        for (int i = 1; i < rsltArr.size(); i++) {
            if (rsltArr.get(i).getY() < minValue) {
                minValue = rsltArr.get(i).getY();
            }
        }
        return minValue;
    }

    /*public double getMinValue(ArrayList<DataPoint> rsltArr) {
        double minValue = 0;

        for (int i = 0; i < rsltArr.size(); i++) {
            double time0 = (rsltArr.get(i).getX());
            if(time0==0 || time0==0.1 || time0==0.2 || time0==0.3 || time0==0.4) {
                minValue = (rsltArr.get(i).getY());
            }

        }
        return minValue;
    }*/

    public double get1Value(ArrayList<DataPoint> rsltArr) {
        double temp1 = 0;

        for (int i = 0; i < rsltArr.size(); i++) {
            double time1 = (rsltArr.get(i).getX());
            if(time1==1.3 || time1==1.2 || time1==1.1 || time1==1) {
                temp1 = (rsltArr.get(i).getY());
            }

        }
        return temp1;
    }
}

