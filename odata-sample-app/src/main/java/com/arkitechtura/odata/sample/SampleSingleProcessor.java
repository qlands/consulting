package com.arkitechtura.odata.sample;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.arkitechtura.odata.sample.SampleEdmProvider.ENTITY_SET_NAME_CARS;
import static com.arkitechtura.odata.sample.SampleEdmProvider.ENTITY_SET_NAME_MANUFACTURERS;

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
}
