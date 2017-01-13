package pranav.apps.amazing.debouncesearch;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

import static java.lang.String.format;

public class MainActivity extends AppCompatActivity {

    private LogAdapter _logAdapter;

    private EditText input;

    private ListView _logView;
    private List<String> _logs;

    private ImageButton clr;


    //Flowable is much like an observable available in rx 2.x
    //private Observable<CharSequence> _inputTextSearch;

    //Disposable Subscriber is a type of observer available in 2.x version
    //DisposableSubscriber<Boolean> _disposable;

    private Disposable _disposable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = (EditText)findViewById(R.id.input_txt_debounce);
        _logView = (ListView)findViewById(R.id.list_threading_log);
        clr=(ImageButton)findViewById(R.id.clr_debounce);
        _setupLogger();
        //using interop library we made an instance of v2 flowable from rx binding which supports v1 only :(
        //_inputTextSearch = RxJavaInterop.toV2Observable(RxTextView.textChanges(input));

        //Here we will schedule the observable to observer
        //in textChangeEvents value is emitted immediately on subscribe
        _disposable = RxJavaInterop.toV2Observable(RxTextView.textChangeEvents(input))
                .debounce(400, TimeUnit.MILLISECONDS)// default Scheduler is Computation
                .filter(changes -> isNotNullOrEmpty(input.getText().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(_getObserver());
        clr.setOnClickListener(view -> {
            _logs = new ArrayList<>();
            _logAdapter.clear();
        });



    }

    private boolean isNotNullOrEmpty(String s) {
        if(s!= null){
            return true;
        }
        else
            return false;
    }
    //method returning an observer

    private DisposableObserver<TextViewTextChangeEvent> _getObserver(){

        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            /*As our observable is an observable to CharSequence and
              observer is a disposable subscriber to  textchange so will receive similar thing as argument
            * */
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                _log(format("Searching for %s",textViewTextChangeEvent.text().toString()));
                //_log("Searching for "+ textViewTextChangeEvent.text().toString()+"\n");

            }

            @Override
            public void onError(Throwable t) {
                Timber.d("Oops an error occur");
                _log("Oops there is an error");
            }

            @Override
            public void onComplete() {
                Timber.d("........complete");
            }
        };

    }

    private void _setupLogger() {
        _logs = new ArrayList<>();
        _logAdapter = new LogAdapter(MainActivity.this, new ArrayList<>());
        _logView.setAdapter(_logAdapter);
    }

    private void _log(String logMsg) {

        if (_isCurrentlyOnMainThread()) {
            _logs.add(0, logMsg + " (main thread)\n");
            _logAdapter.clear();
            _logAdapter.addAll(_logs);
        } else {
            _logs.add(0, logMsg + " (NOT main thread)\n");

            // You can only do below stuff on main thread.
            new Handler(Looper.getMainLooper()).post(() -> {
                _logAdapter.clear();
                _logAdapter.addAll(_logs);
            });
        }
    }

    private boolean _isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!_disposable.isDisposed()){
            _disposable.dispose();
        }
    }

    public class LogAdapter extends ArrayAdapter<String>{

        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log,R.id.item_log,logs);
        }
    }
}
