package ru.spbu.math.baobab.server;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.spbu.math.baobab.lang.AttendeeCommandParser;
import ru.spbu.math.baobab.lang.AuditoriumCommandParser;
import ru.spbu.math.baobab.lang.Parser;
import ru.spbu.math.baobab.lang.ScriptInterpreter;
import ru.spbu.math.baobab.lang.TimeSlotCommandParser;
import ru.spbu.math.baobab.model.Attendee;
import ru.spbu.math.baobab.model.AttendeeExtent;
import ru.spbu.math.baobab.model.Auditorium;
import ru.spbu.math.baobab.model.AuditoriumExtent;
import ru.spbu.math.baobab.model.TimeSlot;
import ru.spbu.math.baobab.model.TimeSlotExtent;
import ru.spbu.math.baobab.server.sql.AttendeeExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.AuditoriumExtentSqlImpl;
import ru.spbu.math.baobab.server.sql.TimeSlotExtentSqlImpl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Simple script form handler
 * Get script source from the web interface and try to execute it
 * 
 * @author agudulin
 */
public class ScriptFormServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("ScriptFormService");

  private final AttendeeExtent myAttendeeExtent = new AttendeeExtentSqlImpl();
  private final AuditoriumExtent myAuditoriumExtent = new AuditoriumExtentSqlImpl();
  private final TimeSlotExtent myTimeSlotExtent = new TimeSlotExtentSqlImpl();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    process(request, response, "");
  }

  private void process(HttpServletRequest request, HttpServletResponse response, String result) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    request.setAttribute("result", result);
    request.setAttribute("group_list", getGroupList());
    request.setAttribute("teacher_list", getTeacherList());
    request.setAttribute("auditorium_list", getAuditoriumList());
    request.setAttribute("time_slot_list", getTimeSlotList());
    request.setAttribute("placeholders", Parser.placeholders());
    RequestDispatcher scriptForm = request.getRequestDispatcher("/script_form.jsp");
    scriptForm.forward(request, response);
  }

  private String getGroupList() {
    return getAttendeeList(Attendee.Type.ACADEMIC_GROUP);
  }

  private String getTeacherList() {
    return getAttendeeList(Attendee.Type.TEACHER);
  }
  
  private String getAttendeeList(Attendee.Type type) {
    List<Attendee> groups = Lists.newArrayList();
    for (Attendee a : myAttendeeExtent.getAll()) {
      if (a.getType() == type) {
        groups.add(a);
      }
    }
    List<String> names = Lists.newArrayList(Lists.transform(groups, new Function<Attendee, String>() {
      @Override
      public String apply(Attendee a) {
        return a.getName();
      }
    }));
    Collections.sort(names);
    return Joiner.on(", ").join(names);    
  }
  
  private String getTimeSlotList() {
    List<TimeSlot> timeSlots = Lists.newArrayList(myTimeSlotExtent.getAll());
    List<String> names = Lists.transform(timeSlots, new Function<TimeSlot, String>() {
      @Override
      public String apply(TimeSlot ts) {
        return String.format("%s (%s %02d:%02d - %02d:%02d)", ts.getName(), Parser.DAYS_LONG_RU[ts.getDayOfWeek() - 1], 
            ts.getStart().getHour(), ts.getStart().getMinute(), ts.getFinish().getHour(), ts.getFinish().getMinute());
      }
    });
    return Joiner.on(", ").join(names);
  }
  private String getAuditoriumList() {
    List<Auditorium> auditoria = Lists.newArrayList(myAuditoriumExtent.getAll());
    List<String> names = Lists.newArrayList(Lists.transform(auditoria, new Function<Auditorium, String>() {
      @Override
      public String apply(Auditorium a) {
        return a.getID();
      }
    }));
    Collections.sort(names);
    return Joiner.on(", ").join(names);        
  }
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String scriptText = request.getParameter("script");

    AttendeeExtent attendeeExtent = new AttendeeExtentSqlImpl();
    TimeSlotExtent timeSlotExtent = new TimeSlotExtentSqlImpl();
    AuditoriumExtent auditoriumExtent = new AuditoriumExtentSqlImpl();
    
    ScriptInterpreter interpreter = new ScriptInterpreter(Lists.<Parser>newArrayList(
        new TimeSlotCommandParser(timeSlotExtent), new AttendeeCommandParser(attendeeExtent), new AuditoriumCommandParser(auditoriumExtent)));
    
    String result = "Все завершилось прекрасно";
    for (String command : Splitter.on('\n').omitEmptyStrings().split(scriptText)) {
      try {
        interpreter.process(command);
      } catch (Throwable e) {
        LOGGER.log(Level.SEVERE, "Failed to execute script", e);
        result = String.format("Ошибка при выполнении команды %s", command);
        break;
      }
    }
    process(request, response, result);
  }
}
