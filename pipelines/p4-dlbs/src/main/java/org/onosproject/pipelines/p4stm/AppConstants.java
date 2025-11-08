/*
 * Copyright 2019-present Open Networking Foundation
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

package org.onosproject.pipelines.p4dlbs;

import org.onosproject.net.pi.model.PiActionId;
// import org.onosproject.net.pi.model.PiActionParamId;
// import org.onosproject.net.pi.model.PiActionProfileId;
// import org.onosproject.net.pi.model.PiMeterId;
// import org.onosproject.net.pi.model.PiPacketMetadataId;
// import org.onosproject.net.pi.model.PiCounterId;
import org.onosproject.net.pi.model.PiMatchFieldId;
import org.onosproject.net.pi.model.PiTableId;
/**
 * Constants for basic pipeline.
 */
public final class AppConstants {

    // hide default constructor
    private AppConstants() {
    }

    // -- Header field IDs --
    // Ethernet
    public static final PiMatchFieldId HDR_HDR_ETHERNET_ETHER_TYPE =
            PiMatchFieldId.of("hdr.ethernet.ether_type");
    public static final PiMatchFieldId HDR_HDR_ETHERNET_SRC_ADDR =
            PiMatchFieldId.of("hdr.ethernet.src_addr");
    public static final PiMatchFieldId HDR_HDR_ETHERNET_DST_ADDR =
            PiMatchFieldId.of("hdr.ethernet.dst_addr");

    // IPv4
    public static final PiMatchFieldId HDR_HDR_IPV4_PROTOCOL =
            PiMatchFieldId.of("hdr.ipv4.protocol");
    public static final PiMatchFieldId HDR_HDR_IPV4_SRC_ADDR =
            PiMatchFieldId.of("hdr.ipv4.src_addr");
    public static final PiMatchFieldId HDR_HDR_IPV4_DST_ADDR =
            PiMatchFieldId.of("hdr.ipv4.dst_addr");

    // TCP
    public static final PiMatchFieldId HDR_HDR_TCP_SRC_PORT =
            PiMatchFieldId.of("hdr.tcp.srcPort");
    public static final PiMatchFieldId HDR_HDR_TCP_DST_PORT =
            PiMatchFieldId.of("hdr.tcp.dstPort");

    // CPU
    public static final PiMatchFieldId HDR_HDR_CPU_OUT_EGRESS_PORT =
            PiMatchFieldId.of("hdr.cpu_out.egress_port");
    public static final PiMatchFieldId HDR_HDR_CPU_IB_INGRESS_PORT =
            PiMatchFieldId.of("hdr.cpu_in.ingress_port");

    // --Standard Metadata--
    public static final PiMatchFieldId HDR_STANDARD_METADATA_INGRESS_PORT =
            PiMatchFieldId.of("standard_meta.ingress_port");
    public static final PiMatchFieldId HDR_STANDARD_METADATA_PACKET_LENGTH =
            PiMatchFieldId.of("standard_meta.packet_length");
    public static final PiMatchFieldId HDR_STANDARD_METADATA_EGRESS_SPEC =
            PiMatchFieldId.of("standard_meta.egress_spec");
    public static final PiMatchFieldId HDR_STANDARD_METADATA_EGRESS_PORT =
            PiMatchFieldId.of("standard_meta.egress_port");
    public static final PiMatchFieldId HDR_STANDARD_METADATA_INGRESS_GLOBAL_TIMESTAMP =
            PiMatchFieldId.of("standard_meta.ingress_global_timestamp");

    // -- Table IDs --
    public static final PiTableId INGRESS_STATE_TABLE =
            PiTableId.of("IngressControl.state_table");

    // Action IDs
    public static final PiActionId INGRESS_STATE_TABLE_UPDATE =
            PiActionId.of("IngressControl.update_state_table");
    public static final PiActionId INGRESS_STATE_TABLE_NEW_ENTRY =
            PiActionId.of("IngressControl.create_new_entry");
}
