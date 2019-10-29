package tif.springboot.ReactiveAccessMongoDB.service;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tif.springboot.ReactiveAccessMongoDB.bean.Image;
import tif.springboot.ReactiveAccessMongoDB.bean.Singer;

import java.util.ArrayList;
import java.util.List;

@Component
public class InitDatabase {

    @Bean
    CommandLineRunner init(MongoOperations operations){
      return args -> {
          operations.dropCollection(Image.class);

          Image image1989 = new Image("3", "1989.jpg", "Album");
          Image imageLoves = new Image("4", "Loves.jpg", "Album");
          Image imageRED = new Image("5", "RED.png", "Album");
          Image imagePrism = new Image("6", "Prism.jpg", "Album");
          Image imageRise = new Image("7", "Rise.png", "Album");

          List albumsTS = new ArrayList<Image>();
          albumsTS.add(image1989);
          albumsTS.add(imageLoves);
          albumsTS.add(imageRED);

          List albumsKP= new ArrayList<Image>();
          albumsKP.add(imagePrism);
          albumsKP.add(imageRise);


//          operations.insert(image1989);
//          operations.insert(imageLoves);
//          operations.insert(imagePrism);
//          operations.insert(imageRED);
//          operations.insert(imageRise);
//
//          operations.findAll(Image.class).forEach(image -> {
//              System.out.println(image.toString());
//          });


          operations.dropCollection(Singer.class);

          operations.insert(new Singer("1", "Taylor", "Swift", new Image("1", "TaylorSwift.jpg","Avatar"), albumsTS));
          operations.insert(new Singer("2", "Katy", "Perry", new Image("2", "KatyPerry.gif","Avatar"), albumsKP));

          operations.findAll(Singer.class).forEach(singer -> {
              System.out.println(singer.toString());
          });
      };
    };

}
