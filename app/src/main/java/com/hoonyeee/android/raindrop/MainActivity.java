package com.hoonyeee.android.raindrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    FrameLayout layout;
    float displayHeight;
    float displayWidth;
    Stage stage;
    RunThread runThread;
    boolean isEnd, isEndMake, first;
    boolean pause, isFirst;
    Button start,stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        displayHeight = metrics.heightPixels;
        displayWidth = metrics.widthPixels;
        isEnd = false;
        isEndMake = false;
        first = true;
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        layout = findViewById(R.id.stage);
        stage = new Stage(this);
        layout.addView(stage);
        pause = false;
        isFirst = true;

        runThread = new RunThread();
        runThread.start();


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.stop:
                        stage.pause();
                        break;
                    case R.id.start:
                        if(isFirst) {
                            makeRainDrop();
                            isFirst = false;
                        }else{
                            if(pause){
                             //pause = false;
                                stage.restart();
                            }
                        }
                        break;
                }
            }
        };
        start.setOnClickListener(listener);
        stop.setOnClickListener(listener);
    }

    public void makeRainDrop(){
        new Thread(){
            public void run(){
                for (int i = 0; i < 50; i++) {
                    if(!pause) {
                        RainDrop rainDrop = new RainDrop((int) displayWidth, (int) displayHeight);
                        rainDrop.start();
                        stage.addRainDrop(rainDrop);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        //paused면 for문 안돌게
                        while(pause){
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                isEndMake = true;
            }
        }.start();
    }

    //화면갱신
    class RunThread extends Thread{
        public void run(){
            while(!isEnd){
                stage.postInvalidate(); //sub thread에서 view를 갱신시키기 위함
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class RainDrop extends Thread{
        public float x = 0;
        public float y = 0;
        public float radius = 0;
        public Paint paint;
        float speed = 0;
        float limit;

        public RainDrop(int width, int height){
            Random random = new Random();
            x = random.nextInt(width);
            y = 0 - radius;
            radius = random.nextInt(10) + 10;
            speed = random.nextInt(15) + 5;
            paint = new Paint();
            paint.setColor(Color.MAGENTA);

            limit = height;
        }
        public void run(){
            while( y < limit){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!pause)
                    y += speed;
            }
        }
    }

    // 스테이지
    class Stage extends View {
        List<RainDrop> rainDrops;

        public Stage(Context context) {
            super(context);
            rainDrops = new ArrayList<>();
        }
        public void addRainDrop(RainDrop rainDrop){
            rainDrops.add(rainDrop);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for(int i=0; i<rainDrops.size(); i++){
                RainDrop rainDrop = rainDrops.get(i);
                canvas.drawCircle(rainDrop.x, rainDrop.y, rainDrop.radius, rainDrop.paint);
            }

            /*if(isEndMake = true)
                isEnd= false;*/
        }

        public void pause() {
            pause = true;
        }

        public void restart(){
            pause = false;
        }
    }
}
