package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{
    private static final String BASE_URL = "https://restapi.amap.com/v3/weather/weatherInfo?";
    private static final String API_KEY = "bce82cb405b6d89d48d43ce6c4889c18";
    private static final String END_TYPE = "&extensions=base";
    ImageView iv_back;
    TextView tv_cityName,tv_cityID,tv_refreshTime,tv_temperature,tv_humidity,tv_weather,tv_province;
    Button btn_interser,btn_refresh;
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    String search_cityID;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        dbHelper = new MyDatabaseHelper(this,"city.db",null,1);
        db = dbHelper.getWritableDatabase();

        iv_back = findViewById(R.id.image_back);
        iv_back.setOnTouchListener(this);
        tv_province = findViewById(R.id.tv_province);
        tv_weather = findViewById(R.id.tv_weather);
        tv_cityName = findViewById(R.id.tv_cityName);
        tv_cityID = findViewById(R.id.tv_cityID);
        tv_refreshTime = findViewById(R.id.tv_refreshTime);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_humidity = findViewById(R.id.tv_humidity);
        btn_interser = findViewById(R.id.btn_interest);
        btn_interser.setOnClickListener(this);
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(this);

        search_cityID = getIntent().getStringExtra("cityID");
        //获取天气数据函数
        getWeather();
    }
    //返回图标触碰监听器
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view.getId()==R.id.image_back){
            finish();
        }
        return true;
    }
    //按钮点击监听器
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btn_refresh){
            getWeatherFromNetwork();
            Toast.makeText(this,"刷新成功",Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.btn_interest){
            if(tv_cityID.getText().toString().equals("")){
                Toast.makeText(this,"请先查询",Toast.LENGTH_SHORT).show();
            }
            else{
                ContentValues values = new ContentValues();
                values.put("cityID",tv_cityID.getText().toString());
                values.put("cityName",tv_cityName.getText().toString());
                long insert = db.insert("interestCity",null,values);
                if(insert<0){
                    Toast.makeText(this,"已经关注过了",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,"关注成功",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    //获取天气
    public void getWeather(){
        Boolean isExit = false;  //记录是否在缓存中
        //先在缓存中查找
        isExit = getWeatherFromCache();
        //若不在缓存中,网络查找
        if(!isExit){
            getWeatherFromNetwork();
            Toast.makeText(this,"在网络中查找",Toast.LENGTH_SHORT).show();
        }
    }
    //在缓存中获取天气
    public boolean getWeatherFromCache(){
        Boolean isExit = false;  //记录是否在缓存中
        Cursor cursor = db.query("tempCity",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") String cityID = cursor.getString(cursor.getColumnIndex("cityID"));
                if(cityID.equals(search_cityID)){
                    Toast.makeText(this,"在缓存中查找",Toast.LENGTH_SHORT).show();
                    @SuppressLint("Range") String province = cursor.getString(cursor.getColumnIndex("province"));
                    @SuppressLint("Range") String weather = cursor.getString(cursor.getColumnIndex("weather"));
                    @SuppressLint("Range") String cityName = cursor.getString(cursor.getColumnIndex("cityName"));
                    @SuppressLint("Range") String refreshTime = cursor.getString(cursor.getColumnIndex("refreshTime"));
                    @SuppressLint("Range") String temperature = cursor.getString(cursor.getColumnIndex("temperature"));
                    @SuppressLint("Range") String humidity = cursor.getString(cursor.getColumnIndex("humidity"));
                    isExit = true;
                    tv_province.setText(province);
                    tv_weather.setText(weather);
                    tv_cityID.setText(cityID);
                    tv_cityName.setText(cityName);
                    tv_refreshTime.setText(refreshTime);
                    tv_temperature.setText(temperature);
                    tv_humidity.setText(humidity);
                }
            }while (cursor.moveToNext());
        }
        return isExit;
    }
    //在网络中获取天气
    private Handler handler = new Handler(){
        public void handleMessage(Message message){
            City city = (City) message.obj;
            if(!(city==null)){
                tv_cityName.setText(city.getCityName());
                tv_cityID.setText(city.getCityId());
                tv_province.setText(city.getProvince());
                tv_weather.setText(city.getWeather());
                tv_refreshTime.setText(city.getRefreshTime());
                tv_temperature.setText(city.getTemperature());
                tv_humidity.setText(city.getHumidity());
                //如果获取了天气数据则缓存数据，！注意函数的位置和顺序
                InitIndex();
                index = getIndex();
                refreshCache();
            }
            else{
                Toast.makeText(WeatherActivity.this,"搜索的城市ID不存在",Toast.LENGTH_SHORT).show();
            }
        }
    };
    public void getWeatherFromNetwork(){
        Toast.makeText(this,"在网络中查找",Toast.LENGTH_SHORT).show();
        String path = BASE_URL+"city="+search_cityID+"&key="+API_KEY+END_TYPE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json;
                try {
                    json = GetJson.getJson(path);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                City city = AnalysisJson.getWeather(json);
                Message message = new Message();
                message.obj = city;
                handler.sendMessage(message);
            }
        }).start();
    }
    //初始数据库中索引值为1
    public void InitIndex(){
        Cursor cursor = db.query("indexNum",null,null,null,null,null,null);
        if(!cursor.moveToFirst()){
            ContentValues values = new ContentValues();
            values.put("num",1);
            db.insert("indexNum",null,values);
        }
    }
    //获取数据库中索引值
    @SuppressLint("Range")
    public int getIndex(){
        int index = 0;
        Cursor cursor = db.query("indexNum",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            index = cursor.getInt(cursor.getColumnIndex("num"));
        }
        return index;
    }
    //更行缓存
    public void refreshCache(){
        ContentValues values = new ContentValues();
        values.put("province",tv_province.getText().toString());
        values.put("weather",tv_weather.getText().toString());
        values.put("cityID",tv_cityID.getText().toString());
        values.put("cityName",tv_cityName.getText().toString());
        values.put("refreshTime",tv_refreshTime.getText().toString());
        values.put("temperature",tv_temperature.getText().toString());
        values.put("humidity",tv_humidity.getText().toString());
        //先获取缓存中数据是否有三条
        int number = 0;
        Cursor cursor = db.query("tempCity",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                number++;
            }while(cursor.moveToNext());
        }
        if(number<3){
            db.insert("tempCity",null,values);
        }
        else{
            db.update("tempCity",values,"num = ?",new String[]{String.valueOf(index)});
            //更新索引
            values.clear();
            index += 1;
            values.put("num",(index==4 ? 1 : index));
            db.insert("indexNum",null,values);
        }
    }
}