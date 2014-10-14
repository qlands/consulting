package org.cabi.ofra.dataload.event;

import java.util.List;

/**
 * (c) 2014, Eduardo Quirós-Campos
 */
public interface IEventCollector {
  public void addEvent(Event event);
  public Event removeEvent(String id);
  public List<Event> getEvents();
  public List<Event> getEvents(IEventFilter filter);
}
