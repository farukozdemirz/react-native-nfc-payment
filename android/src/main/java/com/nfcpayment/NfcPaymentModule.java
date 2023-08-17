package com.nfcpayment;

import android.nfc.NfcAdapter;
import android.util.Log;
import androidx.annotation.NonNull;
import android.os.Bundle;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.LifecycleEventListener;

@ReactModule(name = NfcPaymentModule.NAME)
public class NfcPaymentModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
  public static final String NAME = "NfcPayment";

  private final ReactApplicationContext reactContext;
  private NfcAdapter mNfcAdapter;
  private Boolean isResumed = false;

  public NfcPaymentModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addLifecycleEventListener(this);
    mNfcAdapter = NfcAdapter.getDefaultAdapter(reactContext);
    reactContext.addLifecycleEventListener(this);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @Override
  public void onHostResume() {
    if (mNfcAdapter != null) {
      Bundle options = new Bundle();
      // Work around for some broken Nfc firmware implementations that poll the card
      // too fast
      options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

      // Enable ReaderMode for all types of card and disable platform sounds
      // the option NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK is NOT set
      // to get the data of the tag after reading
      mNfcAdapter.enableReaderMode(reactContext.getCurrentActivity(),
          this,
          NfcAdapter.FLAG_READER_NFC_A |
              NfcAdapter.FLAG_READER_NFC_B |
              NfcAdapter.FLAG_READER_NFC_F |
              NfcAdapter.FLAG_READER_NFC_V |
              NfcAdapter.FLAG_READER_NFC_BARCODE |
              NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
          options);
    }
  }

  @Override
  public void onHostPause() {
    Log.d("NFCACTIONTEST", "onHostPause");
  }

  @Override
  public void onHostDestroy() {
    Log.d("NFCACTIONTEST", "onHostDestroy");
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void test(String a, Promise promise) {
    promise.resolve(a);
  }
}
