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
  public Collection<TimeRange> findTimeRangesForMeeting(Collection<Event> events, MeetingRequest request) {
    EventsTimeRange events_time_range = filterEventsTime(events, request.getAttendees(), request.getOptionalAttendees());

    Collection<TimeRange> times = findTimes(events_time_range.getAllEventsTimeRange(), request.getDuration());
    if (times.isEmpty()) {
      times = findTimes(events_time_range.getMandatoryEventsTimeRange(), request.getDuration());
    }
    return times;
  }

  /**
   * Filter events which attend at list one of attendees. 
   * Collect event to mandatory_events_time_range if mandatory attendee has this meeting event, 
   * to optional_events_time_range if optional attendee has this meetang. 
   * mandatory_events_time_range for TimeRanges while attendees are busy, 
   * optional_events_time_range for TimeRanges while optional attendees are busy.
   */
  private EventsTimeRange filterEventsTime(Collection<Event> events, Collection<String> attendees, Collection<String> optional_attendees) {
    Collection<TimeRange> mandatory_events_time_range = new HashSet<>();
    Collection<TimeRange> optional_events_time_range = new HashSet<>();

    for (Event event : events) {
      Collection<String> event_attendees = event.getAttendees();
      if (!intersection(event_attendees, attendees).isEmpty()) {
        mandatory_events_time_range.add(event.getWhen());
      }
      if (!intersection(event_attendees, optional_attendees).isEmpty()) {
        optional_events_time_range.add(event.getWhen());
      }
    }
    
    return new EventsTimeRange(mandatory_events_time_range, optional_events_time_range);
  }

  private Collection<String> intersection(Collection<String> c1, Collection<String> c2) {
    Collection<String> intersection = new HashSet<String>(c1);
    intersection.retainAll(c2);
    return intersection;
  }

  /**
   * If one or more time slots exists so that both mandatory and optional attendees can attend, return those time slots. 
   * Otherwise, return the time slots that fit just the mandatory attendees.
   */
  private Collection<TimeRange> findTimes(Collection<TimeRange> events_time_range, long duration) {
    if (duration > TimeRange.WHOLE_DAY.duration() || duration < 0) {
      return Arrays.asList();
    }
    
    if (events_time_range.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    List<TimeRange> events_time_range_list = new ArrayList<>(events_time_range);
    Collections.sort(events_time_range_list, TimeRange.ORDER_BY_START);
    events_time_range_list.add(TimeRange.fromStartDuration(TimeRange.END_OF_DAY, 0));

    Collection<TimeRange> times = new ArrayList<>();
    int end_last_event = TimeRange.START_OF_DAY;
    for (TimeRange event_time_range : events_time_range_list) {
      int start_this_event = event_time_range.start();
      if (start_this_event - end_last_event >= duration) {
        times.add(TimeRange.fromStartEnd(end_last_event, start_this_event));
      }
      end_last_event = Math.max(end_last_event, event_time_range.end());
    }

    return times;
  }
}
