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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible for deleting messages. */
@WebServlet("/delete-message")
public class DeleteMessageServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().println("You should log in to delete messages");
      return;
    }

    long id;
    try {
      id = Long.parseLong(request.getParameter("id"));
    } catch (NumberFormatException e) {
      System.err.println("Message id for deleting is not long: " + e);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println("Message id should be long");
      return;
    }

    String current_uid = userService.getCurrentUser().getUserId();
    String message_uid = request.getParameter("uid");
    if (!current_uid.equals(message_uid)) {
      System.err.println("User tries to delete others message");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println("You can delete only your messages");
      return;
    }

    Key messageEntityKey = KeyFactory.createKey("Message", id);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(messageEntityKey);
  }
}
