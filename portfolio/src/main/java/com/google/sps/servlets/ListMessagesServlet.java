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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for listing messages. */
@WebServlet("/list-messages")
public class ListMessagesServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Message").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    long number_messages = 5;
    try {
      number_messages = Integer.parseInt(request.getParameter("number_messages"));
    } catch (NumberFormatException e) {
      System.err.println("User choose not a number for number_messages: " + e);
    }
    
    List<Message> messages = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      if (number_messages == 0) {
          break;
      }
      long id = entity.getKey().getId();
      String uid = (String) entity.getProperty("uid");
      String nickname = (String) entity.getProperty("nickname");
      String text = (String) entity.getProperty("text");
      long timestamp = (long) entity.getProperty("timestamp");

      messages.add(new Message(id, name, text, timestamp));
      number_messages -= 1;
    }

    response.setContentType("application/json;");
    Gson gson = new Gson();
    response.getWriter().println(gson.toJson(messages));
  }
}
