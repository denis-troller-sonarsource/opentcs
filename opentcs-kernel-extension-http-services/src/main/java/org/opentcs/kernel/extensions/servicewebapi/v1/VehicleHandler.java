// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.kernel.extensions.servicewebapi.v1;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.opentcs.components.kernel.services.InternalVehicleService;
import org.opentcs.components.kernel.services.RouterService;
import org.opentcs.data.ObjectUnknownException;
import org.opentcs.data.TCSObjectReference;
import org.opentcs.data.model.AcceptableOrderType;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Path;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.TCSResourceReference;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.model.Vehicle.EnergyLevelThresholdSet;
import org.opentcs.data.order.Route;
import org.opentcs.drivers.vehicle.VehicleCommAdapterDescription;
import org.opentcs.drivers.vehicle.VehicleCommAdapterMessage;
import org.opentcs.drivers.vehicle.management.VehicleAttachmentInformation;
import org.opentcs.kernel.extensions.servicewebapi.KernelExecutorWrapper;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.GetVehicleResponseTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.PostVehicleCommAdapterMessageRequestTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.PostVehicleRoutesRequestTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.PutVehicleAcceptableOrderTypesTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.PutVehicleEnergyLevelThresholdSetTO;
import org.opentcs.kernel.extensions.servicewebapi.v1.binding.shared.Property;
import org.opentcs.kernel.extensions.servicewebapi.v1.converter.VehicleConverter;

/**
 * Handles requests related to vehicles.
 */
public class VehicleHandler {

  private static final String UNKNOWN_VEHICLE = "Unknown vehicle: ";
  private static final String VALUE = "value";

  private final InternalVehicleService vehicleService;
  private final RouterService routerService;
  private final KernelExecutorWrapper executorWrapper;
  private final VehicleConverter vehicleConverter;

  /**
   * Creates a new instance.
   *
   * @param vehicleService Used to update vehicle instances.
   * @param routerService Used to get information about potential routes.
   * @param executorWrapper Executes calls via the kernel executor and waits for the outcome.
   * @param vehicleConverter Provides methods for converting vehicle data.
   */
  @Inject
  public VehicleHandler(
      InternalVehicleService vehicleService,
      RouterService routerService,
      KernelExecutorWrapper executorWrapper,
      VehicleConverter vehicleConverter
  ) {
    this.vehicleService = requireNonNull(vehicleService, "vehicleService");
    this.routerService = requireNonNull(routerService, "routerService");
    this.executorWrapper = requireNonNull(executorWrapper, "executorWrapper");
    this.vehicleConverter = requireNonNull(vehicleConverter, "vehicleConverter");
  }

  /**
   * Find all vehicles orders and filters depending on the given parameters.
   *
   * @param procStateName The filter parameter for the processing state of the vehicle.
   * The filtering is disabled for this parameter if the value is null.
   * @return A list of vehicles, that match the filter.
   * @throws IllegalArgumentException If procStateName could not be parsed.
   */
  public List<GetVehicleResponseTO> getVehiclesState(
      @Nullable
      String procStateName
  )
      throws IllegalArgumentException {
    return executorWrapper.callAndWait(() -> {
      Vehicle.ProcState pState = procStateName == null
          ? null
          : Vehicle.ProcState.valueOf(procStateName);

      return vehicleService.stream(Vehicle.class)
          .filter(Filters.vehicleWithProcState(pState))
          .map(vehicleConverter::toGetVehicleResponseTO)
          .sorted(Comparator.comparing(GetVehicleResponseTO::getName))
          .toList();
    });
  }

