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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * EventsTimeRange is the container class for all TimaRanges when a specific group of people are meeting and are therefore
 * busy. MandatoryEventsTimeRange for TimeRanges while attendees are busy, optionalEventsTimeRange for TimeRanges while optional attendees are busy.
 */
public final class EventsTimeRange {
  private final Collection<TimeRange> mandatory_events_time_range = new HashSet<>();
  private final Collection<TimeRange> optional_events_time_range = new HashSet<>();

  public EventsTimeRange(Collection<TimeRange> mandatory_events_time_range, Collection<TimeRange> optional_events_time_range) {
    this.mandatory_events_time_range.addAll(mandatory_events_time_range);
    this.optional_events_time_range.addAll(optional_events_time_range);
  }

  public Collection<TimeRange> getMandatoryEventsTimeRange() {
    return Collections.unmodifiableCollection(mandatory_events_time_range);
  }

  public Collection<TimeRange> getOptionalEventsTimeRange() {
    return Collections.unmodifiableCollection(optional_events_time_range);
  }

  public Collection<TimeRange> getAllEventsTimeRange() {
    Collection<TimeRange> all_events_time_range = new HashSet<>(mandatory_events_time_range);
    all_events_time_range.addAll(optional_events_time_range);
    return Collections.unmodifiableCollection(all_events_time_range);
  }
}
