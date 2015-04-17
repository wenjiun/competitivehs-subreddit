package my.wenjiun.subreddit.competitivehs.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyAuthenticatorService extends Service {

    private MyAuthenticator myAuthenticator;

    @Override
    public void onCreate() {
        myAuthenticator = new MyAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myAuthenticator.getIBinder();
    }
}
