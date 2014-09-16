package com.arkitechtura.odata.sample;

import org.apache.olingo.odata2.api.exception.ODataNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Created by equiros on 8/25/2014.
 */
public interface ISampleDataStore {
  public Map<String, Object> getCar(long id) throws ODataNotFoundException;
  public Map<String, Object> getManufacturer(long id) throws ODataNotFoundException;
  public List<Map<String,Object>> getCars() throws ODataNotFoundException;
  public List<Map<String,Object>> getManufacturers() throws ODataNotFoundException;
}
