package com.arkitechtura.odata.sample;

import com.arkitechtura.odata.sample.domain.Address;
import com.arkitechtura.odata.sample.domain.Car;
import com.arkitechtura.odata.sample.domain.Manufacturer;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by equiros on 8/25/2014.
 */
public class SampleHibernateDataStore implements ISampleDataStore {
  private Car getCarEntity(long id) {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      return (Car) session.get(Car.class, id);
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public Map<String, Object> getCar(long id) throws ODataNotFoundException {
    Car car = getCarEntity(id);
    if (car != null) {
      return getCarMap(car);
    }
    else {
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    }
  }

  private Map<String, Object> getCarMap(Car car) {
    Map<String, Object> data = new HashMap<>();
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

  @Override
  public Map<String, Object> getManufacturer(long id) throws ODataNotFoundException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Manufacturer manu = (Manufacturer) session.get(Manufacturer.class, id);
      if (manu != null) {
        Map<String, Object> data = getManufacturerMap(manu);
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

  private Map<String, Object> getManufacturerMap(Manufacturer manu) {
    Map<String, Object> data = new HashMap<>();
    data.put("Id", manu.getId());
    data.put("Name", manu.getName());
    data.put("Address", convertAddress(manu.getAddress()));
    data.put("Updated", manu.getUpdated());
    return data;
  }

  private Map<String, Object> convertAddress(Address address) {
    if (address == null) return null;
    Map<String, Object> addr = new HashMap<>();
    addr.put("Street", address.getStreet());
    addr.put("City", address.getCity());
    addr.put("ZipCode", address.getZipCode());
    addr.put("Country", address.getCountry());
    return addr;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getCars() throws ODataNotFoundException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      List<Car> cars = session.createQuery("from Car").list();
      List<Map<String, Object>> result = new ArrayList<>();
      for (Car car : cars) {
        result.add(getCarMap(car));
      }
      return result;
    }
    finally {
      tx.commit();
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> getManufacturers() throws ODataNotFoundException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      List<Manufacturer> manufacturers = session.createQuery("from Manufacturer").list();
      List<Map<String, Object>> result = new ArrayList<>();
      for (Manufacturer manu : manufacturers) {
        result.add(getManufacturerMap(manu));
      }
      return result;
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public Map<String, Object> getManufacturerForCar(long id) throws ODataNotFoundException {
    Car car = getCarEntity(id);
    if (car != null) {
      Manufacturer manu = car.getManufacturer();
      if (manu != null) {
        return getManufacturerMap(manu);
      }
    }
    throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
  }
}
