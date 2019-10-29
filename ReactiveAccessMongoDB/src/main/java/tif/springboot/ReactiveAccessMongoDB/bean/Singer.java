package tif.springboot.ReactiveAccessMongoDB.bean;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document
public class Singer {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    //private Flux<Image> alumsFlux;      //Wrong
    private List<Image> albums;     //Right

    private Image avatar;
    private String role = "singer";

    public Singer() {
        this.id = UUID.randomUUID().toString();
        this.avatar = new Image();
        this.albums = new ArrayList<Image>();
    }

    public Singer(String firstName, String lastName) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = new Image();
        this.albums = new ArrayList<Image>();
    }

    public Singer(String id, String firstName, String lastName, Image avatar, List<Image> albums) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "Singer{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", albums='" + albums.size() + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Image> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Image> albums) {
        this.albums = albums;
    }

    public Image getAvatar() {
        return avatar;
    }

    public void setAvatar(Image avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }
}
