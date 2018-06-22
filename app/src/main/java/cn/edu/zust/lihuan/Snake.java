package cn.edu.zust.lihuan;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;
public class   Snake extends Activity {
    private SnakeView mSnakeView;
    private Button mBt_up, mBt_down, mBt_left, mBt_right, mBt_home;
    private Vibrator vibrator;
    private Resources r;
    private static String ICICLE_KEY = "snake-view";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(cn.edu.zust.lihuan.R.layout.snake_layout);
        mSnakeView = (SnakeView) findViewById(cn.edu.zust.lihuan.R.id.snake);
        mSnakeView.setTextView((TextView) findViewById(cn.edu.zust.lihuan.R.id.text));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        r = getResources();
        if (savedInstanceState == null) {
            mSnakeView.setMode(SnakeView.READY);
        } else {
            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
            if (map != null) {
                mSnakeView.restoreState(map);
            } else {
                mSnakeView.setMode(SnakeView.PAUSE);
                mSnakeView.setMODE(SnakeView.PAUSE);
            }
        }
        mBt_up = findViewById(cn.edu.zust.lihuan.R.id.up);
        mBt_down = findViewById(cn.edu.zust.lihuan.R.id.dowm);
        mBt_left = findViewById(cn.edu.zust.lihuan.R.id.left);
        mBt_right = findViewById(cn.edu.zust.lihuan.R.id.right);
        mBt_home = findViewById(cn.edu.zust.lihuan.R.id.home);
        mBt_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case ACTION_DOWN:
                        mSnakeView.up();
                        vibrator.vibrate(30);
                        mSnakeView.addBuff();
                        break;
                    case ACTION_UP:
                        mSnakeView.deBuff();
                        break;
                }
                return false;
            }
        });
        mBt_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case ACTION_DOWN:
                        mSnakeView.down();
                        vibrator.vibrate(30);
                        mSnakeView.addBuff();
                        break;
                    case ACTION_UP:
                        mSnakeView.deBuff();
                        break;
                }
                return false;
            }
        });
        mBt_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case ACTION_DOWN:
                        mSnakeView.left();
                        vibrator.vibrate(30);
                        mSnakeView.addBuff();
                        break;
                    case ACTION_UP:
                        mSnakeView.deBuff();
                        break;
                }
                return false;
            }
        });
        mBt_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case ACTION_DOWN:
                        mSnakeView.right();
                        vibrator.vibrate(30);
                        mSnakeView.addBuff();
                        break;
                    case ACTION_UP:
                        mSnakeView.deBuff();
                        break;
                }
                return false;
            }
        });
        mBt_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeHome();
                mSnakeView.star();
                vibrator.vibrate(300);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        mSnakeView.setMode(SnakeView.PAUSE);
        changeHome();
        mSnakeView.setMODE(SnakeView.PAUSE);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }
    public void changeHome() {
        if (r != null) {
            if (mSnakeView.getMODE() == mSnakeView.PAUSE ||
                    mSnakeView.getMODE() == mSnakeView.READY || mSnakeView.getMODE() == mSnakeView.LOSE) {
                mBt_home.setBackground(r.getDrawable(R.drawable.btn_bg_home_selector));
            } else if (mSnakeView.getMODE() == mSnakeView.RUNNING) {
                mBt_home.setBackground(r.getDrawable(R.drawable.btn_bg_home2_selector));
            }
        } else {
        }
    }
}