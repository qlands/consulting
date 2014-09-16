package com.arkitechtura.odata.sample.domain;

import javax.persistence.Entity;
import java.util.Date;
import java.util.Set;

/**
 * Created by equiros on 8/25/2014.
 */
@Entity
public class Manufacturer {
  private Long id;
  private String name;
  private Address address;
  private Date updated;
  private Set<Car> cars;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public Set<Car> getCars() {
    return cars;
  }

  public void setCars(Set<Car> cars) {
    this.cars = cars;
  }
}
