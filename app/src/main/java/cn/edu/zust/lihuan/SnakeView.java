
package cn.edu.zust.lihuan;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class SnakeView extends TileView {
    private static final String TAG = "SnakeView";
    private int mMode = READY;
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;
    private int mDirection = NORTH;
    private int mNextDirection = NORTH;
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;
    private static final int APPLE = 1;
    private static final int HEAD = 2;
    private static final int DEADHEAD = 3;
    private static final int BODY = 4;
    private static final int APPLE2 = 5;
    private static int MODE = READY;

    public static void setMODE(int MODE) {
        SnakeView.MODE = MODE;
    }

    public int getMODE() {
        return MODE;
    }

    private long mScore = 0;
    private long mMoveDelay = 100;
    private boolean mBuff = false;
    private int gorwTag = 1;

    /**
     * Give our snake some buff!
     */
    public void addBuff() {
        if (!mBuff) {
            mBuff = true;
            mMoveDelay /= 5;
            loadTile(APPLE2, r.getDrawable(R.drawable.redstar));

        }
    }

    public void deBuff() {
        if (mBuff) {
            mBuff = false;
            mMoveDelay *= 5;
        }
        loadTile(APPLE2, r.getDrawable(R.drawable.greenstar));
    }

    private long mLastMove;
    private TextView mStatusText;
    private ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mAppleList = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mAppleList2 = new ArrayList<Coordinate>();
    /**
     * Everyone needs a little randomness in their life
     */
    private static final Random RNG = new Random();
    private RefreshHandler mRedrawHandler = new RefreshHandler();
    Resources r = this.getContext().getResources();

    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            SnakeView.this.update();
            SnakeView.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            //     Toast.makeText(SnakeView.this.getContext(),"这是一个TOAST",Toast.LENGTH_SHORT).show();
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }

    public SnakeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnakeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initSnakeView();
    }

    private void initSnakeView() {
        setFocusable(true);
        resetTiles(6);
        loadTile(HEAD, r.getDrawable(cn.edu.zust.lihuan.R.drawable.head));
        loadTile(APPLE, r.getDrawable(cn.edu.zust.lihuan.R.drawable.apple));
        loadTile(BODY, r.getDrawable(cn.edu.zust.lihuan.R.drawable.body));
        loadTile(DEADHEAD, r.getDrawable(cn.edu.zust.lihuan.R.drawable.deadhead));
        loadTile(APPLE2, r.getDrawable(R.drawable.redstar));
    }

    private void initNewGame() {
        mSnakeTrail.clear();
        mAppleList.clear();
        mAppleList2.clear();
        gorwTag = 1;
        // that's just turned north
        for (int i = 4; i > 0; i--) {
            mSnakeTrail.add(new Coordinate(i, 7));
        }
        mNextDirection = NORTH;
        // How many apples to start with
        for (int i = 0; i < 100; i++) {
            addRandomApple();
        }
        for (int i = 0; i < 5; i++) {
            addRandomApple2();
        }
        //init time of snake
        mMoveDelay = 300;
        mScore = 0;
    }

    private int[] coordArrayListToArray(ArrayList<Coordinate> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            Coordinate c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }

    public Bundle saveState() {
        Bundle map = new Bundle();
        map.putIntArray("mAppleList", coordArrayListToArray(mAppleList));
        map.putIntArray("mAppleList2", coordArrayListToArray(mAppleList2));
        map.putInt("mDirection", Integer.valueOf(mDirection));
        map.putInt("mNextDirection", Integer.valueOf(mNextDirection));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putLong("mScore", Long.valueOf(mScore));
        map.putIntArray("mSnakeTrail", coordArrayListToArray(mSnakeTrail));
        return map;
    }

    private ArrayList<Coordinate> coordArrayToArrayList(int[] rawArray) {
        ArrayList<Coordinate> coordArrayList = new ArrayList<Coordinate>();
        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 2) {
            Coordinate c = new Coordinate(rawArray[index], rawArray[index + 1]);
            coordArrayList.add(c);
        }
        return coordArrayList;
    }

    public void restoreState(Bundle icicle) {
        setMode(PAUSE);
        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList"));
        mAppleList2 = coordArrayToArrayList(icicle.getIntArray("mAppleList2"));
        mDirection = icicle.getInt("mDirection");
        mNextDirection = icicle.getInt("mNextDirection");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
    }

    public void star() {
        if (mMode == READY | mMode == LOSE) {
            initNewGame();
            setMode(RUNNING);
            this.MODE = RUNNING;
            update();
        } else if (mMode == PAUSE) {
            setMode(RUNNING);
            this.MODE = RUNNING;
            update();
        } else if (mMode == RUNNING) {
            setMode(PAUSE);
            this.MODE = PAUSE;
            update();
        }
    }

    public void up() {
        if (mDirection != SOUTH) {
            mNextDirection = NORTH;
            loadTile(HEAD, r.getDrawable(R.drawable.head));
        }
    }

    public void down() {
        if (mDirection != NORTH) {
            mNextDirection = SOUTH;
            loadTile(HEAD, r.getDrawable(R.drawable.head2));
        }
    }

    public void left() {
        if (mDirection != EAST) {
            mNextDirection = WEST;
            loadTile(HEAD, r.getDrawable(R.drawable.head3));
        }
    }

    public void right() {
        if (mDirection != WEST) {
            mNextDirection = EAST;
            loadTile(HEAD, r.getDrawable(R.drawable.head4));
        }
    }

    public void setTextView(TextView newView) {
        mStatusText = newView;
    }

    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == RUNNING & oldMode != RUNNING) {
            mStatusText.setVisibility(View.INVISIBLE);
            update();
            return;
        }
        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == PAUSE) {
            str = res.getText(cn.edu.zust.lihuan.R.string.mode_pause);
        }
        if (newMode == READY) {
            str = res.getText(cn.edu.zust.lihuan.R.string.mode_ready);
        }
        if (newMode == LOSE) {
            str = res.getString(cn.edu.zust.lihuan.R.string.mode_lose_prefix) + mScore
                    + res.getString(cn.edu.zust.lihuan.R.string.mode_lose_suffix);
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }

    private void addRandomApple() {
        Coordinate newCoord = null;
        boolean found = false;
        while (!found) {
            // Choose a new location for our apple
            int newX = 1 + RNG.nextInt(mXTileCount - 2);
            int newY = 1 + RNG.nextInt(mYTileCount - 2);
            newCoord = new Coordinate(newX, newY);
            // Make sure it's not already under the snake
            boolean collision = false;
            int snakelength = mSnakeTrail.size();
            for (int index = 0; index < snakelength; index++) {
                if (mSnakeTrail.get(index).equals(newCoord)) {
                    collision = true;
                }
            }
            found = !collision;
        }
        if (newCoord == null) {
            Log.e(TAG, "Somehow ended up with a null newCoord!");
        }
        mAppleList.add(newCoord);
    }

    private void addRandomApple2() {
        Coordinate newCoord = null;
        boolean found = false;
        while (!found) {
            int newX = 1 + RNG.nextInt(mXTileCount - 2);
            int newY = 1 + RNG.nextInt(mYTileCount - 2);
            newCoord = new Coordinate(newX, newY);
            boolean collision = false;
            int snakelength = mSnakeTrail.size();
            for (int index = 0; index < snakelength; index++) {
                if (mSnakeTrail.get(index).equals(newCoord)) {
                    collision = true;
                }
            }
            found = !collision;
        }
        if (newCoord == null) {
            Log.e(TAG, "Somehow ended up with a null newCoord!");
        }
        mAppleList2.add(newCoord);
    }

    public void update() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();
            if (now - mLastMove > mMoveDelay) {
                clearTiles();
                updateWalls();
                updateSnake();
                updateApples();
                updateApples2();
                mLastMove = now;
            }
            mRedrawHandler.sleep(mMoveDelay);
        }
    }

    private void updateWalls() {
        for (int x = 0; x < mXTileCount; x++) {
            setTile(DEADHEAD, x, 0);
            setTile(DEADHEAD, x, mYTileCount - 1);
        }
        for (int y = 1; y < mYTileCount - 1; y++) {
            setTile(DEADHEAD, 0, y);
            setTile(DEADHEAD, mXTileCount - 1, y);
        }
    }

    private void updateApples() {
        for (Coordinate c : mAppleList) {
            setTile(APPLE, c.x, c.y);
        }
    }

    private void updateApples2() {
        for (Coordinate c : mAppleList2) {
            setTile(APPLE2, c.x, c.y);
        }
    }

    private void updateSnake() {
        boolean growSnake = false;
        // grab the snake by the head
        Coordinate head = mSnakeTrail.get(0);
        Coordinate newHead = new Coordinate(1, 1);
        mDirection = mNextDirection;
        switch (mDirection) {
            case EAST: {
                newHead = new Coordinate(head.x + 1, head.y);
                break;
            }
            case WEST: {
                newHead = new Coordinate(head.x - 1, head.y);
                break;
            }
            case NORTH: {
                newHead = new Coordinate(head.x, head.y - 1);
                break;
            }
            case SOUTH: {
                newHead = new Coordinate(head.x, head.y + 1);
                break;
            }
        }
        if ((newHead.x < 1) || (newHead.x > mXTileCount - 2)
                ) {
            setMode(LOSE);
            return;

        } else if (newHead.y == 0) {//穿上面的墙

            newHead.y = mYTileCount - 2;
        } else if (newHead.y == mYTileCount - 1) {//穿下面的墙

            newHead.y = 1;
        }
        int snakelength = mSnakeTrail.size();
        for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {
            Coordinate c = mSnakeTrail.get(snakeindex);
            if (c.equals(newHead)) {
                setMode(LOSE);
                return;
            }
        }
        int applecount = mAppleList.size();
        int applecount2 = mAppleList2.size();
        for (int appleindex = 0; appleindex < applecount; appleindex++) {
            Coordinate c = mAppleList.get(appleindex);
            if (c.equals(newHead)) {
                mAppleList.remove(c);
                addRandomApple();
                mScore++;
              //  mMoveDelay *= 0.998;
                growSnake = true;
            }
        }
        for (int appleindex = 0; appleindex < applecount2; appleindex++) {
            Coordinate c = mAppleList2.get(appleindex);
            if (c.equals(newHead)) {
                gorwTag = 2;
                mAppleList2.remove(c);
                addRandomApple2();
                mScore += 5;
                //chenge time of snake
                //mMoveDelay *= 0.998;
                growSnake = true;
            }
        }
        switch (gorwTag) {
            case 1:
                mSnakeTrail.add(0, newHead);
                break;

            case 2:
                for (int i = 0; i < 4; i++) {
                    mSnakeTrail.add(0, newHead);
                }
                gorwTag = 1;
                for (int i = 0; i < 4; i++) {
                    updateSnake();
                }
                break;
            default:
                break;
        }
        if (!growSnake) {
            mSnakeTrail.remove(mSnakeTrail.size() - 1);
        }
        // draw snake
        int index = 0;
        for (Coordinate c : mSnakeTrail) {
            if (index == 0) {
                setTile(HEAD, c.x, c.y);
            } else {
                setTile(BODY, c.x, c.y);
            }
            index++;
        }

    }

    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }

}
