package pranav.apps.amazing.debouncesearch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {

    private LogAdapter _logAdapter;

    private EditText input;
    private ListView _logView;



    //Flowable is much like an observable available in rx 2.x
    private Flowable<CharSequence> _inputTextSearch;

    //Disposable Subscriber is a type of observer available in 2.x version
    DisposableSubscriber<Boolean> _disposable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText)findViewById(R.id.input_txt_debounce);
        _logView = (ListView)findViewById(R.id.list_threading_log);

        _inputTextSearch = RxTextView.textChanges(input);



    }


    public class LogAdapter extends ArrayAdapter<String>{

        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log,R.id.item_log,logs);
        }
    }
}
