package com.arkitechtura.odata.sample;

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
        int id = getKeyValue(uriInfo.getKeyPredicates().get(0));
        Map<String, Object> data = dataStore.getCar(id);

        if (data != null) {
          URI serviceRoot = getContext().getPathInfo().getServiceRoot();
          EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder propertiesBuilder = EntityProviderWriteProperties.serviceRoot(serviceRoot);

          return EntityProvider.writeEntry(contentType, entitySet, data, propertiesBuilder.build());
        }
      }
      else if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
        int id = getKeyValue(uriInfo.getKeyPredicates().get(0));
        Map<String, Object> data = null;
        data = dataStore.getManufacturer(id);
        if (data != null) {
          URI serviceRoot = getContext().getPathInfo().getServiceRoot();
          EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder propertiesBuilder = EntityProviderWriteProperties.serviceRoot(serviceRoot);

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
      long carId = dataStore.storeCar(data);
      data.put("Id", carId);
    }
    else if (ENTITY_NAME_MANUFACTURER.equals(uriInfo.getStartEntitySet().getEntityType().getName())) {
      long manuId = dataStore.storeManufacturer(data);
      data.put("Id", manuId);
    }
    //serialize the entry, Location header is set by OData Library
    return EntityProvider.writeEntry(contentType, uriInfo.getStartEntitySet(), data, EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
  }

  @Override
  public ODataResponse readEntitySet(GetEntitySetUriInfo uriInfo, String contentType) throws ODataException {
    EdmEntitySet entitySet;

    if (uriInfo.getNavigationSegments().size() == 0) {
      entitySet = uriInfo.getStartEntitySet();

      if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
        return EntityProvider.writeFeed(contentType, entitySet, dataStore.getCars(), EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
      }
      else if (ENTITY_SET_NAME_MANUFACTURERS.equals(entitySet.getName())) {
        return EntityProvider.writeFeed(contentType, entitySet, dataStore.getManufacturers(), EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
      }

      throw new ODataNotFoundException(ODataNotFoundException.ENTITY);

    }
    else if (uriInfo.getNavigationSegments().size() == 1) {
      //navigation first level, simplified example for illustration purposes only
      entitySet = uriInfo.getTargetEntitySet();

      if (ENTITY_SET_NAME_CARS.equals(entitySet.getName())) {
        int manufacturerKey = getKeyValue(uriInfo.getKeyPredicates().get(0));

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
    EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(false).build();
    ODataEntry entry = EntityProvider.readEntry(requestContentType, uriInfo.getTargetEntitySet(), content, properties);

    Map<String, Object> data = entry.getProperties();

    if (ENTITY_SET_NAME_CARS.equals(uriInfo.getTargetEntitySet().getName())) {
      int carId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.carExists(carId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.updateCar(carId, data);
    }
    else if (ENTITY_SET_NAME_MANUFACTURERS.equals(uriInfo.getTargetEntitySet().getName())) {
      int manufacturerId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.manufacturerExists(manufacturerId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.updateManufacturer(manufacturerId, data);
    }

    //we can return Status Code 204 No Content because the URI Parsing already guarantees that
    //a) only valid URIs are dispatched (also checked against the metadata)
    //b) 404 Not Found is already returned above, when the entry does not exist
    return ODataResponse.status(HttpStatusCodes.NO_CONTENT).build();
  }

  @Override
  public ODataResponse deleteEntity(DeleteUriInfo uriInfo, String contentType) throws ODataException {
    if (ENTITY_SET_NAME_CARS.equals(uriInfo.getTargetEntitySet().getName())) {
      int carId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.carExists(carId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.deleteCar(carId);
    }
    else if (ENTITY_SET_NAME_MANUFACTURERS.equals(uriInfo.getTargetEntitySet().getName())) {
      int manufacturerId = getKeyValue(uriInfo.getKeyPredicates().get(0));
      if (!dataStore.manufacturerExists(manufacturerId)) {
        return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
      }
      dataStore.deleteManufacturer(manufacturerId);
    }

    //we can return Status Code 204 No Content because the URI Parsing already guarantees that
    //a) only valid URIs are dispatched (also checked against the metadata)
    //b) 404 Not Found is already returned above, when the entry does not exist
    return ODataResponse.status(HttpStatusCodes.NO_CONTENT).build();
  }
}
