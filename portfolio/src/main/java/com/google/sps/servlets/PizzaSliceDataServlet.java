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

import com.google.sps.data.User;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.Arrays; 
import java.util.List;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/pizza-data")
public class PizzaSliceDataServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Query query = new Query("Vote").addSort("slice", SortDirection.ASCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Integer> pizzaVotes = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0));
    for (Entity entity : results.asIterable()) {
      Integer slice = Integer.parseInt(entity.getProperty("slice").toString());
      if (slice > 5 || slice < 0) {
        System.err.println("Slice number is not in [0, 5]: " + slice);
        continue;
      }
      pizzaVotes.set(slice, pizzaVotes.get(slice) + 1);
    }
    Gson gson = new Gson();
    String json = gson.toJson(pizzaVotes);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().println("You should log in to vote");
      return;
    }

    String uid = userService.getCurrentUser().getUserId();
    if (User.getVote(uid) != null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println("You can't vote twice");
      return;
    }

    Integer slice = null;
    try {
      slice = Integer.parseInt(request.getParameter("slice"));
    } catch (NumberFormatException e) {
      System.err.println("Slice number is not int: " + e);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println("Slice number should be int");
      return;
    }
    if (slice > 5 || slice < 0) {
      System.err.println("Slice number is not in [0, 5]: " + slice);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println("Slice number should be in [0, 5]");
      return;
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("Vote", uid);
    entity.setProperty("uid", uid);
    entity.setProperty("slice", slice);
    datastore.put(entity);

    response.sendRedirect("/tasks.html");
  }
}
