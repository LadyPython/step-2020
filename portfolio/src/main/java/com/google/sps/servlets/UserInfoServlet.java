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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/user-info")
public class UserInfoServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().println("You should log in to change nickname");
      return;
    }
    
    String nickname = request.getParameter("nickname");
    String uid = userService.getCurrentUser().getUserId();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", uid);
    entity.setProperty("uid", uid);
    entity.setProperty("nickname", nickname);
    datastore.put(entity);

    response.sendRedirect("/chat.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    UserService userService = UserServiceFactory.getUserService();
    
    String destinationURL = request.getHeader("referer");
    if (destinationURL == null) {
        destinationURL = "/index.html";
    }
    boolean isLoggedIn = userService.isUserLoggedIn();
    
    Map<String, Object> userInfo = new HashMap<String, Object>();
    userInfo.put("is-logged-in", isLoggedIn);
    if (isLoggedIn) {
      String uid = userService.getCurrentUser().getUserId();
      userInfo.put("logout-url", userService.createLogoutURL(destinationURL));
      userInfo.put("uid", uid);
      userInfo.put("nickname", User.getUserNickname(uid));
      userInfo.put("vote", User.getVote(uid));
    } else {
      userInfo.put("login-url", userService.createLoginURL(destinationURL));
    }
    
    Gson gson = new Gson();
    String json = gson.toJson(userInfo);
    response.getWriter().println(json);
  }
}
