package com.ginsmile.calculatorpro;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    //显示区内容
    private String expression = "";
    private boolean last_equal = false;//上一次的按键是否为等号

    protected EditText text1;//第一行，用来显示按过等号之后的完整表达式
    protected EditText text2;//第二行，用来显示表达式和结果
    protected static boolean isSimple = true;//当前是否是简易计算器

    //动画效果
    private View board;
    private View board2;

    private int screen_width;
    private int screen_height;


    private LinearLayout display;
    private Button[] buttons;
   // private Button[] buttons2;

    //设置切换布局动画,切换成数据库列表，显示输入过的计算表达式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //绑定主布局
        setContentView(R.layout.activity_main);
        //绑定右上角切换按钮
        Button change_btn = (Button)findViewById(R.id.change);
        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this,displayActivity.class);
                startActivity(intent);
            }
        });

//绑定布局中上下两个显示框
        text1 = (EditText)findViewById(R.id.text1);
        text2 = (EditText)findViewById(R.id.text2);

        //初始化计算器键盘
        //简单计算器布局按钮
        buttons = new Button[18];
        //科学计算器布局按钮
        //buttons2 = new Button[30];
        initSimpleBoard(buttons);//初始化简易计算器键盘
        //initScienceBoard(buttons2);//初始化科学计算器键盘
        //绑定简单计算器布局
        board = (View)findViewById(R.id.board);
        //绑定科学计算器布局
        board2 = (View)findViewById(R.id.board2);

        //意外推出数据保存
        if(savedInstanceState != null){
            text1.setText(savedInstanceState.getString("text1"));
            text2.setText(savedInstanceState.getString("text2"));
            isSimple = savedInstanceState.getBoolean("isSimple");
            Log.v("TAG==>","OKKOKOKO");
        }
    }


    //活动被回收时，保存临时数据
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("text1", text1.getText().toString());
        outState.putString("text2", text2.getText().toString());
        outState.putBoolean("isSimple",isSimple);

    }


    //为了得到用户区域的高度，重写onWindowFocusChanged,这个方法在onResume之后被调用
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Dimension dimen1 = getAreaOne(this);
            Dimension dimen2 = getAreaTwo(this);
            Dimension dimen3 = getAreaThree(this);
            Log.v("one=>","Area one : \n\tWidth: "+dimen1.mWidth + ";\tHeight: "+dimen1.mHeight);
            Log.v("two=>","\nArea two: \n\tWidth: "+dimen2.mWidth + ";\tHeight: "+dimen2.mHeight);
            Log.v("three","\nArea three: \n\tWidth: "+dimen3.mWidth + ";\tHeight: "+dimen3.mHeight);

            Log.v("TAG","---isSimple=>>" + isSimple);
            screen_width = dimen3.mWidth;
            screen_height = dimen3.mHeight;

            initWidthAndHeight();
        }
    }

    //初始化键盘，显示区域的宽和高（显示区域包括change按钮，text，text2）
    private void initWidthAndHeight(){
        //设置change按钮和显示区域的高度只和 始终为用户区域高度的三分之一
        display  = (LinearLayout)findViewById(R.id.display);
        android.view.ViewGroup.LayoutParams lp =display.getLayoutParams();
        lp.height=screen_height/3;

        //简易计算器
        int btn_width = screen_width/4;
        int btn_height = (screen_height - screen_height/3)/5;//tablelayout为屏幕的2/3大，一共5行
        for(int i= 0; i < 18; i++){
            buttons[i].setWidth(btn_width);
            buttons[i].setHeight(btn_height);
        }

        buttons[0].setWidth(btn_width*2);
        buttons[16].setHeight(btn_height*2);


    }

    //初始化简易计算器键盘
    private void initSimpleBoard(final Button[] buttons){
        buttons[0] = (Button)findViewById(R.id.zero);
        buttons[1] = (Button)findViewById(R.id.one);
        buttons[2] = (Button)findViewById(R.id.two);
        buttons[3] = (Button)findViewById(R.id.three);
        buttons[4] = (Button)findViewById(R.id.four);
        buttons[5] = (Button)findViewById(R.id.five);
        buttons[6] = (Button)findViewById(R.id.six);
        buttons[7] = (Button)findViewById(R.id.seven);
        buttons[8] = (Button)findViewById(R.id.eight);
        buttons[9] = (Button)findViewById(R.id.nine);

        //清空
        buttons[10] = (Button)findViewById(R.id.empty);
        //删除
        buttons[11] = (Button)findViewById(R.id.delete);
        //除法
        buttons[12] = (Button)findViewById(R.id.divide);
        //乘法
        buttons[13] = (Button)findViewById(R.id.multiple);
        //减法
        buttons[14] = (Button)findViewById(R.id.minus);
        //加法
        buttons[15] = (Button)findViewById(R.id.plus);
        //等号
        buttons[16] = (Button)findViewById(R.id.equal);
        //小数点
        buttons[17] = (Button)findViewById(R.id.dot);


        initCommonBtns(buttons);
    }





    //初始化简易计算器，科学计算器相同的18个按钮
    private void initCommonBtns(final Button[] buttons){
        //添加监听事件
        //数字0～9
        for(int i = 0; i < 10; i++){
            final int m = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(last_equal){
                        expression = "";//这次按的数字，如果上次按了等号，则清空表达式
                        last_equal = false;
                    }
                    expression += buttons[m].getText();
                    text2.setText(expression);
                    text2.setSelection(expression.length());
                }
            });
        }
        //empty
        buttons[10].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expression = "";
                text2.setText("0");
                text1.setText(null);
                last_equal = false;
            }
        });
        //delete
        buttons[11].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expression.length() < 1){
                    return;
                }
                expression = expression.substring(0,expression.length()-1);
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //divide
        buttons[12].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[12].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //multiple
        buttons[13].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[13].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //minus
        buttons[14].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[14].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //plus
        buttons[15].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[15].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
        //equal
        buttons[16].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(last_equal) return;//如果上次还是按的等号，那么什么也不做

                //小小的动画效果
                AnimationSet animSet = new AnimationSet(true);
                TranslateAnimation ta = new TranslateAnimation(0,0,0,-100);
                ta.setDuration(80);
                AlphaAnimation aa = new AlphaAnimation(1f, 0f);
                aa.setDuration(75);
                animSet.addAnimation(ta);
                animSet.addAnimation(aa);
                text2.startAnimation(animSet);
                animSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //动画结束之后计算
                        text1.setText(expression + "=");
                        text1.setSelection(expression.length()+1);//在第一行显示计算表达式
                        try{
                            expression = Calculate.calculate(expression);
                            text2.setText(expression);//在第二行显示计算结果

                            //获取EditText,text1内容，text2内容
                            String expression_name=text1.getText().toString();
                            String expression_result=text2.getText().toString();
                            ContentValues values = new ContentValues();
                            //在values中添加内容
                            values.put("expression_name",expression_name);
                            values.put("expression_result",expression_result);
                            //实例化数据库类
                            DBHelper helper = new DBHelper(getApplicationContext());
                            //插入数据
                            helper.insert(values);

                            //查找
                            Cursor cursor = helper.query();
                            Log.d("yyj", "onAnimationEnd: "+cursor.moveToFirst());
                            if (cursor.moveToFirst()){
                                do{
                                    int id = cursor.getInt(cursor.getColumnIndex("_id"));
                                    String ss = cursor.getString(cursor.getColumnIndex("expression_name"));
                                    Log.d("yyj", "onAnimationEnd: " + id + " =====" + ss);
                                }
                                while (cursor.moveToNext());
                            }


                        }catch(Exception exception){
                            text2.setText("表达式错误!");//在第二行显示计算结果
                            expression = "";
                        }


                        //实例化intent
