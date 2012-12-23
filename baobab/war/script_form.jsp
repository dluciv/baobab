<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<%@ include file="../include_header.jsp" %>
<html>
<body>
  <div class="container-fluid">
    <div class="row-fluid">
      <div class="page-header span12">
        <h2>Редактирование данных</h2>
      </div>
    </div>
    <div class="row-fluid">
      <div class="span3">
        <h4>У нас уже есть</h4>
        <ul class="well nav nav-list">
          <li class="nav-header">Группы</li>
          <li><p>${group_list}</p></li>
          <li class="nav-header">Преподаватели</li>
          <li><p>${teacher_list}</p></li>
          <li class="nav-header">Аудитории</li>
          <li><p>${auditorium_list}</p></li>
          <li class="nav-header">Временные слоты</li>
          <li><p>${time_slot_list}</p></li>
        </ul>
      </div>
      <div class="span9">
        <h4>И сейчас мы добавим</h4>
        <div class="btn-toolbar" id="controlPanel">
          <div class="btn-group">
            <button class="btn btn-small" id="exam">экзамен</button>
            <button class="btn btn-small" id="teacher">преподавателя</button>
            <button class="btn btn-small" id="event">событие</button>
          </div>
        </div>
        <form action="/data/edit" method="post">
          <div class="row-fluid">
            <div class="span12">
              <textarea class="span12" rows="10" name="script" id="script"></textarea>
            </div>
          </div>
          <div class="row-fluid">
            <span class="span10">
              <div class="${alert}">${result}</div>
            </span>
            <span class="span2"><input type="submit" class="btn btn-primary" style="float: right" value="Поехали!"/></span>
          </div>
        </form>
      </div>  
    </div>
  </div>

  <script type="text/javascript">
    var placeholders = {};
    <c:forEach items="${placeholders}" var="item">
      placeholders['${item.key}'] = '${item.value}';
    </c:forEach>

    var textarea = $('#script');

    // Add command template with placeholders to the text area
    $('#controlPanel button').click(function(){
      var buttonID = $(this).attr('id');
      textarea.val(textarea.val() + placeholders[buttonID] + '\n');
    });
  </script>
</body>
</html>
