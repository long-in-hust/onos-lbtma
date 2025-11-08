package org.onosproject.pipelines.p4dlbs.pipeconf;

import com.google.common.collect.ImmutableList;
import org.onosproject.core.CoreService;
// import org.onosproject.net.behaviour.inbandtelemetry.IntProgrammable;
import org.onosproject.net.behaviour.Pipeliner;
import org.onosproject.net.device.PortStatisticsDiscovery;
import org.onosproject.net.pi.model.DefaultPiPipeconf;
import org.onosproject.net.pi.model.PiPipeconf;
import org.onosproject.net.pi.model.PiPipeconfId;
// import org.onosproject.net.pi.model.PiPipelineInterpreter;
import org.onosproject.net.pi.model.PiPipelineModel;
import org.onosproject.net.pi.service.PiPipeconfService;
import org.onosproject.p4runtime.model.P4InfoParser;
import org.onosproject.p4runtime.model.P4InfoParserException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.net.URL;
import java.util.Collection;

import static org.onosproject.net.pi.model.PiPipeconf.ExtensionType.BMV2_JSON;
import static org.onosproject.net.pi.model.PiPipeconf.ExtensionType.P4_INFO_TEXT;

/**
 * Component that produces and registers the basic pipeconfs when loaded.
 */
@Component(immediate = true)
public final class PipeconfLoader {

    private static final String APP_NAME = "org.onosproject.pipelines.p4dlbs";
    private static final PiPipeconfId p4_dlbs_PIPECONF_ID = new PiPipeconfId("org.onosproject.pipelines.p4dlbs");
    private static final String p4_dlbs_JSON_PATH = "/output/p4_dlbs.json";
    private static final String p4_dlbs_P4INFO = "/output/p4_dlbs.p4info.txt";

    public static final PiPipeconf p4_dlbs_PIPECONF = buildStmPipeconf();

    private static final Collection<PiPipeconf> ALL_PIPECONFS = ImmutableList.of(p4_dlbs_PIPECONF);

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private PiPipeconfService piPipeconfService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private CoreService coreService;

    @Activate
    public void activate() {
        coreService.registerApplication(APP_NAME);
        // Registers all pipeconf at component activation.
        ALL_PIPECONFS.forEach(piPipeconfService::register);
    }

    @Deactivate
    public void deactivate() {
        ALL_PIPECONFS.stream().map(PiPipeconf::id).forEach(piPipeconfService::unregister);
    }

    private static PiPipeconf buildStmPipeconf() {
        final URL jsonUrl = PipeconfLoader.class.getResource(p4_dlbs_JSON_PATH);
        final URL p4InfoUrl = PipeconfLoader.class.getResource(p4_dlbs_P4INFO);

        return DefaultPiPipeconf.builder()
                .withId(p4_dlbs_PIPECONF_ID)
                .withPipelineModel(parseP4Info(p4InfoUrl))
                // .addBehaviour(PiPipelineInterpreter.class, InterpreterImpl.class)
                .addBehaviour(Pipeliner.class, PipelinerImpl.class)
                .addBehaviour(PortStatisticsDiscovery.class, PortStatisticsDiscoveryImpl.class)
                .addExtension(P4_INFO_TEXT, p4InfoUrl)
                .addExtension(BMV2_JSON, jsonUrl)
                // Put here other target-specific extensions,
                // e.g. Tofino's bin and context.json.
                .build();
    }

    private static PiPipelineModel parseP4Info(URL p4InfoUrl) {
        try {
            return P4InfoParser.parse(p4InfoUrl);
        } catch (P4InfoParserException e) {
            throw new IllegalStateException(e);
        }
    }
}
