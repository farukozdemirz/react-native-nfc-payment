package com.nfcpayment;

public class Config {
  private boolean contactLess = true;
  private boolean readAllAids = true;
  private boolean readTransactions = true;
  private boolean removeDefaultParsers = false;
  private boolean readAt = true;

  public boolean isContactLess() {
    return contactLess;
  }

  public void setContactLess(boolean contactLess) {
    this.contactLess = contactLess;
  }

  public boolean isReadAllAids() {
    return readAllAids;
  }

  public void setReadAllAids(boolean readAllAids) {
    this.readAllAids = readAllAids;
  }

  public boolean isReadTransactions() {
    return readTransactions;
  }

  public void setReadTransactions(boolean readTransactions) {
    this.readTransactions = readTransactions;
  }

  public boolean isRemoveDefaultParsers() {
    return removeDefaultParsers;
  }

  public void setRemoveDefaultParsers(boolean removeDefaultParsers) {
    this.removeDefaultParsers = removeDefaultParsers;
  }

  public boolean isReadAt() {
    return readAt;
  }

  public void setReadAt(boolean readAt) {
    this.readAt = readAt;
  }

  @Override
  public String toString() {
    return "Config{" +
      "contactLess=" + contactLess +
      ", readAllAids=" + readAllAids +
      ", readTransactions=" + readTransactions +
      ", removeDefaultParsers=" + removeDefaultParsers +
      ", readAt=" + readAt +
      '}';
  }
}
