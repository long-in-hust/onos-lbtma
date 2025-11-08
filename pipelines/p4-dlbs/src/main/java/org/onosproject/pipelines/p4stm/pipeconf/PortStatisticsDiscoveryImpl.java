/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.pipelines.p4dlbs.pipeconf;

import com.google.common.collect.Maps;
// import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.net.DeviceId;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DefaultPortStatistics;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.device.PortStatisticsDiscovery;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
// import org.onosproject.net.pi.model.PiCounterId;
// import org.onosproject.net.pi.model.PiPipeconf;
// import org.onosproject.net.pi.runtime.PiCounterCell;
// import org.onosproject.net.pi.runtime.PiCounterCellHandle;
// import org.onosproject.net.pi.runtime.PiCounterCellId;
import org.onosproject.net.pi.service.PiPipeconfService;
import org.onosproject.p4runtime.api.P4RuntimeClient;
import org.onosproject.p4runtime.api.P4RuntimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
// import java.util.Set;
import java.util.stream.Collectors;

// import static org.onosproject.net.pi.model.PiCounterType.INDIRECT;

/**
 * Implementation of the PortStatisticsBehaviour for basic.p4.
 */
public class PortStatisticsDiscoveryImpl extends AbstractHandlerBehaviour implements PortStatisticsDiscovery {

    // private static final long DEFAULT_P4_DEVICE_ID = 1;
    private static final Map<Pair<DeviceId, PortNumber>, Long> PORT_START_TIMES =
            Maps.newConcurrentMap();

    protected final Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public Collection<PortStatistics> discoverPortStatistics() {

        DeviceService deviceService = this.handler().get(DeviceService.class);
        DeviceId deviceId = this.data().deviceId();

        PiPipeconfService piPipeconfService = handler().get(PiPipeconfService.class);
        if (!piPipeconfService.ofDevice(deviceId).isPresent() ||
                !piPipeconfService.getPipeconf(piPipeconfService.ofDevice(deviceId).get()).isPresent()) {
            log.warn("Unable to get the pipeconf of {}, aborting operation", deviceId);
            return Collections.emptyList();
        }

        P4RuntimeController controller = handler().get(P4RuntimeController.class);
        P4RuntimeClient client = controller.get(deviceId);
        if (client == null) {
            log.warn("Unable to find client for {}, aborting operation", deviceId);
            return Collections.emptyList();
        }

        Map<Long, DefaultPortStatistics.Builder> portStatBuilders = Maps.newHashMap();
        deviceService.getPorts(deviceId)
                .forEach(p -> portStatBuilders.put(
                        p.number().toLong(),
                        DefaultPortStatistics.builder()
                                .setPort(p.number())
                                .setDeviceId(deviceId)
                                .setDurationSec(getDuration(p.number()))));

        return portStatBuilders
                .values()
                .stream()
                .map(DefaultPortStatistics.Builder::build)
                .collect(Collectors.toList());
    }

    private long getDuration(PortNumber port) {
        // FIXME: This is a workaround since we cannot determine the port
        // duration from a P4 counter. We'll be fixed by gNMI.
        final long now = System.currentTimeMillis() / 1000;
        final Long startTime = PORT_START_TIMES.putIfAbsent(
                Pair.of(data().deviceId(), port), now);
        return startTime == null ? now : now - startTime;
    }
}
