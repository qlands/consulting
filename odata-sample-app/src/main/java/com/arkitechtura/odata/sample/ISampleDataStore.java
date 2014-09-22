package com.arkitechtura.odata.sample;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Created by equiros on 8/25/2014.
 */
public interface ISampleDataStore {
  public Map<String, Object> getCar(long id) throws ODataException;
  public Map<String, Object> getManufacturer(long id) throws ODataException;
  public List<Map<String,Object>> getCars() throws ODataException;
  public List<Map<String,Object>> getManufacturers() throws ODataException;
  public Map<String, Object> getManufacturerForCar(long id) throws ODataException;
  public Long storeCar(Map<String, Object> properties) throws ODataException;
  public Long storeManufacturer(Map<String, Object> properties) throws ODataException;
  public boolean carExists(long id) throws ODataException;
  public boolean manufacturerExists(long id) throws ODataException;
  public void updateCar(long id, Map<String, Object> properties) throws ODataException;
  public void updateManufacturer(long id, Map<String, Object> properties) throws ODataException;
  public void deleteCar(long id) throws  ODataException;
  public void deleteManufacturer(long id) throws ODataException;
}
