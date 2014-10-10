package org.cabi.ofra.dataload;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * (c) 2014, Eduardo Quirós-Campos
 */
public interface ISheetProcessor {
  public SheetValidationData validateSheet(Sheet sheet);
}
