package jp.skypencil.javadocky;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * This class is responsible to download javadoc.jar and unzip its contents onto {@link Storage}.
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
class JavadocExtractor {
    @NonNull
    private final JavadocDownloader downloader;

    @NonNull
    private final Storage storage;

    Mono<Void> extract(String groupId, String artifactId, String version) {
        return downloader.download(groupId, artifactId, version).flatMapMany(downloaded -> {
                if (!downloaded.isPresent()) {
                    String message = String.format("Javadoc.jar not found for %s:%s:%s", groupId, artifactId, version);
                    return Mono.error(new IllegalArgumentException(message));
                }
                return unzip(downloaded.get());
            }).flatMap(nameAndData -> {
                String name = nameAndData.getT1();
                Flux<ByteBuffer> data = nameAndData.getT2();
                return storage.write(groupId, artifactId, version, name, data);
            })
            .reduce((a,b) -> a);
    }

    Flux<Tuple2<String, Flux<ByteBuffer>>> unzip(File file) {
        ZipFile zip;
        try {
            zip = new ZipFile(file);
        } catch (IOException e) {
            return Flux.error(e);
        }
        // Flux.from invokes internal lambda immediately, so use Flux.create instead
        Flux<Tuple2<String, Flux<ByteBuffer>>> result = Flux.<Tuple2<String, Flux<ByteBuffer>>>create(emitter -> {
            AtomicInteger opened = new AtomicInteger(0);
            // When we use emitter (FluxSink), we need to consider how we should handle cancellation
            emitter.onCancel(() -> {
                if (emitter.isCancelled() && opened.get() > 0) {
                    try {
                        zip.close();
                    } catch (IOException e) {
                        log.warn("Failed to close ZIP input stream", e);
                    }
                }
            });

            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (!emitter.isCancelled() && entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                try {
                    InputStream input = zip.getInputStream(entry);
                    Tuple2<String, Flux<ByteBuffer>> tuple = handleEntry(entry, input);
                    opened.incrementAndGet();
                    tuple.getT2().doFinally(signal -> {
                        if (opened.decrementAndGet() == 0) {
                            // Closing ZipFile will close all InputStream created from it, so we need to wait until all InputStreams are closed
                            try {
                                zip.close();
                            } catch (IOException e) {
                                log.warn("Failed to close ZIP input stream", e);
                            }
                        }
                    });
                    emitter.next(tuple);
                } catch (IOException e) {
                    emitter.error(e);
                }
            }
            emitter.complete();
        });
        return result;
    }

    private Tuple2<String, Flux<ByteBuffer>> handleEntry(ZipEntry entry, InputStream input) throws IOException {
        String name = entry.getName();
        // Flux.from invokes internal lambda immediately, so use Flux.create instead
        Flux<ByteBuffer> flux = Flux.create(emitter -> {
            try {
                byte[] buffer = new byte[8 * 1024];
                int len;
                while (!emitter.isCancelled() && (len = input.read(buffer)) != -1) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(Arrays.copyOf(buffer, len));
                    emitter.next(byteBuffer);
                }
            } catch (IOException e) {
                emitter.error(e);
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    log.warn("Failed to close InputStream from ZIP file", e);
                }
            }
            emitter.complete();
        });
        return Tuples.of(name, flux);
    }
}