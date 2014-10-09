package com.arkitechtura.odata.sample;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.type.IntegerType;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
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

  private static Session getSession() {
    return HibernateUtil.getSessionFactory().getCurrentSession();
  }

  public static boolean userInRole(Principal principal, String role) {
    Session session = getSession();
    Transaction tx = session.beginTransaction();
    try {
      SQLQuery query = session.createSQLQuery("SELECT COUNT(*) as num FROM user_roles WHERE user_name = :user AND role_name = :role");
      query.setString("user", principal.getName()).setString("role", role);
      Object result = query.uniqueResult();
      BigInteger i = (BigInteger) result;
      return i.longValue() > 0;
    }
    finally {
      tx.commit();
    }
  }

  private static boolean checkPermission(Session session, String user, String entity, String permissionString) {
    SQLQuery query = session.createSQLQuery("SELECT COUNT(*) as num FROM user_permissions WHERE user_name = :user AND entity = :entity AND permissions like :permissions");
    query.setString("user", user);
    query.setString("entity", entity);
    query.setString("permissions", permissionString.toLowerCase());
    Object result = query.uniqueResult();
    BigInteger i = (BigInteger) result;
    return i.longValue() > 0;
  }

  public static boolean canCreate(Principal principal, String entity) {
    Session session = getSession();
    Transaction tx = session.beginTransaction();
    try {
      return checkPermission(session, principal.getName(), entity, "%c%");
    }
    finally {
      tx.commit();
    }
  }

  public static boolean canRead(Principal principal, String entity) {
    Session session = getSession();
    Transaction tx = session.beginTransaction();
    try {
      return checkPermission(session, principal.getName(), entity, "%r%");
    }
    finally {
      tx.commit();
    }
  }

  public static boolean canUpdate(Principal principal, String entity) {
    Session session = getSession();
    Transaction tx = session.beginTransaction();
    try {
      return checkPermission(session, principal.getName(), entity, "%u%");
    }
    finally {
      tx.commit();
    }
  }

  public static boolean canDelete(Principal principal, String entity) {
    Session session = getSession();
    Transaction tx = session.beginTransaction();
    try {
      return checkPermission(session, principal.getName(), entity, "%d%");
    }
    finally {
      tx.commit();
    }
  }
}
