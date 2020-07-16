// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.Message;
import com.google.sps.data.Chat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that encapsulates the subtraction game. */
@WebServlet("/send-message")
public final class ChatServlet extends HttpServlet {
  static final String ANONYM = "anonym";

  private Chat chat = new Chat();
  private String warning = "";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("chat", chat);
    data.put("warning", warning);
    
    String json = new Gson().toJson(data);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    Message message = getMessage(request);

    warning = "";
    if (message.nameIsEmpty()) {
      warning = "Please choose name other than an anonym.\n";
    }
    if (message.textIsEmpty()) {
      warning += "Please write a message.\n";
    }

    if (warning.length() != 0) {
      response.sendRedirect("/chat.html");
      return;
    }
    
    chat.sendMessage(message);

    // Redirect back to the HTML page.
    response.sendRedirect("/chat.html");
  }

  private Message getMessage(HttpServletRequest request) {
    return new Message(processName(request.getParameter("name")), processText(request.getParameter("text")));
  }

  /** Returns the name entered by the person, or "anonym" if the choice was empty. */
  private String processName(String name) {
    // If the input is empty, name -> anonym
    if (name.length() == 0) {
      return ANONYM;
    }

    // If the input is anonym, choose another name
    if (name.equalsIgnoreCase(ANONYM)) {
      System.err.println("Choosen name is " + ANONYM);
      return "";
    }
    return name;
  }

  /** Returns the text entered by the person. */
  private String processText(String text) {
    // If the input is empty
    if (text.length() == 0) {
      System.err.println("Empty message");
      return "";
    }
    return text;
  }
}
