package com.apoim.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.apoim.groupchatwebrtc.models.QbConfigs;
import com.apoim.groupchatwebrtc.util.QBResRequestExecutor;
import com.apoim.groupchatwebrtc.utils.ConfigUtils;
import com.apoim.helper.Constant;
import com.apoim.modal.FilterInfo;
import com.apoim.session.Session;
import com.apoim.util.Utils;
import com.crashlytics.android.Crashlytics;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.ServiceZone;
import com.squareup.picasso.Picasso;
import io.fabric.sdk.android.Fabric;

/**
 * Created by mindiii on 16/2/18.
 */

public class Apoim extends Application implements LifeCycleDelegateListner  {

    private static Apoim instance;
    private QBResRequestExecutor qbResRequestExecutor;

    public static final String TAG = Apoim.class.getSimpleName();

    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config_template.json";
    private QbConfigs qbConfigs;


    private RequestQueue mRequestQueue;
    FilterInfo filterInfo;


    public static Apoim getInstance() {
        return instance;
    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

    private void initApplication(){
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
        initQBSessionManager();
        initQbConfigs();
        initCredentials();
        initApplication();

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        Session session = new Session(getApplicationContext());
        if(session.getFilterInfo() != null){
            session.setFilterSession(new FilterInfo());
        }

        AppLifeCycle lifecycleHandler = new AppLifeCycle(this);
        registerLifecycle(lifecycleHandler);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }

    public void cancelTask(){
        mRequestQueue.cancelAll(TAG);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void registerLifecycle (AppLifeCycle lifecycleHandler){
        registerActivityLifecycleCallbacks(lifecycleHandler);
        registerComponentCallbacks(lifecycleHandler);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onAppBackgrounded() {
        Utils.goToOnlineStatus(instance, Constant.offline);
    }

    @Override
    public void onAppForegrounded() {
        Utils.goToOnlineStatus(instance, Constant.online);
    }


    /*.............videocalling...........................*/

    private void initQbConfigs() {
        Log.e(TAG, "QB CONFIG FILE NAME: " + getQbConfigFileName());
        qbConfigs = ConfigUtils.getCoreConfigsOrNull(getQbConfigFileName());
    }


    public void initCredentials(){
        if (qbConfigs != null) {
            QBSettings.getInstance().init(getApplicationContext(), qbConfigs.getAppId(), qbConfigs.getAuthKey(), qbConfigs.getAuthSecret());
            QBSettings.getInstance().setAccountKey(qbConfigs.getAccountKey());

            if (!TextUtils.isEmpty(qbConfigs.getApiDomain()) && !TextUtils.isEmpty(qbConfigs.getChatDomain())) {
                QBSettings.getInstance().setEndpoints(qbConfigs.getApiDomain(), qbConfigs.getChatDomain(), ServiceZone.PRODUCTION);
                QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);
            }
        }
    }

    public QbConfigs getQbConfigs(){
        return qbConfigs;
    }

    protected String getQbConfigFileName(){
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }

    private void initQBSessionManager() {
        QBSessionManager.getInstance().addListener(new QBSessionManager.QBSessionListener() {
            @Override
            public void onSessionCreated(QBSession qbSession) {
                Log.d(TAG, "Session Created");
            }

            @Override
            public void onSessionUpdated(QBSessionParameters qbSessionParameters) {
                Log.d(TAG, "Session Updated");
            }

            @Override
            public void onSessionDeleted() {
                Log.d(TAG, "Session Deleted");
            }

            @Override
            public void onSessionRestored(QBSession qbSession) {
                Log.d(TAG, "Session Restored");
            }

            @Override
            public void onSessionExpired() {
                Log.d(TAG, "Session Expired");
            }

            @Override
            public void onProviderSessionExpired(String provider) {
                Log.d(TAG, "Session Expired for provider:" + provider);
            }
        });
    }

}
