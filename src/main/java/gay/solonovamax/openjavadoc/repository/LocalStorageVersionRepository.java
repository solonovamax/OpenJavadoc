package gay.solonovamax.openjavadoc.repository;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;


class LocalStorageVersionRepository implements VersionRepository {
    
    @NonNull
    private final Path root;
    
    LocalStorageVersionRepository(@NonNull Path root) {
        this.root = Objects.requireNonNull(root);
    }
    
    @Override
    public Mono<ArtifactVersion> findLatest(String groupId, String artifactId) {
        File dir = root.resolve(groupId).resolve(artifactId).toFile();
        if (!dir.isDirectory()) {
            return Mono.empty();
        }
        
        Optional<DefaultArtifactVersion> found =
                Arrays.stream(dir.listFiles())
                      .filter(File::isDirectory)
                      .map(File::getName)
                      .map(DefaultArtifactVersion::new)
                      .sorted(Comparator.reverseOrder())
                      .findFirst();
        return Mono.justOrEmpty(found);
    }
    
    @Override
    public Flux<ArtifactVersion> list(String groupId, String artifactId) {
        throw new UnsupportedOperationException();
    }
}
