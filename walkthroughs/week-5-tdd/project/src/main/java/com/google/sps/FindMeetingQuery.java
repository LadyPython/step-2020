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

package com.google.sps;

import java.lang.Math;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> eventTimeRanges = filterEventTimes(events, request.getAttendees());
    return findTimes(eventTimeRanges, request.getDuration());
  }

  public Collection<TimeRange> filterEventTimes(Collection<Event> events, Collection<String> attendees) {
    Predicate<Event> streamsPredicate = event -> !intersection(event.getAttendees(), attendees).isEmpty();
    return events.stream().filter(streamsPredicate).map(event -> event.getWhen()).collect(Collectors.toList());
  }

  public Collection<String> intersection(Collection<String> c1, Collection<String> c2) {
    Collection<String> intersection = new HashSet<String>(c1);
    intersection.retainAll(c2);
    return intersection;
  }

  public Collection<TimeRange> findTimes(Collection<TimeRange> eventTimeRanges, long duration) {
    if (duration > TimeRange.WHOLE_DAY.duration()) {
      return Collections.emptyList();
    }
    
    if (eventTimeRanges.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    List<TimeRange> eventTimeRangesList = new ArrayList<>(eventTimeRanges);
    Collections.sort(eventTimeRangesList, TimeRange.ORDER_BY_START);

    Collection<TimeRange> times = new ArrayList<>();
    int end = TimeRange.START_OF_DAY;
    for (TimeRange eventTimeRange : eventTimeRangesList) {
      int start = eventTimeRange.start();
      if (end < start) {
        if (start - end >= duration) {
          times.add(TimeRange.fromStartEnd(end, start, false));
        }
      }
      end = Math.max(end, eventTimeRange.end());
    }

    if (TimeRange.END_OF_DAY + 1 - end >= duration) {
      times.add(TimeRange.fromStartEnd(end, TimeRange.END_OF_DAY, true));
    }

    return times;
  }
}
