package com.nfcpayment;

import androidx.annotation.NonNull;

import android.nfc.tech.IsoDep;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

import com.github.devnied.emvnfccard.enums.EmvCardScheme;
import com.github.devnied.emvnfccard.model.Application;
import com.github.devnied.emvnfccard.model.EmvCard;
import com.github.devnied.emvnfccard.parser.EmvTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ReactModule(name = NfcPaymentModule.NAME)
public class NfcPaymentModule extends ReactContextBaseJavaModule implements LifecycleEventListener, NfcAdapter.ReaderCallback {

  public static final String NAME = "NfcPayment";
  private final ReactApplicationContext reactContext;
  private NfcAdapter mNfcAdapter;
  private Promise promise;
  private Boolean isResume;
  private Config config = new Config();

  private Boolean isComboCard = false;

  public NfcPaymentModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addLifecycleEventListener(this);
    mNfcAdapter = NfcAdapter.getDefaultAdapter(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private void setConfigFromOptions(ReadableMap options) {
    if (options.hasKey("contactLess")) {
      config.setContactLess(options.getBoolean("contactLess"));
    }
    if (options.hasKey("readAllAids")) {
      config.setReadAllAids(options.getBoolean("readAllAids"));
    }
    if (options.hasKey("readTransactions")) {
      config.setReadTransactions(options.getBoolean("readTransactions"));
    }
    if (options.hasKey("removeDefaultParsers")) {
      config.setRemoveDefaultParsers(options.getBoolean("removeDefaultParsers"));
    }
    if (options.hasKey("readAt")) {
      config.setReadAt(options.getBoolean("readAt"));
    }
  }

  @ReactMethod
  private void registerTagEvent(ReadableMap options, Promise promise) {
    if (mNfcAdapter != null){
      Bundle bundle = new Bundle();
      bundle.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);
      this.isResume = true;
      this.promise = promise;
      setConfigFromOptions(options);
      mNfcAdapter.enableReaderMode(reactContext.getCurrentActivity(),
        this,
        NfcAdapter.FLAG_READER_NFC_A |
          NfcAdapter.FLAG_READER_NFC_B |
          NfcAdapter.FLAG_READER_NFC_F |
          NfcAdapter.FLAG_READER_NFC_V |
          NfcAdapter.FLAG_READER_NFC_BARCODE |
          NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
        bundle);
    }
  }

  public void responseCallback(String responseObject, IsoDep isoDep ) {

    if (isoDep != null) {
      Vibrator vibrator = (Vibrator) reactContext.getCurrentActivity().getSystemService(reactContext.getCurrentActivity().VIBRATOR_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (vibrator != null) {
          vibrator.vibrate(VibrationEffect.createOneShot(350, 250));
        }
      } else {
        if (vibrator != null) {
          vibrator.vibrate(350);
        }
      }
    }

    this.promise.resolve(responseObject);
    this.isResume = false;

    try {
      isoDep.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private CreditCard processNfc(IsoDep isoDep) {
    try{
      NfcPaymentProvider provider = createNfcPaymentProvider(isoDep);
      EmvTemplate parser = createEmvTemplate(provider);
      EmvCard card = parser.readEmvCard();
      CreditCard creditCard = extractCreditCardInfo(card);
      return creditCard;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void onTagDiscovered(Tag tag) {
    IsoDep isoDep = null;
    Log.d("NFCPaymentModule", "onTagDiscovered");
    if(this.isResume){
      try {
        isoDep = IsoDep.get(tag);
        isoDep.connect();
        CreditCard creditCard = this.processNfc(isoDep);
        List<CreditCard> creditCardList = new ArrayList<>();

        if(this.isComboCard){
          CreditCard secondCard = this.processNfc(isoDep);
          creditCardList.add(secondCard);
          this.isComboCard = false;
        }

        creditCardList.add(creditCard);
        String responseObject = creditCardList.toString();
        responseCallback(responseObject, isoDep);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onHostPause() {
    Log.d("NFCPaymentModule", "onHostPause");
     if (mNfcAdapter != null){
        mNfcAdapter.disableReaderMode(reactContext.getCurrentActivity());
     }
  }

  @Override
  public void onHostDestroy() {
    Log.d("NFCPaymentModule", "onHostDestroy");
    if (mNfcAdapter != null){
      mNfcAdapter.disableReaderMode(reactContext.getCurrentActivity());
    }
  }

  @Override
  public void onHostResume() {}


  @ReactMethod
  private void unregisterTagEvent(Promise promise) {
    if (mNfcAdapter != null){
      Log.d("NFCPaymentModule", "unregisterTagEvent");
      mNfcAdapter.disableReaderMode(this.reactContext.getCurrentActivity());
    }
    promise.resolve(true);
  }

  private LocalDate extractExpireDate(Date expireDate) {
      if (expireDate != null) {
        return expireDate.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
      } else {
        return LocalDate.of(1999, 12, 31);
      }
  }

  private CreditCard extractCreditCardInfo(EmvCard card) {
    String cardNumber = card.getCardNumber();
    LocalDate expireDate = extractExpireDate(card.getExpireDate());
    EmvCardScheme cardGetType = card.getType();
    CreditCard creditCard = new CreditCard();
    creditCard.setCardNumber(prettyPrintCardNumber(cardNumber));
    creditCard.setExpireDate(expireDate.toString());

    if (cardGetType != null) {
      String typeName = card.getType().getName();
      String[] typeAids = card.getType().getAid();
      creditCard.setCardType(typeName);
      creditCard.setTypeAids(typeAids);
    }

    List<Application> applications = card.getApplications();
    this.isComboCard = applications.size() == 2;
    creditCard.setApplications(applications);

    return creditCard;
  }

  private EmvTemplate createEmvTemplate(NfcPaymentProvider provider) {
    EmvTemplate.Config emvConfig = EmvTemplate.Config()
      .setContactLess(config.isContactLess())
      .setReadAllAids(!this.isComboCard)
      .setReadTransactions(config.isReadTransactions())
      .setRemoveDefaultParsers(config.isRemoveDefaultParsers())
      .setReadAt(config.isReadAt());

    return EmvTemplate.Builder()
      .setProvider(provider)
      .setConfig(emvConfig)
      .build();
  }

  private NfcPaymentProvider createNfcPaymentProvider(IsoDep isoDep) {
    NfcPaymentProvider provider = new NfcPaymentProvider();
    provider.setmTagCom(isoDep);
    return provider;
  }

  public static String prettyPrintCardNumber(String cardNumber) {
      if (cardNumber == null) return null;
      char delimiter = ' ';
      return cardNumber.replaceAll(".{4}(?!$)", "$0" + delimiter);
  }
}
