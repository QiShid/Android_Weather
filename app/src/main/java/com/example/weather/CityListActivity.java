package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CityListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnTouchListener{
    private static final String PATH_HEAD = "https://restapi.amap.com/v3/config/district?keywords=";
    private static final String PATH_END = "&subdistrict=1&key=bce82cb405b6d89d48d43ce6c4889c18";

    ListView listView;
    ImageView iv_back;
    List<City> CityList= new ArrayList<>();
    CityAdapter adapter;
    TextView tv_province;
    String province,path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        tv_province = findViewById(R.id.tv_province);
        province = getIntent().getStringExtra("province");
        tv_province.setText(province);
        path = PATH_HEAD+province+PATH_END;

        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        adapter = new CityAdapter(CityListActivity.this,R.layout.city_item,CityList);

        iv_back = findViewById(R.id.image_back);
        iv_back.setOnTouchListener(this);

        getCitys();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        City city = CityList.get(i);
        Intent intent = new Intent(this,WeatherActivity.class);
        intent.putExtra("cityID",city.getCityId());
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId()==R.id.image_back){
            finish();
        }
        return true;
    }
    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            CityList = (List<City>) message.obj;
            if(!(CityList==null)){
                adapter = new CityAdapter(CityListActivity.this,R.layout.city_item,CityList);
                listView.setAdapter(adapter);
            }
            else{
                Toast.makeText(CityListActivity.this,"网络搜索出错",Toast.LENGTH_SHORT).show();
            }
        }
    };
    public void getCitys(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json;
                List<City> cityList = new ArrayList<>();
                try {
                    json = GetJson.getJson(path);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    cityList = AnalysisJson.getcitys(json);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Message message = new Message();
                message.obj = cityList;
                handler.sendMessage(message);
            }
        }).start();
    }
}