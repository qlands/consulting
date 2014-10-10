package org.cabi.ofra.dataload.impl;

import org.cabi.ofra.dataload.event.Event;
import org.cabi.ofra.dataload.event.IEventCollector;
import org.cabi.ofra.dataload.event.IEventFilter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * (c) 2014, Eduardo Quirós-Campos
 */
public class DefaultEventCollector implements IEventCollector {
  private Map<String, Event> eventMap;
  private List<Event> events;

  public DefaultEventCollector() {
    eventMap = new HashMap<String, Event>();
    events = new LinkedList<Event>();
  }


  @Override
  public void addEvent(Event event) {
    events.add(event);
    eventMap.put(event.getId(), event);
  }

  @Override
  public boolean removeEvent(String id) {
    return false;
  }

  @Override
  public List<Event> getEvents() {
    return null;
  }

  @Override
  public List<Event> getEvents(IEventFilter filter) {
    return null;
  }
}