  /**
   * Finds the vehicle with the given name.
   *
   * @param name The name of the requested vehicle.
   * @return A single vehicle that has the given name.
   * @throws ObjectUnknownException If a vehicle with the given name does not exist.
   */
  public GetVehicleResponseTO getVehicleStateByName(String name)
      throws ObjectUnknownException {
    requireNonNull(name, "name");

    return executorWrapper.callAndWait(
        () -> vehicleService.fetch(Vehicle.class, name)
            .map(vehicleConverter::toGetVehicleResponseTO)
            .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name))
    );
  }

  public void putVehicleIntegrationLevel(String name, String value)
      throws ObjectUnknownException,
        IllegalArgumentException {
    requireNonNull(name, "name");
    requireNonNull(value, VALUE);

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      vehicleService.updateVehicleIntegrationLevel(
          vehicle.getReference(),
          Vehicle.IntegrationLevel.valueOf(value)
      );
    });
  }

  public void putVehiclePaused(String name, String value)
      throws ObjectUnknownException,
        IllegalArgumentException {
    requireNonNull(name, "name");
    requireNonNull(value, VALUE);

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      vehicleService.updateVehiclePaused(vehicle.getReference(), Boolean.parseBoolean(value));
    });
  }

  public void putVehicleEnvelopeKey(String name, String value)
      throws ObjectUnknownException,
        IllegalArgumentException {
    requireNonNull(name, "name");

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      vehicleService.updateVehicleEnvelopeKey(vehicle.getReference(), value);
    });
  }

  public void putVehicleCommAdapterEnabled(String name, String value)
      throws ObjectUnknownException,
        IllegalArgumentException {
    requireNonNull(name, "name");
    requireNonNull(value, VALUE);

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      if (Boolean.parseBoolean(value)) {
        vehicleService.enableCommAdapter(vehicle.getReference());
      }
      else {
        vehicleService.disableCommAdapter(vehicle.getReference());
      }
    });
  }

  public VehicleAttachmentInformation getVehicleCommAdapterAttachmentInformation(String name)
      throws ObjectUnknownException {
    requireNonNull(name, "name");

    return executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      return vehicleService.fetchAttachmentInformation(vehicle.getReference());
    });
  }

  public void putVehicleCommAdapter(String name, String value)
      throws ObjectUnknownException {
    requireNonNull(name, "name");
    requireNonNull(value, VALUE);

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      VehicleCommAdapterDescription newAdapter
          = vehicleService.fetchAttachmentInformation(vehicle.getReference())
              .getAvailableCommAdapters()
              .stream()
              .filter(description -> description.getClass().getName().equals(value))
              .findAny()
              .orElseThrow(
                  () -> new IllegalArgumentException("Unknown vehicle driver class name: " + value)
              );
      vehicleService.attachCommAdapter(vehicle.getReference(), newAdapter);
    });
  }

  public void postVehicleCommAdapterMessage(
      String name,
      PostVehicleCommAdapterMessageRequestTO request
  )
      throws ObjectUnknownException {
    requireNonNull(name, "name");
    requireNonNull(request, "request");

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      vehicleService.sendCommAdapterMessage(
          vehicle.getReference(),
          new VehicleCommAdapterMessage(
              request.getType(),
              toParameterMap(request.getParameters())
          )
      );
    });
  }

  /**
   * Sets the position of a vehicle's loopback comm adapter.
   * Sending a {@code null} or empty position name resets the vehicle's position.
   *
   * @param name The name of the vehicle.
   * @param position The name of the position to set, or {@code null}/empty to reset.
   * @throws ObjectUnknownException If a vehicle with the given name does not exist.
   */
  public void postVehicleCommAdapterPosition(
      String name,
      @Nullable String position
  )
      throws ObjectUnknownException {
    requireNonNull(name, "name");

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      if (position == null || position.isBlank()) {
        vehicleService.sendCommAdapterMessage(
            vehicle.getReference(),
            new VehicleCommAdapterMessage(
                "tcs:virtualVehicle:resetPosition",
                Map.of()
            )
        );
      }
      else {
        vehicleService.sendCommAdapterMessage(
            vehicle.getReference(),
            new VehicleCommAdapterMessage(
                "tcs:virtualVehicle:setPosition",
                Map.of("position", position)
            )
        );
      }
    });
  }

  public void putVehicleAcceptableOrderTypes(
      String name,
      PutVehicleAcceptableOrderTypesTO acceptableOrderTypes
  )
      throws ObjectUnknownException {
    requireNonNull(name, "name");
    requireNonNull(acceptableOrderTypes, "acceptableOrderTypes");

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      vehicleService.updateVehicleAcceptableOrderTypes(
          vehicle.getReference(),
          acceptableOrderTypes.getAcceptableOrderTypes().stream()
              .map(
                  acceptableOrderType -> new AcceptableOrderType(
                      acceptableOrderType.getName(),
                      acceptableOrderType.getPriority()
                  )
              )
              .collect(Collectors.toSet())
      );
    });
  }

  public void putVehicleEnergyLevelThresholdSet(
      String name,
      PutVehicleEnergyLevelThresholdSetTO energyLevelThresholdSet
  )
      throws ObjectUnknownException {
    requireNonNull(name, "name");
    requireNonNull(energyLevelThresholdSet, "energyLevelThresholdSet");

    executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      vehicleService.updateVehicleEnergyLevelThresholdSet(
          vehicle.getReference(),
          new EnergyLevelThresholdSet(
              energyLevelThresholdSet.getEnergyLevelCritical(),
              energyLevelThresholdSet.getEnergyLevelGood(),
              energyLevelThresholdSet.getEnergyLevelSufficientlyRecharged(),
              energyLevelThresholdSet.getEnergyLevelFullyRecharged()
          )
      );
    });
  }

  public Map<TCSObjectReference<Point>, Set<Route>> getVehicleRoutes(
      String name,
      int maxRoutesPerDestinationPoint,
      PostVehicleRoutesRequestTO request
  )
      throws ObjectUnknownException {
    requireNonNull(name, "name");
    requireNonNull(request, "request");

    return executorWrapper.callAndWait(() -> {
      Vehicle vehicle = vehicleService.fetch(Vehicle.class, name)
          .orElseThrow(() -> new ObjectUnknownException(UNKNOWN_VEHICLE + name));

      TCSObjectReference<Point> sourcePointRef = resolveSourcePoint(vehicle, request);

      Set<TCSObjectReference<Point>> destinationPointRefs = request.getDestinationPoints()
          .stream()
          .map(destPointName -> {
            Point destPoint = vehicleService.fetch(Point.class, destPointName)
                .orElseThrow(
                    () -> new ObjectUnknownException("Unknown destination point: " + destPointName)
                );
            return destPoint.getReference();
          })
          .collect(Collectors.toSet());

      Set<TCSResourceReference<?>> resourcesToAvoid = resolveResourcesToAvoid(request);

      return routerService.computeRoutes(
          vehicle.getReference(),
          sourcePointRef,
          destinationPointRefs,
          resourcesToAvoid,
          maxRoutesPerDestinationPoint
      );
    });
  }

  private TCSObjectReference<Point> resolveSourcePoint(
      Vehicle vehicle,
      PostVehicleRoutesRequestTO request
  ) {
    if (request.getSourcePoint() == null) {
      if (vehicle.getCurrentPosition() == null) {
        throw new IllegalArgumentException("Unknown vehicle position: " + vehicle.getName());
      }
      return vehicle.getCurrentPosition();
    }

    Point sourcePoint = vehicleService.fetch(Point.class, request.getSourcePoint())
        .orElseThrow(
            () -> new ObjectUnknownException(
                "Unknown source point: " + request.getSourcePoint()
            )
        );
    return sourcePoint.getReference();
  }

  private Set<TCSResourceReference<?>> resolveResourcesToAvoid(PostVehicleRoutesRequestTO request) {
    Set<TCSResourceReference<?>> resourcesToAvoid = new HashSet<>();

    if (request.getResourcesToAvoid() == null) {
      return resourcesToAvoid;
    }

    for (String resourceName : request.getResourcesToAvoid()) {
      resourcesToAvoid.add(resolveResource(resourceName));
    }

    return resourcesToAvoid;
  }

  private TCSResourceReference<?> resolveResource(String resourceName) {
    Optional<Point> point = vehicleService.fetch(Point.class, resourceName);
    if (point.isPresent()) {
      return point.get().getReference();
    }

    Optional<Path> path = vehicleService.fetch(Path.class, resourceName);
    if (path.isPresent()) {
      return path.get().getReference();
    }

    Optional<Location> location = vehicleService.fetch(Location.class, resourceName);
    if (location.isPresent()) {
      return location.get().getReference();
    }

    throw new ObjectUnknownException("Unknown resource: " + resourceName);
  }

  private Map<String, String> toParameterMap(List<Property> parameters) {
    Map<String, String> result = new HashMap<>();
    if (parameters != null) {
      for (Property param : parameters) {
        result.put(param.getKey(), param.getValue());
      }
    }
    return result;
  }
}
