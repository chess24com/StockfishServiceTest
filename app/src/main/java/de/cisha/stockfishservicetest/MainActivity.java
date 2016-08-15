package de.cisha.stockfishservicetest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.incoming_text)
    TextView mIncomingText;

    @BindView(R.id.outgoing_text)
    EditText mOutGoingText;

    @OnClick(R.id.fab)
    public void bindToService(View view) {
        mIncomingText.setText(mIncomingText.getText() + "Binding to service...\n");
        // TODO

        Snackbar.make(view, "Succesfully bound to StockfishService", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
    }

}