//                        Intent intent = new Intent(MainActivity.this,displayActivity.class);
//                        startActivity(intent);
                        //启动intent

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });


                // 为下一次按计算器键盘做准备。
                // 如果下次按的是数字，那么清空第二行重新输入第一个数。
                // 如果是非数字，那就当这次的结果是输入的第一个数，直接参与运算。
                last_equal = true;

            }


        });
        buttons[17].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression += buttons[17].getText();
                text2.setText(expression);
                text2.setSelection(expression.length());
                last_equal = false;
            }
        });
    }



    //屏幕高度
    private Dimension getAreaOne(Activity activity){
        Dimension dimen = new Dimension();
        Display disp = activity.getWindowManager().getDefaultDisplay();
        Point outP = new Point();
        disp.getSize(outP);
        dimen.mWidth = outP.x ;
        dimen.mHeight = outP.y;
        return dimen;
    }
    //不算状态栏的高度
    private Dimension getAreaTwo(Activity activity){
        Dimension dimen = new Dimension();
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        System.out.println("top:"+outRect.top +" ; left: "+outRect.left) ;
        dimen.mWidth = outRect.width() ;
        dimen.mHeight = outRect.height();
        return dimen;
    }
    //不算状态栏，标题栏的高度
    private Dimension getAreaThree(Activity activity){
        Dimension dimen = new Dimension();
        // 用户绘制区域
        Rect outRect = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
        dimen.mWidth = outRect.width() ;
        dimen.mHeight = outRect.height();
        // end
        return dimen;
    }
    private class Dimension {
        public int mWidth ;
        public int mHeight ;
        public Dimension(){}
    }



}


