package com.nfcpayment;

import com.github.devnied.emvnfccard.model.Application;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class CreditCard {
  public String cardNumber;
  public String expireDate;
  public String cardType;
  public String[] typeAids;
  public List<Application> applications;

  public String getCardNumber() {
    return cardNumber;
  }

  public String getExpireDate() {
    return expireDate;
  }

  public String getCardType() {
    return cardType;
  }

  public String[] getTypeAids() {
    return typeAids;
  }

  public List<Application> getApplications() {
    return applications;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public void setExpireDate(String expireDate) {
    this.expireDate = expireDate;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public void setTypeAids(String[] typeAids) {
    this.typeAids = typeAids;
  }

  public void setApplications(List<Application> applications) {
    this.applications = applications;
  }

  public static String convertToJson(Object data) {
    Gson gson = new Gson();
    return gson.toJson(data);
  }

  @Override
  public String toString() {
    return "{" +
      "\"cardNumber\":\"" + cardNumber + "\"," +
      "\"expireDate\":\"" + expireDate + "\"," +
      "\"cardType\":\"" + cardType + "\"," +
      "\"typeAids\":" + convertToJson(typeAids) + "," +
      "\"applications\":" +convertToJson(applications)+"}";
  }

}
