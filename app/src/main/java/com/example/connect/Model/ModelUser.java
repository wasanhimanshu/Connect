package com.example.connect.Model;

public class ModelUser {
    private String name;
    private String email;
    private String phone;
    private String image_url;
    private String uid;
    private String bio;
    private String status;

    public ModelUser(){}

    public ModelUser(String name,String email,String phn,String image_url,String uid, String bio,String status){
        this.name=name;
        this.email=email;
        this.phone=phn;
        this.image_url=image_url;
        this.uid=uid;
        this.bio=bio;
        this.status=status;
    }

    public String getUid() {
        return uid;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
