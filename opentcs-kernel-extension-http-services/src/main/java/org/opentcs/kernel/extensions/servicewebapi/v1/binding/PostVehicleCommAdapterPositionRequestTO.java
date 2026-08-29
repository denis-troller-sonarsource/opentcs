// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.v1.binding;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

/**
 * A request to set the position of a vehicle's loopback comm adapter.
 */
public class PostVehicleCommAdapterPositionRequestTO {

  private final String newValue;

  @JsonCreator
  public PostVehicleCommAdapterPositionRequestTO(
      @Nullable
      @JsonProperty(value = "newValue")
      String newValue
  ) {
    this.newValue = newValue;
  }

  @Nullable
  public String getNewValue() {
    return newValue;
  }
}
