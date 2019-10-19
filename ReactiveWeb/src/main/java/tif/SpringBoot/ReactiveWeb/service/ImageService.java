package tif.SpringBoot.ReactiveWeb.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tif.SpringBoot.ReactiveWeb.bean.Image;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ImageService {
    private static String UPLOAD_ROOT = "images";

    private final ResourceLoader resourceLoader;

    public ImageService (ResourceLoader resourceLoader){
        this.resourceLoader = resourceLoader;
    }

    /**
     * Pre-load some test images
     *
     * @return Spring Boot automatically
     * 			run after app context is loaded.
     *
     */
    @Bean
    CommandLineRunner setUp() throws IOException {
        return (arugs) -> {
            FileSystemUtils.deleteRecursively (new File(UPLOAD_ROOT));

            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            FileCopyUtils.copy("Test file1",
                    new FileWriter(UPLOAD_ROOT +
                            "/1.jpg"));

            FileCopyUtils.copy("Test file2",
                    new FileWriter(UPLOAD_ROOT +
                            "/2.jpg"));

            FileCopyUtils.copy("Test file3",
                    new FileWriter(UPLOAD_ROOT +
                            "/3.jpg"));

            FileCopyUtils.copy("Test file4",
                    new FileWriter(UPLOAD_ROOT +
                            "/4.jpg"));

        };
    }


    public Flux<Image> findAllImages(){
        try {
            return Flux.fromIterable(Files.newDirectoryStream(Paths.get("src/main/resources/" + UPLOAD_ROOT)))
                    .map(path -> new Image(path.hashCode(), path.getFileName().toString()));
        } catch(IOException e){
                return Flux.empty();
        }
    }


    public Mono<Resource> findOneImage(String filename){
        return Mono.fromSupplier(() -> resourceLoader.getResource("classpath:" + UPLOAD_ROOT + "/" + filename));
    }


    public Mono<Void> createImage(Flux<FilePart> files){
        return files.flatMap(file -> file.transferTo(Paths.get("src/main/resources/" + UPLOAD_ROOT, file.filename()).toFile())).then();
    }


    public Mono<Void> deleteImage(String filename){
        return Mono.fromRunnable( () -> {
           try{
               Files.deleteIfExists(Paths.get("src/main/resources/" + UPLOAD_ROOT, filename));
           }catch (IOException e){
               throw new RuntimeException(e);
           }
        });
    }
}
