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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/vk-data")
public class VKDataServlet extends HttpServlet {

  private Map<String, Integer> countries_sorted = new LinkedHashMap<>();
  private Map<String, Integer> cities_sorted = new LinkedHashMap<>();
  private Map<String, Integer> genders = new HashMap<>();
  private Map<String, Map<String, Integer>> views = new LinkedHashMap<>();

  @Override
  public void init() {
    Map<String, Integer> countries = new HashMap<>();
    Map<String, Integer> cities = new HashMap<>();

    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(
        "/WEB-INF/vkontakte_stat.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
      String year = cells[0].replace("\"", "").split("\\.")[2];
      String type = cells[1].replace("\"", "");
      switch (type) {
        case "countries":
          String country = cells[2].replace("\"", "");
          try {
            Integer value = Integer.valueOf(cells[4]);
            Integer current_value = countries.get(country);
            if (!country.equals("Russia") && !country.equals("")) {
              countries.put(country, current_value == null ? value : current_value + value);
            }
          } catch (NumberFormatException e) {
            System.out.println(e);
          }
          break;
        
        case "cities":
          String city = cells[2].replace("\"", "");
          try {
            Integer value = Integer.valueOf(cells[4]);
            Integer current_value = cities.get(city);
            if (!city.equals("")) {
              cities.put(city, current_value == null ? value : current_value + value);
            }
          } catch (NumberFormatException e) {
            System.out.println(e);
          }
          break;
        
        case "gender": 
          String gender = cells[2].replace("\"", "");
          try {
            Integer value = Integer.valueOf(cells[4]);
            Integer current_value = genders.get(gender);
            genders.put(gender, current_value == null ? value : current_value + value);
          } catch (NumberFormatException e) {
            System.out.println(e);
          }
          break;
        
        case "views": 
          try {
            Integer value = Integer.valueOf(cells[4]);
            Map<String, Integer> views_for_year = null;
            if (views.get(year) == null) {
              views_for_year = new HashMap<String, Integer>();
            } else {
              views_for_year = views.get(year);
            }
            if (value != 0) {
              Integer current_value = views_for_year.get("total");
              views_for_year.put("total", current_value == null ? value : current_value + value);
              views.put(year, views_for_year);
            }
          } catch (NumberFormatException e) {
            System.out.println(e);
          }
          break;
        case "age":
          String age = cells[2].replace("\"", ""); 
          try {
            Integer value = Integer.valueOf(cells[4]);
            Map<String, Integer> views_for_year = null;
            if (views.get(year) == null) {
                views_for_year = new HashMap<String, Integer>();
            } else {
                views_for_year = views.get(year);
            }
            Integer current_value = views_for_year.get(age);
            views_for_year.put(age, current_value == null ? value : current_value + value);
            views.put(year, views_for_year);
          } catch (NumberFormatException e) {
            System.out.println(e);
          }
          break;
      }
    }
    scanner.close();

    List<Map.Entry<String, Integer>> countries_list = new ArrayList<>(countries.entrySet());
    countries_list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    for (Map.Entry<String, Integer> entry : countries_list) {
        countries_sorted.put(entry.getKey(), entry.getValue());
    }

    List<Map.Entry<String, Integer>> cities_list = new ArrayList<>(cities.entrySet());
    cities_list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    for (Map.Entry<String, Integer> entry : cities_list) {
        cities_sorted.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = "";
    switch (request.getParameter("type")) {
      case "countries":
        json = gson.toJson(countries_sorted);
        break;
      case "cities":
        json = gson.toJson(cities_sorted);
        break;
      case "genders":
        json = gson.toJson(genders);
        break;
      case "ages":
        json = gson.toJson(views);
        break;
    }
    response.getWriter().println(json);
  }
}
