package gay.solonovamax.openjavadoc;

import gay.solonovamax.openjavadoc.repository.ArtifactRepository;
import gay.solonovamax.openjavadoc.repository.LocalStorage;
import gay.solonovamax.openjavadoc.repository.LocalStorageArtifactRepository;
import gay.solonovamax.openjavadoc.repository.Storage;
import gay.solonovamax.openjavadoc.service.JavadocDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootApplication
public class JavadockyApplication {
    private static final Logger logger = LoggerFactory.getLogger(JavadockyApplication.class);
    
    private static final String USER_HOME      = "user.home";
    private static final String JAVADOCKY_ROOT = ".javadocky";
    private static final String STORAGE_DIR    = "storage";
    /**
     * Name of directory to store downloaded javadoc.jar file.
     */
    private static final String JAVADOC_DIR    = "javadoc";
    
    public static void main(String[] args) {
        SpringApplication.run(JavadockyApplication.class, args);
    }
    
    @Bean
    public Storage localStorage() {
        Path home = Paths.get(System.getProperty(USER_HOME), JAVADOCKY_ROOT, STORAGE_DIR);
        home.toFile().mkdirs();
        logger.info("Making storage at {}", home.toFile().getAbsolutePath());
        return new LocalStorage(home);
    }
    
    @Bean
    public ArtifactRepository artifactRepository() {
        Path home = Paths.get(System.getProperty(USER_HOME), JAVADOCKY_ROOT, JAVADOC_DIR);
        home.toFile().mkdirs();
        logger.info("Making storage at {}", home.toFile().getAbsolutePath());
        return new LocalStorageArtifactRepository(home);
    }
    
    @Bean
    public JavadocDownloader javadocDownloader(@Value("${javadocky.maven.repository}") String repoURL) {
        Path home = Paths.get(System.getProperty(USER_HOME), JAVADOCKY_ROOT, JAVADOC_DIR);
        home.toFile().mkdirs();
        logger.info("Making javadoc storage at {}", home.toFile().getAbsolutePath());
        return new JavadocDownloader(home, repoURL);
    }
}
