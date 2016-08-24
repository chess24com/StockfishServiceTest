package de.cisha.stockfishservicetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.cisha.stockfishservice.StockfishService;

import static de.cisha.stockfishservice.StockfishService.MSG_KEY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "StockfishServiceTest";

    @BindView(R.id.incoming_text)
    TextView mIncomingText;

    @BindView(R.id.outgoing_text)
    EditText mOutGoingText;

    @OnClick(R.id.fab)
    public void crashService(View view) {
        mIncomingText.setText(mIncomingText.getText() + "Crashing service...\n");
        // TODO implement
        mIncomingText.setText(mIncomingText.getText() + "Not implemented yet.\n");
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setUpEditText(mOutGoingText);
    }

    void setUpEditText(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeActionLabel("Send", EditorInfo.IME_ACTION_SEND);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText.setLines(1);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendCommand(textView.getText().toString() + "\n");
                    textView.setText("");
                    handled = true;
                }
                return handled;
            }
        });
    }

    void sendCommand(String cmd) {
        if (mService == null) {
            mIncomingText.setText(mIncomingText.getText() + "Service is down.\n");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(StockfishService.MSG_KEY, cmd);
        Message msg = Message.obtain();
        Log.v(TAG, "sending message: " + cmd);
        msg.replyTo = mMessenger;
        msg.setData(bundle);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            mService = null;
        }
    }

    Messenger mService = null;

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.v(TAG, "incoming message");
            Bundle data = msg.peekData();
            if (data == null) {
                Log.d(TAG, "data is null");
                super.handleMessage(msg);
                return;
            }
            data.setClassLoader(Thread.currentThread().getContextClassLoader());
            String line = data.getString(MSG_KEY);
            if (line == null) {
                Log.d(TAG, "line is null");
                super.handleMessage(msg);
                return;
            }
            mIncomingText.setText(mIncomingText.getText() + line);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, StockfishService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = new Messenger(service);
            mIncomingText.setText(mIncomingText.getText() + "Service connected.\n");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mIncomingText.setText(mIncomingText.getText() + "Service disconnected.\n");
        }
    };

}
