package gay.solonovamax.openjavadoc.controller;

import gay.solonovamax.openjavadoc.repository.ArtifactRepository;
import gay.solonovamax.openjavadoc.repository.VersionRepository;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Controller
class DocumentController {
    @NonNull
    private final VersionRepository versionRepo;
    
    @NonNull
    private final ArtifactRepository artifactRepo;
    
    @Autowired
    @ParametersAreNonnullByDefault
    DocumentController(VersionRepository versionRepo, ArtifactRepository artifactRepo) {
        this.versionRepo = Objects.requireNonNull(versionRepo);
        this.artifactRepo = Objects.requireNonNull(artifactRepo);
    }
    
    @Bean
    public RouterFunction<ServerResponse> routeForDoc() {
        return route(
                GET("/doc/{groupId}/{artifactId}"),
                req -> {
                    String groupId    = req.pathVariable("groupId");
                    String artifactId = req.pathVariable("artifactId");
                    
                    Flux<String>                    artifacts = artifactRepo.list(groupId);
                    Flux<? extends ArtifactVersion> versions  = versionRepo.list(groupId, artifactId);
                    return versionRepo
                            .findLatest(groupId, artifactId)
                            .flatMap(
                                    latestVersion -> {
                                        Map<String, Object> model = new HashMap<>();
                                        model.put("groupId", groupId);
                                        model.put("artifactId", artifactId);
                                        model.put("artifactIds", artifacts);
                                        model.put("version", latestVersion);
                                        model.put("versions", versions);
                                        return ok().contentType(MediaType.TEXT_HTML).render("doc", model);
                                    })
                            .switchIfEmpty(notFound().build());
                });
    }
}
