package tif.springboot.ReactiveAccessMongoDB.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tif.springboot.ReactiveAccessMongoDB.bean.Image;
import tif.springboot.ReactiveAccessMongoDB.bean.Singer;
import tif.springboot.ReactiveAccessMongoDB.dao.SingerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class SingerService {

    @Value("${my_uploaded_folder}")
    private String UPLOAD_ROOT;

    private ResourceLoader resourceLoader;
    private SingerRepository singerRepository;

    public SingerService(ResourceLoader resourceLoader, SingerRepository singerRepository){
        this.resourceLoader = resourceLoader;
        this.singerRepository = singerRepository;
    }

    /*
     * Find all singer
     */
    public Flux<Singer> findAllSingers(){
        return singerRepository.findAll()
               .log("SingerService.findAllSingers");
    }



    /*
     * Display a image (avatar) AKA find singer's avatar
     */
    public Mono<Resource> findSingerAvatar(String filename){
        return Mono.fromSupplier(()-> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/avatar/" + filename))
                                                    .log("SingerServer.findSingerAvatar");
    }



    /*
     * Delete a singer
     */
    public Mono<Void> deleteSinger(String id){
        Mono<Void> deleteDatabaseSinger = singerRepository.findById(id)
                .log("SingerService.deleteSinger-findInDB")
                .flatMap(singerRepository::delete)
                .log("SingerService.deleteSinger-deleteFromDB");

        //delete file - Avatar
        Mono<Object> deleteAvatar = Mono.fromRunnable( () -> {
//            singerRepository.findById(id).subscribe(v -> System.out.println(v.getAvatar().getName()));
            singerRepository.findById(id).subscribe(singer -> {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_ROOT + "/avatar/", singer.getAvatar().getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }).log("SingerService.deleteSinger-deleteAvatar");

//        //should invoke deleteAlbum function
//        Flux<Object> deleteAlbums;
//CODE HERE ------------------------------------------


        //Not interested in the results, so append a then()
        return Mono.when(deleteAvatar)  //, deleteAlbums);
                    .log("SingerService.deleteSinger-when")
                //Q: deleteAvatar and deleteAlbums need to invoke singerRepository.findById(id)
                //Q: not sure if database deleted first, would it effect another 2 functions
                    .then(deleteDatabaseSinger)
                    .log("SingerService.deleteSinger-done");
    }



    /*
     * Create a singer
     */
    public Mono<Void> createSinger(Singer singer, Mono<FilePart> fileMono) {
        return fileMono.log("SingerService.createSinger-file").flatMap(file -> {
            Mono<Singer> saveDatabaseSinger = singerRepository.save(new Singer(UUID.randomUUID().toString(), singer.getFirstName(), singer.getLastName(), new Image(UUID.randomUUID().toString(), file.filename(), "Avatar" ), new ArrayList<Image>()));;

            Mono<Void> copyFile = Mono.just(Paths.get(UPLOAD_ROOT + "/avatar/", file.filename()).toFile())
                                        .log("SingerService.createSinger-picktarget")
                                        .map(destFile -> {
                                            try{
                                                destFile.createNewFile();
                                                return destFile;
                                            } catch (IOException e){
                                                throw new RuntimeException(e);
                                            }
                                        })
                                        .log("SingerService.createSinger-newFile")
                                        .flatMap(file::transferTo)
                                        .log("SingerService.createSinger-copy");

            return Mono.when(saveDatabaseSinger, copyFile)
                    .log("SingerService.createSinger=when");
        }).log("SingerService.createSinger-flatMap")
                .then()
                .log("SingerService.createSinger-done");

    }



    /*
     * Find all albums
     *
     */
    public Mono<Singer> findSingerById(String id) {
       return singerRepository.findById(id).log("SingerSerivce.findSingerById");
    }



    /*
     * Display a image (album)
     */
    public Mono<Resource> findOneAlbum(String filename){
        return Mono.fromSupplier(()-> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/album/" + filename))
                .log("SingerServer.findOneAlbum");
    }



    /*
     * Delete a album
     *
     */
    public Mono<Void> deleteAlbum(String singerId, String albumName) {

        Mono<Object> deleteFile = Mono.fromRunnable(()->{
            //Delete DB not working
            singerRepository.findById(singerId).subscribe(singer -> {
                singerRepository.deleteById(singerId);
                if(singer.getAlbums().removeIf(a->a.getName().equals(albumName))){
                    System.out.println("Delete Singer: " + singerId + "---------------------------------------------------------------------");
                    Singer newS = new Singer(singer.getId(), singer.getFirstName(), singer.getLastName(), singer.getAvatar(), singer.getAlbums());
                    System.out.println(newS.toString());
//                singerRepository.save(newS);
                };

            });

            //Delete File Works
            try {
                Files.deleteIfExists(Paths.get(UPLOAD_ROOT + "/album/", albumName));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
                .log("deleteImage-file");


        return Mono.when( deleteFile)
                    .log("deleteAlbum-when")
                    .then()
                    .log("deleteAlbum-done");
    }
}
