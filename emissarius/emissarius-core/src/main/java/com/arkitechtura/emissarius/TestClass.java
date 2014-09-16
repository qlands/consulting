package com.arkitechtura.emissarius;

import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.service.log.LogService;

/**
 * Created by equiros on 9/15/2014.
 */
public class TestClass {
  @Requires
  private LogService log;
}
