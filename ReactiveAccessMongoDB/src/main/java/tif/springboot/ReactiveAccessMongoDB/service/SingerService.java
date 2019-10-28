package tif.springboot.ReactiveAccessMongoDB.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tif.springboot.ReactiveAccessMongoDB.bean.Image;
import tif.springboot.ReactiveAccessMongoDB.bean.Singer;
import tif.springboot.ReactiveAccessMongoDB.dao.SingerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

//    /*
//     * Find singer by singer.id
//     */
//    public Mono<Singer> findSingerById(Mono<String> id){
//        return singerRepository.findBySingerId(id);
//        //.log("SingerService.findBySingerId()");
//    }



    /*
     * Find singer's avatar
     */
    public Mono<Resource> findSingerAvatar(String filename){
        return Mono.fromSupplier(()-> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/avatar/" + filename))
                                                    .log("SingerServer.findSingerAvatar");
    }






    /*
     * Delete singer
     */
    public Mono<Void> deleteSinger(String id){
        Mono<Void> deleteDatabaseSinger = singerRepository.findById(id)//.findById(id)
                .log("SingerService.deleteSinger-findInDB")
                .flatMap(singerRepository::delete)
                .log("SingerService.deleteSinger-deleteFromDB");


        //delete file - Avatar
        Mono<Object> deleteAvatar = Mono.fromRunnable( () -> {
//            singerRepository.findById(id).subscribe(v -> System.out.println(v.getAvatar().getName()));
            singerRepository.findById(id).subscribe(v -> {
                try {
                    Files.deleteIfExists(Paths.get(UPLOAD_ROOT + "/avatar/", v.getAvatar().getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }).log("SingerService.deleteSinger-deleteAvatar");
//
//        //should invoke  function
//        Flux<Object> deleteAlbums;
//



        //Not interested in the results, so append a then()
        return Mono.when(deleteDatabaseSinger, deleteAvatar)  //, deleteAlbums);
                    .log("SingerService.deleteSinger-when")
                    .then()
                    .log("SingerService.deleteSinger-done");


    }





}
