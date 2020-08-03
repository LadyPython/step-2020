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

public final class EventsTimeRange {
  private final Collection<TimeRange> mandatoryEventsTimeRange = new HashSet<>();
  private final Collection<TimeRange> optionalEventsTimeRange = new HashSet<>();

  public EventsTimeRange(Collection<TimeRange> mandatoryEventsTimeRange, Collection<TimeRange> optionalEventsTimeRange) {
    this.mandatoryEventsTimeRange.addAll(mandatoryEventsTimeRange);
    this.optionalEventsTimeRange.addAll(optionalEventsTimeRange);
  }

  public Collection<TimeRange> getMandatoryEventsTimeRange() {
    return Collections.unmodifiableCollection(mandatoryEventsTimeRange);
  }

  public Collection<TimeRange> getOptionalEventsTimeRange() {
    return Collections.unmodifiableCollection(optionalEventsTimeRange);
  }

  public Collection<TimeRange> getAllEventsTimeRange() {
    Collection<TimeRange> allEventsTimeRange = new HashSet<>(mandatoryEventsTimeRange);
    allEventsTimeRange.addAll(optionalEventsTimeRange);
    return Collections.unmodifiableCollection(allEventsTimeRange);
  }
}
