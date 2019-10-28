package tif.springboot.ReactiveAccessMongoDB.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tif.springboot.ReactiveAccessMongoDB.bean.Singer;
import tif.springboot.ReactiveAccessMongoDB.service.SingerService;

import java.awt.*;
import java.io.IOException;

@Controller
public class SingerController {

    private static final String SINGER_BASE_PATH = "singer";
    private static final String IMAGE_BASE_PATH = "images";
    private static final String FILENAME = "{filename:.+}";
    private static final String SINGER_ID = "{id:.+}";

    private SingerService singerService;

    public SingerController(SingerService singerService){
        this.singerService = singerService;
    }

    @GetMapping("/")
    public Mono<String> index(Model model){
        model.addAttribute("singers", singerService.findAllSingers());
        return Mono.just("index");
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
    @RequestMapping(method = RequestMethod.POST, value=SINGER_BASE_PATH + "/" + SINGER_ID)
    public Mono<String> deleteFile(@PathVariable String id){
//    @RequestMapping(value = "/deleteSinger", method = RequestMethod.POST)
//    public Mono<String> deleteFile(@ModelAttribute("singer") Singer singer){

        //return singerService.deleteSinger().then(Mono.just("redirect:/"));

        // singerService.findSingerById(Mono.just(id)).subscribe(value -> singerService.deleteSinger(value).then());
        //return Mono.just("/");

        return singerService.deleteSinger(id).then(Mono.just("redirect:/"));
    }




    /*
     * Display list of albums by SINGER_ID
     */
    @GetMapping(value = "/album/" + SINGER_ID )
    public Mono<String> albumDisplay(Model model){
        //model.addAttribute("singers", singerService.findAllSingers());
        return Mono.just("index");
    }





}
