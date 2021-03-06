package gay.solonovamax.openjavadoc.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import gay.solonovamax.openjavadoc.repository.Storage;
import gay.solonovamax.openjavadoc.repository.LocalStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reactor.test.StepVerifier;

class JavadocExtractorTest {
  private static final String MAVEN_REPO = "https://repo.maven.apache.org/maven2/";

  @Test
  void test(@TempDir Path root, @TempDir Path javadoc) throws IOException {
    JavadocDownloader downloader = new JavadocDownloader(javadoc, MAVEN_REPO);
    Storage storage = new LocalStorage(root);
    JavadocExtractor extractor = new JavadocExtractor(downloader, storage);

    StepVerifier.create(
            storage.find("com.github.spotbugs", "spotbugs-annotations", "3.1.0-RC3", "index.html"))
        .expectComplete()
        .verify();
    File downloaded =
        extractor
            .extract("com.github.spotbugs", "spotbugs-annotations", "3.1.0-RC3", "index.html")
            .block();
    assertTrue(downloaded.isFile());
    StepVerifier.create(
            storage.find("com.github.spotbugs", "spotbugs-annotations", "3.1.0-RC3", "index.html"))
        .expectNextMatches(file -> file.equals(downloaded))
        .expectComplete()
        .verify();
  }
}
