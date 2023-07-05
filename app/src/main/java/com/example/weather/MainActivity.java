package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{
    CityAdapter adapter;
    List<City> cityList = new ArrayList<>();
    Button btn_search,btn_searchByRank;
    EditText et_cityID;
    ListView listView;
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //关注城市列表listview
        adapter = new CityAdapter(this,R.layout.city_item,cityList);
        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        //其他控件
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        btn_searchByRank = findViewById(R.id.btn_searchByRank);
        btn_searchByRank.setOnClickListener(this);
        et_cityID = findViewById(R.id.et_cityID);
        //建立数据库
        dbHelper = new MyDatabaseHelper(this,"city.db",null,1);
        db = dbHelper.getWritableDatabase();
        refreshListview();
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btn_search){
            if(et_cityID.getText().toString().equals("")){
                Toast.makeText(this,"ID不能为空",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(this,WeatherActivity.class);
                intent.putExtra("cityID",et_cityID.getText().toString());
                startActivityForResult(intent,1);
            }
        } else if (id==R.id.btn_searchByRank) {
            Intent intent = new Intent(this,ProvinceListActivity.class);
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        City city = cityList.get(position);
        Intent intent = new Intent(this,WeatherActivity.class);
        intent.putExtra("cityID",city.getCityId());
        startActivityForResult(intent,1);
    }
    //长按取消关注
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        // 确认删除对话框构建
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("确认取消关注?");
        // 点击对话框的 确认 按钮后的操作
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.delete("interestCity","cityID = ?",new String[]{cityList.get(position).getCityId()});
                refreshListview();
            }
        });
        // 点击对话框的 取消 按钮后的操作
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 无操作
            }
        });
        builder.create().show();
        return true;
    }
    //刷新listview
    public void refreshListview(){
        cityList.clear();
        Cursor cursor = db.query("interestCity",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                @SuppressLint("Range") String cityID = cursor.getString(cursor.getColumnIndex("cityID"));
                @SuppressLint("Range") String cityName = cursor.getString(cursor.getColumnIndex("cityName"));
                City ciyt = new City(cityID,cityName);
                cityList.add(ciyt);
            }while (cursor.moveToNext());
        }
        else{
            //Toast.makeText(MainActivity.this,"数据库中无数据",Toast.LENGTH_SHORT).show();
        }
        listView.setAdapter(adapter);
    }
    //返回后刷新关注列表
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshListview();
    }
}