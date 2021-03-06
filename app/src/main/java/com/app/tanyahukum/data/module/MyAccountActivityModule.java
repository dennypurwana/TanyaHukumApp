package com.app.tanyahukum.data.module;

import android.content.Context;

import com.app.tanyahukum.util.CustomScope;
import com.app.tanyahukum.view.MyAccountActivityInterface;
import com.app.tanyahukum.view.RegistrationActivityInterface;

import dagger.Module;
import dagger.Provides;

/**
 * Created by emerio on 4/8/17.
 */

@Module
public class MyAccountActivityModule {
    private final MyAccountActivityInterface.View mView;
    private final Context mContext;
    public MyAccountActivityModule(MyAccountActivityInterface.View view, Context context){
        this.mView=view;
        this.mContext=context;
    }

    @Provides
    @CustomScope
    MyAccountActivityInterface.View providesMyAccountActivityInterfaceView(){
        return mView;
    }
    @Provides
    @CustomScope
    Context providesMyAccountActivityContext(){
        return mContext;
    }

}
