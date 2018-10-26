package com.ginsmile.calculatorpro;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class displayActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实例化DBHelper
        final DBHelper helper=new DBHelper(this);
        //获得Cuosor
        Cursor c = helper.query();
        //列表项数组
        String[] from={"_id","expression_name","expression_result"};
        //列表项id
        int[] to={R.id.id,R.id.expression_name,R.id.expression_result};
        //适配器
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.display,c,from,to);
        //列表试图
        ListView listView = getListView();
        //为列表添加适配器
        listView.setAdapter(adapter);
        //对话框
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //为listview添加监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final long temp=arg3;
                builder.setMessage("是否删除记录？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除数据
                        helper.de1((int)temp);
                        //查询
                        Cursor c=helper.query();
                        String[] from={"_id","expression_name","expression_result"};
                        int[] to={R.id.id,R.id.expression_name,R.id.expression_result};
                        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),R.layout.display,c,from,to);
                        ListView listView=getListView();
                        listView.setAdapter(adapter);
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
//                //创建对话框
//                AlertDialog ad=builder.create();
//                //显示对话框
//                ad.show();
                builder.show();

            }
        });
        //关闭数据库
        helper.close();
    }
}
