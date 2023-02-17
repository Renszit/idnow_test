package com.idnowtest.idnowtestandroid;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import de.idnow.sdk.IDnowSDK;
import com.facebook.react.bridge.Callback;

import java.util.Map;
import java.util.HashMap;

public class IDNowModule extends ReactContextBaseJavaModule implements ActivityEventListener {
  private static ReactApplicationContext reactContext;
  private Callback successCallback;
  String TAG = "IDNOW_ANDROID";

  IDNowModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
    reactContext.addActivityEventListener(this);
  }
  @Override
  public void onNewIntent(Intent  intent) {
    Log.v(TAG, "onNewIntent.");
  }

  @Override
  public String getName() {
    return "IDNow";
  }


  @ReactMethod
  public void show(String identId, String company,Callback errorCallback,
      Callback success) {
    successCallback = success;
    try {
      IDnowSDK.getInstance().initialize(getReactApplicationContext().getCurrentActivity(), company);
      IDnowSDK.setShowErrorSuccessScreen(false,  getReactApplicationContext());
      IDnowSDK.setShowVideoOverviewCheck(true, getReactApplicationContext());
      IDnowSDK.setTransactionToken(identId);
      IDnowSDK.setApiHost("https://api.idnow.de", getReactApplicationContext());
      Log.v(TAG, IDnowSDK.getTransactionToken());
      // To actually start the identification process, pass the transactionToken.
      IDnowSDK.getInstance().start(IDnowSDK.getTransactionToken());
    } catch (Exception e) {
      System.out.println("ERROR:   "+e);
      errorCallback.invoke(e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    System.out.println("requestCode:    "+requestCode);

    if (requestCode == IDnowSDK.REQUEST_ID_NOW_SDK) {
      if (resultCode == IDnowSDK.RESULT_CODE_SUCCESS) {
        if (data != null) {
          String transactionToken = data.getStringExtra(IDnowSDK.RESULT_DATA_TRANSACTION_TOKEN);
          Log.v(TAG, "success, transaction token: " + transactionToken);
        }
            if (successCallback != null){
              successCallback.invoke("success");
           }
      } else if (resultCode == IDnowSDK.RESULT_CODE_CANCEL) {
        if (data != null) {
          String transactionToken = data.getStringExtra(IDnowSDK.RESULT_DATA_TRANSACTION_TOKEN);
          String errorMessage = data.getStringExtra(IDnowSDK.RESULT_DATA_ERROR);
          Log.v(TAG, "canceled, transaction token: " + transactionToken + errorMessage);
        }
            if (successCallback != null){
              successCallback.invoke("canceled");
           }
      } else if (resultCode == IDnowSDK.RESULT_CODE_FAILED) {
        if (data != null) {
          String transactionToken = data.getStringExtra(IDnowSDK.RESULT_DATA_TRANSACTION_TOKEN);
          String errorMessage = data.getStringExtra(IDnowSDK.RESULT_DATA_ERROR);
          Log.v(TAG, "failed, transaction token: " + transactionToken + errorMessage);
        }
           if (successCallback != null){
              successCallback.invoke("failed");
           }
      } else {
        Log.v(TAG, "Result Code: " + resultCode);
           if (successCallback != null){
              successCallback.invoke("Resultcode: "+ resultCode);
           }
      }
    }
  }
}
