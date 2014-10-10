package org.cabi.ofra.dataload;

/**
 * (c) 2014, Eduardo Quirós-Campos
 */
public class SheetValidationData {
  private enum VALIDATION_STATUS {SUCCESS, ERROR}

  private VALIDATION_STATUS status;
  private String message;

  public SheetValidationData(VALIDATION_STATUS status, String message) {
    this.status = status;
    this.message = message;
  }

  public VALIDATION_STATUS getStatus() {
    return status;
  }

  public void setStatus(VALIDATION_STATUS status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
