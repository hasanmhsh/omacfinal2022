package hasan.mohamed.shehata;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import hasan.mohamed.shehata.myapplication.types.Callable;

public class InternetAvailabilityChecker extends BroadcastReceiver {

    private AtomicBoolean isInternetAvailable = new AtomicBoolean(false);
    private ArrayList<Callable> seekers = new ArrayList<>();

    public InternetAvailabilityChecker() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent==null||intent.getExtras()==null){
            return;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            isInternetAvailable.set(true);
        }
        else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            isInternetAvailable.set(false);
        }
        broadcastInternetAvailability();
    }

    public void registerSeeker(Callable seeker){
        seekers.add(seeker);
    }

    public void unregisterSeeker(Callable seeker){
        seekers.remove(seeker);
    }

    private void broadcastInternetAvailability(){
        seekers.forEach(new Consumer<Callable>() {
            @Override
            public void accept(Callable callable) {
                if(callable!=null)
                    callable.call(isInternetAvailable.get());
            }
        });

    }
}
