package my.wenjiun.subreddit.competitivehs.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MySyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MySyncAdapter sMySyncAdapter = null;


    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if(sMySyncAdapter == null) {
                sMySyncAdapter = new MySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMySyncAdapter.getSyncAdapterBinder();
    }

}
