package tif.springboot.ReactiveAccessMongoDB.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tif.springboot.ReactiveAccessMongoDB.bean.Image;
import tif.springboot.ReactiveAccessMongoDB.bean.Singer;
import tif.springboot.ReactiveAccessMongoDB.service.SingerService;

import java.io.IOException;

@Controller
public class SingerController {

    private static final String SINGER_BASE_PATH = "singer";
    private static final String IMAGE_BASE_PATH = "images";
    private static final String ALBUM_BASE_PATH = "albums";
    private static final String FILENAME = "{filename:.+}";
    private static final String ID = "{id:.+}";

    private SingerService singerService;

    public SingerController(SingerService singerService){
        this.singerService = singerService;
    }


    /*
     * Displaying the list of singers ------ index.html
     */
    @GetMapping("/")
    public Mono<String> showSinger(Model model){
        //Object singers: contents all Singer objects find from MongoDB
        model.addAttribute("singers", singerService.findAllSingers());

        //Object singer: contents a single Singer object, preparing for the addASinger function
        model.addAttribute("newSinger", new Singer());

        return Mono.just("index");  //index.html
    }



    /*
     * Handler for displaying a single image on the web page
     */
    @GetMapping(value = IMAGE_BASE_PATH + "/avatar/" + FILENAME,produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> oneAvatarImage(@PathVariable String filename){
        return singerService.findSingerAvatar(filename)
                .map( resource -> {
                    try{
                        return ResponseEntity.ok()
                                                .contentLength(resource.contentLength())
                                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e){
                        return ResponseEntity.badRequest().body("Couldn't find " + filename + " => " + e.getMessage());
                    }
                });
    }



    /*
     * Delete a singer
     */
    @RequestMapping(method = RequestMethod.POST, value=SINGER_BASE_PATH + "/" + ID)
    public Mono<String> deleteSinger(@PathVariable String id){
        return singerService.deleteSinger(id).then(Mono.just("redirect:/"));
    }



    /*
     * Add a new singer
     */
    @PostMapping("/addSinger")
    public Mono<String> addSinger (@ModelAttribute("singer") Singer singer,
                                   // WARN: BindingResult *must* immediately follow the Command.
                                   // https://stackoverflow.com/a/29883178/1626026
                                   BindingResult bindingResult,
                                   @RequestPart(name="file") Mono<FilePart> file,
                                   Model model ) {
        System.out.println("SingerController.addASinger: form submission.");
        if( bindingResult.hasErrors()){
            return Mono.just("redirect:/");
        }
        return singerService.createSinger(singer, file).then(Mono.just("redirect:/"));

    }



    /*
     * Display list of albums by SINGER_ID
     * method = RequestMethod.POST,
     */
    @RequestMapping( value = ALBUM_BASE_PATH + "/" + ID)
    public Mono<String> showAlbums(@PathVariable(value = "id") String singerId, Model model){
        //Passing the {singer.id} to albums.html to display {singer.albums.name}
        model.addAttribute("singer", singerService.findSingerById(singerId));
        //Passing a new Image object to content the new album
        model.addAttribute("newAlbum", new Image());

        return Mono.just("albums");
        //Mono.just("albums") redirect view to albums.html
        //Mono.just("redirect:/" + ALBUM_BASE_PATH + "/" + singerId) redirect view to this method showAlbums

    }



    /*
     * Displaying a single album on the web page
     */
    @GetMapping(value = IMAGE_BASE_PATH + "/album/" + FILENAME,produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> oneAlbumImage(@PathVariable String filename){
        return singerService.findOneAlbum(filename)
                .map( resource -> {
                    try{
                        return ResponseEntity.ok()
                                .contentLength(resource.contentLength())
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e){
                        return ResponseEntity.badRequest().body("Couldn't find " + filename + " => " + e.getMessage());
                    }
                });
    }



    /*
     * Delete a album
     *
     */
    @RequestMapping(method = RequestMethod.POST, value=ALBUM_BASE_PATH + "/" + ID + "/" + FILENAME)
    public Mono<String> deleteAlbum(@PathVariable(value = "id") String singerId, @PathVariable(value = "filename") String albumName, Model model){
        System.out.println(singerId + "/" + albumName);
        return singerService.deleteAlbum(singerId, albumName).then(Mono.just("redirect:/" + ALBUM_BASE_PATH + "/" + singerId));



        //return Mono.just("/albums/" + singerId);
        //return singerService.deleteAlbum(singerId, albumName).then(Mono.just("/albums/" + singerId)); //albums.html

    }



}
