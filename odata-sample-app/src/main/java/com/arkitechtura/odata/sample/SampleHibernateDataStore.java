package com.arkitechtura.odata.sample;

import com.arkitechtura.odata.sample.domain.Address;
import com.arkitechtura.odata.sample.domain.Car;
import com.arkitechtura.odata.sample.domain.Manufacturer;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by equiros on 8/25/2014.
 */
public class SampleHibernateDataStore implements ISampleDataStore {
  private Car getCarEntity(long id, Session session) {
    return (Car) session.get(Car.class, id);
  }

  private Manufacturer getManufacturerEntity(long id, Session session) {
    return (Manufacturer) session.get(Manufacturer.class, id);
  }

  @Override
  public Map<String, Object> getCar(long id) throws ODataException{
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Car car = getCarEntity(id, session);
      if (car != null) {
        return getCarMap(car);
      }
      else {
        throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      }
    }
    finally {
      tx.commit();
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
  public Map<String, Object> getManufacturer(long id) throws ODataException{
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Manufacturer manu = getManufacturerEntity(id, session);
      if (manu != null) {
        return getManufacturerMap(manu);
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
  public List<Map<String, Object>> getCars() throws ODataException {
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
  public List<Map<String, Object>> getManufacturers() throws ODataException {
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
  public Map<String, Object> getManufacturerForCar(long id) throws ODataException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Car car = getCarEntity(id, session);
      if (car != null) {
        Manufacturer manu = car.getManufacturer();
        if (manu != null) {
          return getManufacturerMap(manu);
        }
      }
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public Long storeCar(Map<String, Object> properties) throws ODataException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Car car = createCar(properties, session);
      session.save(car);
      return car.getId();
    }
    finally {
      tx.commit();
    }
  }

  private Car createCar(Map<String, Object> properties, Session session) {
    Car car = new Car();
    car.setModel((String) properties.get("Model"));
    int manufacturerId = (Integer) properties.get("ManufacturerId");
    Manufacturer manu = (Manufacturer) session.load(Manufacturer.class, Long.valueOf(manufacturerId));
    car.setManufacturer(manu);
    car.setPrice(((BigDecimal) properties.get("Price")).floatValue());
    car.setModelYear((String) properties.get("ModelYear"));
    car.setCurrency((String) properties.get("Currency"));
    car.setImagePath((String) properties.get("ImagePath"));
    car.setUpdated(new Date(System.currentTimeMillis()));
    return car;
  }

  @Override
  public Long storeManufacturer(Map<String, Object> properties) throws ODataException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Manufacturer manu = createManufacturer(properties);
      session.save(manu);
      return manu.getId();
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public boolean carExists(long id) throws ODataException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Car car = getCarEntity(id, session);
      return car != null;
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public boolean manufacturerExists(long id) throws ODataException {
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      Manufacturer manu = getManufacturerEntity(id, session);
      return manu != null;
    }
    finally {
      tx.commit();
    }
  }

  private Manufacturer createManufacturer(Map<String, Object> properties) {
    Manufacturer manu = new Manufacturer();
    manu.setName((String) properties.get("Name"));
    manu.setUpdated(new Date(System.currentTimeMillis()));
    manu.setAddress(extractAddress((Map<String, Object>) properties.get("Address")));
    return manu;
  }

  private Address extractAddress(Map<String, Object> properties) {
    Address addr = new Address();
    addr.setCity((String) properties.get("City"));
    addr.setStreet((String) properties.get("Street"));
    addr.setZipCode((String) properties.get("ZipCode"));
    addr.setCountry((String) properties.get("Country"));
    return addr;
  }

  interface ValueSetter {
    void setValue(Object value);
  }

  private void setProperty(Map<String, Object> properties, String propertyName, ValueSetter setter) {
    if (properties.containsKey(propertyName)) {
      Object val = properties.get(propertyName);
      setter.setValue(val);
    }
  }

  @Override
  public void updateCar(long id, Map<String, Object> properties) throws ODataException{
    final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      final Car car = getCarEntity(id, session);
      if (car == null) throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      setProperty(properties, "Model", (value) -> {car.setModel((String) value);});
      setProperty(properties, "ModelYear", (value) -> {car.setModelYear((String) value);});
      setProperty(properties, "Price", (value) -> {car.setPrice(((BigDecimal) value).floatValue());});
      setProperty(properties, "Currency", (value) -> {car.setCurrency((String) value);});
      setProperty(properties, "ImagePath", (value) -> {car.setImagePath((String) value);});
      setProperty(properties, "ManufacturerId", (value) -> {
        int manufacturerId = (Integer) value;
        Manufacturer manu = (Manufacturer) session.load(Manufacturer.class, Long.valueOf(manufacturerId));
        car.setManufacturer(manu);
      });
      car.setUpdated(new Date(System.currentTimeMillis()));
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public void updateManufacturer(long id, Map<String, Object> properties) throws ODataException{
    final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      final Manufacturer manufacturer = getManufacturerEntity(id, session);
      if (manufacturer == null) throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      setProperty(properties, "Name", (value) -> {manufacturer.setName((String) value);});
      if (properties.containsKey("Address")) {
        Map<String, Object> addr = (Map<String, Object>) properties.get("Address");
        setProperty(addr, "City", (value) -> {manufacturer.getAddress().setCity((String) value);});
        setProperty(addr, "Street", (value) -> {manufacturer.getAddress().setStreet((String) value);});
        setProperty(addr, "ZipCode", (value) -> {manufacturer.getAddress().setZipCode((String) value);});
        setProperty(addr, "Country", (value) -> {manufacturer.getAddress().setCountry((String) value);});
      }
      manufacturer.setUpdated(new Date(System.currentTimeMillis()));
    }
    finally {
      tx.commit();
    }
  }

  @Override
  public void deleteCar(long id) throws ODataException {
    final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      final Car car = getCarEntity(id, session);
      if (car == null) throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      session.delete(car);
    }
    finally {
      tx.commit();;
    }
  }

  @Override
  public void deleteManufacturer(long id) throws ODataException {
    final Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    Transaction tx = session.beginTransaction();
    try {
      final Manufacturer manufacturer = getManufacturerEntity(id, session);
      if (manufacturer == null) throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      session.delete(manufacturer);
    }
    finally {
      tx.commit();
    }
  }
}
