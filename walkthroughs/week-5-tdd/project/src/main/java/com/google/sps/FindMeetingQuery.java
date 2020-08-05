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
    EventsTimeRange eventsTimeRange = filterEventsTime(events, request.getAttendees(), request.getOptionalAttendees());

    Collection<TimeRange> times = findTimes(eventsTimeRange.getAllEventsTimeRange(), request.getDuration());
    if (times.isEmpty()) {
      times = findTimes(eventsTimeRange.getMandatoryEventsTimeRange(), request.getDuration());
    }
    return times;
  }

  /**
   * Filter events which attand at list one of attendees. Collect event to mandatoryEventsTimeRange if mandatory attendee has this meeting event, 
   * to optionalEventsTimeRange if optional attendee has this meetang. MandatoryEventsTimeRange for TimeRanges while attendees are busy, optionalEventsTimeRange for TimeRanges while optional attendees are busy.
   */
  private EventsTimeRange filterEventsTime(Collection<Event> events, Collection<String> attendees, Collection<String> optional_attendees) {
    Collection<TimeRange> mandatoryEventsTimeRange = new HashSet<>();
    Collection<TimeRange> optionalEventsTimeRange = new HashSet<>();

    for (Event event : events) {
      Collection<String> event_attendees = event.getAttendees();
      if (!intersection(event_attendees, attendees).isEmpty()) {
        mandatoryEventsTimeRange.add(event.getWhen());
      }
      if (!intersection(event_attendees, optional_attendees).isEmpty()) {
        optionalEventsTimeRange.add(event.getWhen());
      }
    }
    
    return new EventsTimeRange(mandatoryEventsTimeRange, optionalEventsTimeRange);
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
  private Collection<TimeRange> findTimes(Collection<TimeRange> eventsTimeRange, long duration) {
    if (duration > TimeRange.WHOLE_DAY.duration() || duration < 0) {
      return Arrays.asList();
    }
    
    if (eventsTimeRange.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    
    List<TimeRange> eventsTimeRangeList = new ArrayList<>(eventsTimeRange);
    Collections.sort(eventsTimeRangeList, TimeRange.ORDER_BY_START);
    eventsTimeRangeList.add(TimeRange.fromStartDuration(TimeRange.END_OF_DAY, 0));

    Collection<TimeRange> times = new ArrayList<>();
    int end_last_event = TimeRange.START_OF_DAY;
    for (TimeRange eventTimeRange : eventsTimeRangeList) {
      int start_this_event = eventTimeRange.start();
      if (start_this_event - end_last_event >= duration) {
        times.add(TimeRange.fromStartEnd(end_last_event, start_this_event));
      }
      end_last_event = Math.max(end_last_event, eventTimeRange.end());
    }

    return times;
  }
}
