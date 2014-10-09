package com.arkitechtura.odata.sample;

import com.arkitechtura.odata.sample.audit.AuditHelper;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.info.*;

import java.io.InputStream;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.arkitechtura.odata.sample.SampleEdmProvider.*;

/**
 * Created by equiros on 8/18/2014.
 */
public class SampleSingleProcessor extends ODataSingleProcessor {
  private ISampleDataStore dataStore;

  public SampleSingleProcessor() {
    dataStore = new SampleHibernateDataStore();
  }

  private int getKeyValue(KeyPredicate key) throws ODataException {
    EdmProperty property = key.getProperty();
    EdmSimpleType type = (EdmSimpleType) property.getType();
    return type.valueOfString(key.getLiteral(), EdmLiteralKind.DEFAULT, property.getFacets(), Integer.class);
  }

  @Override
  public ODataResponse readEntity(GetEntityUriInfo uriInfo, String contentType) throws ODataException {
    Principal p = SecurityUtils.getPrincipal();
    if (uriInfo.getNavigationSegments().size() == 0) {
      EdmEntitySet entitySet = uriInfo.getStartEntitySet();

      if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
        if (!SecurityUtils.canRead(p, "cars")) {
          throw new ODataException("Authorization error. User '" + p.getName() + "' does not have READ permission on 'Cars' entity");
        }
        int id = getKeyValue(uriInfo.getKeyPredicates().get(0));
        Map<String, Object> data = dataStore.getCar(id);

        if (data != null) {
          URI serviceRoot = getContext().getPathInfo().getServiceRoot();
          EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder propertiesBuilder = EntityProviderWriteProperties.serviceRoot(serviceRoot);
          AuditHelper.trace(p, "cars", "r");
          return EntityProvider.writeEntry(contentType, entitySet, data, propertiesBuilder.build());
        }
      }
      else if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
        if (!SecurityUtils.canRead(p, "manufacturers")) {
          throw new ODataException("Authorization error. User '" + p.getName() + "' does not have READ permission on 'Manufacturers' entity");
        }
        int id = getKeyValue(uriInfo.getKeyPredicates().get(0));
        Map<String, Object> data = null;
        data = dataStore.getManufacturer(id);
        if (data != null) {
          URI serviceRoot = getContext().getPathInfo().getServiceRoot();
          EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder propertiesBuilder = EntityProviderWriteProperties.serviceRoot(serviceRoot);
          AuditHelper.trace(p, "manufacturers", "r");
          return EntityProvider.writeEntry(contentType, entitySet, data, propertiesBuilder.build());
        }
      }

      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);

    }
    else if (uriInfo.getNavigationSegments().size() == 1) {
      //navigation first level, simplified example for illustration purposes only
      EdmEntitySet entitySet = uriInfo.getTargetEntitySet();
      if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
        int carKey = getKeyValue(uriInfo.getKeyPredicates().get(0));
        AuditHelper.trace(p, "manufacturers", "r");
        return EntityProvider.writeEntry(contentType, uriInfo.getTargetEntitySet(), dataStore.getManufacturerForCar(carKey), EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
      }
      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    }
    throw new ODataNotImplementedException();
  }

  @Override
  public ODataResponse createEntity(PostUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
    //No support for creating and linking a new entry
    if (uriInfo.getNavigationSegments().size() > 0) {
      throw new ODataNotImplementedException();
    }
    Principal p = SecurityUtils.getPrincipal();
    //No support for media resources
    if (uriInfo.getStartEntitySet().getEntityType().hasStream()) {
      throw new ODataNotImplementedException();
    }
    EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(false).build();
    ODataEntry entry = null;
    entry = EntityProvider.readEntry(requestContentType, uriInfo.getStartEntitySet(), content, properties);
    //if something goes wrong in deserialization this is managed via the ExceptionMapper
    //no need for an application to do exception handling here an convert the exceptions in HTTP exceptions

    Map<String, Object> data = entry.getProperties();
    if (ENTITY_NAME_CAR.equals(uriInfo.getStartEntitySet().getEntityType().getName())) {
      if (!SecurityUtils.canCreate(p, "cars")) {
        throw new ODataException("Authorization error. User '" + p.getName() + "' does not have CREATE permission on 'Cars' entity");
      }
      long carId = dataStore.storeCar(data);
      data.put("Id", carId);
      AuditHelper.trace(p, "cars", "c");
    }
    else if (ENTITY_NAME_MANUFACTURER.equals(uriInfo.getStartEntitySet().getEntityType().getName())) {
      if (!SecurityUtils.canCreate(p, "manufacturers")) {
        throw new ODataException("Authorization error. User '" + p.getName() + "' does not have CREATE permission on 'Manufacturers' entity");
      }
      long manuId = dataStore.storeManufacturer(data);
      data.put("Id", manuId);
      AuditHelper.trace(p, "manufacturers", "c");
    }
    //serialize the entry, Location header is set by OData Library
    return EntityProvider.writeEntry(contentType, uriInfo.getStartEntitySet(), data, EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
  }

  @Override
  public ODataResponse readEntitySet(GetEntitySetUriInfo uriInfo, String contentType) throws ODataException {
    EdmEntitySet entitySet;

    Principal p = SecurityUtils.getPrincipal();
    if (uriInfo.getNavigationSegments().size() == 0) {
      entitySet = uriInfo.getStartEntitySet();

      if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
        if (!SecurityUtils.canRead(p, "cars")) {
          throw new ODataException("Authorization error. User '" + p.getName() + "' does not have READ permission on 'Cars' entity");
        }
        AuditHelper.trace(p, "cars", "r");
        return EntityProvider.writeFeed(contentType, entitySet, dataStore.getCars(), EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
      }
      else if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
        if (!SecurityUtils.canRead(p, "manufacturers")) {
          throw new ODataException("Authorization error. User '" + p.getName() + "' does not have READ permission on 'Manufacturers' entity");
        }
        AuditHelper.trace(p, "manufacturers", "r");
        return EntityProvider.writeFeed(contentType, entitySet, dataStore.getManufacturers(), EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
      }

      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);

    }
    else if (uriInfo.getNavigationSegments().size() == 1) {
      //navigation first level, simplified example for illustration purposes only
      entitySet = uriInfo.getTargetEntitySet();

      if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
        if (!SecurityUtils.canRead(p, "cars")) {
          throw new ODataException("Authorization error. User '" + p.getName() + "' does not have READ permission on 'Cars' entity");
        }
        int manufacturerKey = getKeyValue(uriInfo.getKeyPredicates().get(0));
        AuditHelper.trace(p, "cars", "r");
        List<Map<String, Object>> cars = new ArrayList<Map<String, Object>>();
        cars.add(dataStore.getCar(manufacturerKey));

        return EntityProvider.writeFeed(contentType, entitySet, cars, EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
      }

      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
    }

    throw new ODataNotImplementedException();
  }

  @Override
  public ODataResponse updateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, boolean merge, String contentType) throws ODataException {
    Principal p = SecurityUtils.getPrincipal();
    if (!SecurityUtils.userInRole(p, "manager")) {
      throw new ODataException("Permission error. User not in the manager role");
    }

    EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(false).build();
    ODataEntry entry = EntityProvider.readEntry(requestContentType, uriInfo.getTargetEntitySet(), content, properties);

    Map<String, Object> data = entry.getProperties();

    if (ENTITY_SET_NAME_CARS.equals(uriInfo.getTargetEntitySet().getName())) {
      if (!SecurityUtils.canUpdate(p, "cars")) {
        throw new ODataException("Authorization error. User '" + p.getName() + "' does not have UPDATE permission on 'Cars' entity");
      }
      int carId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.carExists(carId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.updateCar(carId, data);
      AuditHelper.trace(p, "cars", "u");
    }
    else if (ENTITY_SET_NAME_MANUFACTURERS.equals(uriInfo.getTargetEntitySet().getName())) {
      if (!SecurityUtils.canUpdate(p, "manufacturers")) {
        throw new ODataException("Authorization error. User '" + p.getName() + "' does not have UPDATE permission on 'Manufacturers' entity");
      }
      int manufacturerId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.manufacturerExists(manufacturerId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.updateManufacturer(manufacturerId, data);
      AuditHelper.trace(p, "manufacturers", "u");
    }

    //we can return Status Code 204 No Content because the URI Parsing already guarantees that
    //a) only valid URIs are dispatched (also checked against the metadata)
    //b) 404 Not Found is already returned above, when the entry does not exist
    return ODataResponse.status(HttpStatusCodes.NO_CONTENT).build();
  }

  @Override
  public ODataResponse deleteEntity(DeleteUriInfo uriInfo, String contentType) throws ODataException {
    Principal p = SecurityUtils.getPrincipal();
    if (ENTITY_SET_NAME_CARS.equals(uriInfo.getTargetEntitySet().getName())) {
      if (!SecurityUtils.canDelete(p, "cars")) {
        throw new ODataException("Authorization error. User '" + p.getName() + "' does not have DELETE permission on 'Cars' entity");
      }
      int carId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.carExists(carId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.deleteCar(carId);
      AuditHelper.trace(p, "cars", "d");
    }
    else if (ENTITY_SET_NAME_MANUFACTURERS.equals(uriInfo.getTargetEntitySet().getName())) {
      if (!SecurityUtils.canDelete(p, "manufacturers")) {
        throw new ODataException("Authorization error. User '" + p.getName() + "' does not have DELETE permission on 'Manufacturers' entity");
      }
      int manufacturerId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.manufacturerExists(manufacturerId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.deleteManufacturer(manufacturerId);
      AuditHelper.trace(p, "manufacturers", "d");
    }

    //we can return Status Code 204 No Content because the URI Parsing already guarantees that
    //a) only valid URIs are dispatched (also checked against the metadata)
    //b) 404 Not Found is already returned above, when the entry does not exist
    return ODataResponse.status(HttpStatusCodes.NO_CONTENT).build();
  }
}
