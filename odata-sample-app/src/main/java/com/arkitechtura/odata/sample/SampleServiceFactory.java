package com.arkitechtura.odata.sample;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;

/**
 * Created by equiros on 8/18/2014.
 */
public class SampleServiceFactory extends ODataServiceFactory {
  @Override
  public ODataService createService(ODataContext oDataContext) throws ODataException {
    EdmProvider provider = new SampleEdmProvider();
    ODataSingleProcessor singleProcessor = new SampleSingleProcessor();
    return createODataSingleProcessorService(provider, singleProcessor);
  }
}
