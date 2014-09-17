package com.arkitechtura.odata.sample;

import com.arkitechtura.odata.sample.domain.Address;
import com.arkitechtura.odata.sample.domain.Car;
import com.arkitechtura.odata.sample.domain.Manufacturer;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by equiros on 8/25/2014.
 */
public class SampleHibernateDataStore implements ISampleDataStore {

  @Override
  public Map<String, Object> getCar(long id) throws ODataNotFoundException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Car car = (Car) session.get(Car.class, id);
      if (car != null) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("Id", car.getId());
        data.put("Model", car.getModel());
        data.put("ManufacturerId", car.getManufacturer().getId());
        data.put("Price", car.getPrice());
        data.put("Currency", car.getCurrency());
        data.put("ModelYear", car.getModelYear());
        data.put("Updated", car.getUpdated());
        data.put("ImagePath", car.getImagePath());
        return data;
      }
      else {
        throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      }
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public Map<String, Object> getManufacturer(long id) throws ODataNotFoundException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Manufacturer manu = (Manufacturer) session.get(Manufacturer.class, id);
      if (manu != null) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("Id", manu.getId());
        data.put("Name", manu.getName());
        data.put("Address", convertAddress(manu.getAddress()));
        data.put("Updated", manu.getUpdated());
        return data;
      }
      else {
        throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      }
    }
    finally {
      tx.commit();
    }
  }

  private Map<String, Object> convertAddress(Address address) {
    Map<String, Object> addr = new HashMap<>();
    addr.put("Street", address.getStreet());
    addr.put("City", address.getCity());
    addr.put("ZipCode", address.getZipCode());
    addr.put("Country", address.getCountry());
    return addr;
  }

  @Override
  public List<Map<String, Object>> getCars() throws ODataNotFoundException {
    return null;
  }

  @Override
  public List<Map<String, Object>> getManufacturers() throws ODataNotFoundException {
    return null;
  }
}
