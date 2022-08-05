package com.nothinglin.mysqlconnection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    //声明视图组件
    private Button button;
    private TextView textView;
    private static final int TEST_USER_SELECT = 1;
    int i =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //注册视图组件
        button = (Button) findViewById(R.id.bt_send);
        textView = (TextView) findViewById(R.id.tv_response);
    }


    @Override
    protected void onStart() {
        super.onStart();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行查询操作
                //通过点击button i 自增长查询对应的id的name
                //这里的判断就可以分析出为什么单击该会循环
                if (i<=3){
                    i++;
                }else {
                    i = 1;
                }

                //连接数据库进行操作，这个过程联网过程需要再主线程中进行操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection conn = null;
                        conn = (Connection) DBOpenHelper.getConn();

                        String sql = "select name from info where id='"+i+"'";
                        Statement statement;

                        try {
                            statement = (Statement) conn.createStatement();
                            ResultSet rs = statement.executeQuery(sql);
                            while (rs.next()){

                                Info info = new Info();
                                info.setName(rs.getString(1));//0是第一位，1是第二位
                                Message msg = new Message();
                                msg.what = TEST_USER_SELECT;
                                msg.obj = info;
                                handler.sendMessage(msg);

                            }
                            statement.close();
                            conn.close();

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }).start();


            }

        });

    }




    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String user;
            switch (msg.what){
                case TEST_USER_SELECT:
                    Info info = (Info) msg.obj;
                    user = info.getName();
                    System.out.println("******************");
                    System.out.println("******************");
                    System.out.println("user:"+user);

                    textView.setText(user);
                    break;

            }
        }
    };


}