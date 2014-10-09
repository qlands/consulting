package com.arkitechtura.odata.sample.audit;

import com.arkitechtura.odata.sample.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.security.Principal;

/**
 * (c) 2014, Eduardo Quirós-Campos
 */
public class AuditHelper {
  private static Session getSession() {
    return HibernateUtil.getSessionFactory().getCurrentSession();
  }

  public static void trace(Principal principal, String entity, String operation) {
    if (operation != null) {
      Session session = getSession();
      Transaction tx = session.beginTransaction();
      try {
        SQLQuery q = session.createSQLQuery("INSERT INTO audit_log (entity, user_name, operation) VALUES (:entity, :user, :operation)");
        q.setString("entity", entity);
        q.setString("user", principal.getName());
        q.setString("operation", operation.substring(0, 1));
        q.executeUpdate();
      }
      finally {
        tx.commit();
      }
    }
  }
}
