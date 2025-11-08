package org.onosproject.pipelines.p4dlbs.cli;

import org.onlab.packet.IPv4;
// import org.onlab.packet.IpAddress;
import org.onlab.packet.IpPrefix;
import org.onlab.packet.TpPort;
// import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.DeviceId;
// import org.onosproject.net.flow.DefaultFlowRule;
// import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.pi.runtime.PiAction;
import org.onosproject.net.pi.model.PiActionId;
// import org.onosproject.net.pi.runtime.PiActionParam;
// import org.onosproject.net.pi.model.PiMatchFieldId;
// import org.onosproject.net.pi.model.PiTableId;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
// import org.onosproject.net.flow.criteria.PiCriterion;
// import org.onosproject.net.flow.criteria.Criteria;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;

// This class would be part of a custom ONOS application
public class RuleInstaller {

    private final FlowObjectiveService flowObjectiveService;
    private final CoreService coreService;
    private final ApplicationId appId;

    // P4 Table and Action IDs from p4_dlbs.p4info.txt
    private static final PiActionId UPDATE_ACTION_ID =
            PiActionId.of("IngressControl.update_state_table");

    public RuleInstaller(FlowObjectiveService fos, CoreService cs) {
        this.flowObjectiveService = fos;
        this.coreService = cs;
        // IMPORTANT: Get your application's ID
        this.appId = coreService.getAppId("org.onosproject.app.p4dlbs");
    }

    public void installStateTableRule(DeviceId deviceId) {
        // --- 1. BUILD THE P4 ACTION (TrafficTreatment) ---
        // P4 Action: IngressControl.update_state_table()

        // NOTE: If your action has parameters, you must add them here.
        // Example with a single parameter 'new_state':
        // PiActionParam new_state_param = new PiActionParam(
        //         PiActionParamId.of("new_state"), new_state_value);

        PiAction piAction = PiAction.builder()
                .withId(UPDATE_ACTION_ID)
                // .withParameter(new_state_param) // Add if you have parameters
                .build();

        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .piTableAction(piAction)
                .build();

        // --- 2. BUILD THE MATCH FIELDS (TrafficSelector) ---
        // Match fields from your p4_dlbs.p4info.txt:
        // hdr.ipv4.srcAddr, hdr.ipv4.dstAddr, hdr.tcp.srcPort, hdr.tcp.dstPort

        // Match: All fields must be matched as EXACT, as per p4_dlbs.p4info.txt
        TrafficSelector selector1 = DefaultTrafficSelector.builder()
                // Match 10.0.0.1 (Source IPv4)
                .matchIPSrc(IpPrefix.valueOf("10.0.0.1/32"))
                // Match 10.0.0.2 (Destination IPv4)
                .matchIPDst(IpPrefix.valueOf("10.0.0.2/32"))
                // Match TCP Protocol (hdr.ipv4.protocol)
                .matchIPProtocol(IPv4.PROTOCOL_TCP)
                // Match TCP Source Port 5000 (hdr.tcp.srcPort)
                .matchTcpSrc(TpPort.tpPort(5000))
                // Match TCP Destination Port 80 (hdr.tcp.dstPort)
                .matchTcpDst(TpPort.tpPort(80))
                .build();

        // --- 3. BUILD THE FORWARDING OBJECTIVE ---
        ForwardingObjective forwardingObjective1 = DefaultForwardingObjective.builder()
                .withSelector(selector1)
                .withTreatment(treatment)
                .withPriority(500) // Choose an appropriate priority
                .fromApp(appId)
                .makePermanent()
                .withFlag(ForwardingObjective.Flag.SPECIFIC)
                .add(); // Operation: ADD

        // Match: All fields must be matched as EXACT, as per p4_dlbs.p4info.txt
        TrafficSelector selector2 = DefaultTrafficSelector.builder()
                // Match 10.0.0.1 (Source IPv4)
                .matchIPSrc(IpPrefix.valueOf("10.0.0.1/32"))
                // Match 10.0.0.2 (Destination IPv4)
                .matchIPDst(IpPrefix.valueOf("10.0.0.2/32"))
                // Match TCP Protocol (hdr.ipv4.protocol)
                .matchIPProtocol(IPv4.PROTOCOL_TCP)
                // Match TCP Source Port 5000 (hdr.tcp.srcPort)
                .matchTcpSrc(TpPort.tpPort(5000))
                // Match TCP Destination Port 80 (hdr.tcp.dstPort)
                .matchTcpDst(TpPort.tpPort(80))
                .build();

        // --- 3. BUILD THE FORWARDING OBJECTIVE ---
        ForwardingObjective forwardingObjective2 = DefaultForwardingObjective.builder()
                .withSelector(selector2)
                .withTreatment(treatment)
                .withPriority(500) // Choose an appropriate priority
                .fromApp(appId)
                .makePermanent()
                .withFlag(ForwardingObjective.Flag.SPECIFIC)
                .add(); // Operation: ADD

        // --- 4. SUBMIT THE OBJECTIVE ---
        flowObjectiveService.forward(deviceId, forwardingObjective1);
        flowObjectiveService.forward(deviceId, forwardingObjective2);

        System.out.println("Submitted Forwarding Objective to device " + deviceId);
    }
}