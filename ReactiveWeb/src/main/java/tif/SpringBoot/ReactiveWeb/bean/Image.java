package tif.SpringBoot.ReactiveWeb.bean;


public class Image {

    private int id;
    private String name;

    public Image(){
        this.id = 0;
        this.name = "init";
    }

    public Image(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
