package gay.solonovamax.openjavadoc.controller;

import gay.solonovamax.openjavadoc.repository.VersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;


@Controller
class BadgeController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @NonNull
    private final VersionRepository versionRepo;
    
    @Autowired
    BadgeController(@NonNull VersionRepository versionRepo) {
        this.versionRepo = Objects.requireNonNull(versionRepo);
    }
    
    @Bean
    public RouterFunction<ServerResponse> routeForBadge() {
        return route(
                GET("/badge/{groupId}/{artifactId}.{ext}"),
                req -> {
                    String ext = req.pathVariable("ext");
                    if (!ext.equals("png") && !ext.equals("svg")) {
                        return badRequest().body(Mono.just("Unsupported extention"), String.class);
                    }
                    
                    String groupId    = req.pathVariable("groupId");
                    String artifactId = req.pathVariable("artifactId");
                    log.debug("Got access to badge for {}:{}", groupId, artifactId);
                    
                    return versionRepo
                            .findLatest(groupId, artifactId)
                            .flatMap(
                                    latestVersion -> {
                                        URI shieldsUri =
                                                URI.create(
                                                        String.format(
                                                                "https://img.shields.io/badge/%s-%s-%s.%s",
                                                                escape(req.queryParam("label").orElse("javadoc")),
                                                                escape(latestVersion.toString()),
                                                                escape(req.queryParam("color").orElse("brightgreen")),
                                                                ext));
                                        return seeOther(shieldsUri).build();
                                    })
                            .switchIfEmpty(notFound().build());
                });
    }
    
    /**
     * Escape URI based on the rule described by <a href="https://shields.io/">shields.io</a>
     *
     * @param s target string
     *
     * @return escaped string
     */
    private String escape(String s) {
        return s.replace("-", "--").replace("_", "__").replace(" ", "_");
    }
}
