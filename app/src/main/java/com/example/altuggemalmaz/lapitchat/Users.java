package com.example.altuggemalmaz.lapitchat;

public class Users {

    /** This class will be used to retrieve the user data on the FireBase Database **/
    /** The global variables should match with the DataBase Variables
     * That is why this is a Java Class and simply it doesn't have a UI
     * **/

    //These are the fields that we want to receive from the dataBase
    public String name;
    public String image;
    public String status;

    //Without this the app may crash
    public Users() {
    }

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
