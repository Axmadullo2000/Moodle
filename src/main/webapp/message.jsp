<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // Этот фрагмент можно включать в любую JSP страницу для отображения сообщений
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");

    // Очищаем сообщения после отображения
    session.removeAttribute("successMessage");
    session.removeAttribute("errorMessage");
%>

<% if (successMessage != null && !successMessage.isEmpty()) { %>
<div class="alert alert-success" style="padding: 15px; margin-bottom: 20px; background: #d4edda; color: #155724; border: 1px solid #c3e6cb; border-radius: 8px; font-weight: 500;">
    ✓ <%= successMessage %>
</div>
<% } %>

<% if (errorMessage != null && !errorMessage.isEmpty()) { %>
<div class="alert alert-error" style="padding: 15px; margin-bottom: 20px; background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; border-radius: 8px; font-weight: 500;">
    ✗ <%= errorMessage %>
</div>
<% } %>
