package com.arkitechtura.odata.sample;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by equiros on 8/18/2014.
 */
public class SampleServiceFactory extends ODataServiceFactory {
  @Override
  public ODataService createService(ODataContext ctx) throws ODataException {
    EdmProvider provider = new SampleEdmProvider();
    ODataSingleProcessor singleProcessor = new SampleSingleProcessor();
    HttpServletRequest request = (HttpServletRequest) ctx.getParameter(ODataContext.HTTP_SERVLET_REQUEST_OBJECT);
    SecurityUtils.setPrincipal(request);
    return createODataSingleProcessorService(provider, singleProcessor);
  }
}
