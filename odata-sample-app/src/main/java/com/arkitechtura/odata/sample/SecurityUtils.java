package com.arkitechtura.odata.sample;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by equiros on 9/18/2014.
 */
public class SecurityUtils {
  private static final ThreadLocal<Principal> principal = new ThreadLocal<>();

  public static void setPrincipal(HttpServletRequest request) {
    principal.set(request.getUserPrincipal());
  }

  public static Principal getPrincipal() {
    return principal.get();
  }

  public boolean userInRole(Principal principal, String role) {return false;}

  public boolean canCreate(Principal principal, String entity) {return false;}

  public boolean canRead(Principal principal, String entity) {return false;}

  public boolean canUpdate(Principal principal, String entity) {return false;}

  public boolean canDelete(Principal principal, String entity) {return false;}
}
