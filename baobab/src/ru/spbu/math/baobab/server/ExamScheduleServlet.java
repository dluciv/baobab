package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.Event;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Get schedule of exams for all groups
 * 
 * @author agudulin
 */
public class ExamScheduleServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    TestData data = new TestData();
    Multimap<Attendee, Event> schedule = data.getExamSchedule();
    Multimap<String, Attendee> groups = getGroupList(schedule.keySet());

    request.setCharacterEncoding("UTF-8");
    request.setAttribute("groupsList", groups.asMap());
    request.setAttribute("schedule", schedule.asMap());
    RequestDispatcher view = request.getRequestDispatcher("/exam_schedule.jsp");
    view.forward(request, response);
  }

  private Multimap<String, Attendee> getGroupList(Set<Attendee> attendeeList) {
    if (attendeeList.isEmpty()) {
      return null;
    }

    Multimap<String, Attendee> groups = LinkedListMultimap.<String, Attendee> create();
    for (Attendee a : attendeeList) {
      groups.put(a.getName().substring(0, 1), a);
    }

    return groups;
  }
}