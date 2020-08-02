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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.stream.Collectors; 
import java.util.stream.Stream; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/vk-data")
public class VKDataServlet extends HttpServlet {
  final String parseQuotedCSV = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

  private Map<String, Integer> countries = new HashMap<>();
  private Map<String, Integer> cities = new LinkedHashMap<>();
  private Map<String, Integer> genders = new HashMap<>();
  private Map<String, Map<String, Integer>> views = new LinkedHashMap<>();

  @Override
  public void init() {

    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(
        "/WEB-INF/vkontakte_stat.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      List<String> cells = Arrays.stream(line.split(parseQuotedCSV)).map(s -> s.replace("\"", "")).collect(Collectors.toList());

      String year = cells.get(0).split("\\.")[2];
      String type = cells.get(1);
      String key = cells.get(2);
      if(key.isEmpty()) {
          continue;
      }

      Integer value = 0;
      try {
        value = Integer.valueOf(cells.get(4));
      }
      catch (NumberFormatException e) {
        System.out.println(e);
        continue;
      }

      Map<String, Integer> views_for_year = views.getOrDefault(year, new HashMap<String, Integer>());

      switch (type) {
        case "countries":
          if (!key.equals("Russia") && !key.equals("")) {
            countries.put(key, countries.getOrDefault(key, 0) + value);
          }
          break;
        case "cities":
          if (!key.equals("")) {
            cities.put(key, cities.getOrDefault(key, 0) + value);
          }
          break;
        case "gender": 
          genders.put(key, genders.getOrDefault(key, 0) + value);
          break;
        
        case "views": 
          views_for_year.put("total", views_for_year.getOrDefault("total", 0) + value);
          break;
        case "age":
          views_for_year.put(key, views_for_year.getOrDefault(key, 0) + value);
          views.put(year, views_for_year);
          break;
        default: 
          break;
      }
    }
    scanner.close();

    cities = cities.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = "";
    switch (request.getParameter("type")) {
      case "countries":
        json = gson.toJson(countries);
        break;
      case "cities":
        json = gson.toJson(cities);
        break;
      case "genders":
        json = gson.toJson(genders);
        break;
      case "ages":
        json = gson.toJson(views);
        break;
      default:
        break;
    }
    response.getWriter().println(json);
  }
}
