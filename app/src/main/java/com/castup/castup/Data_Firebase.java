package com.castup.castup;

public class Data_Firebase {

    String surname , status ,email,fullname,barthday,
            city,gender,country,langudage,address,myself,
            coverimage,ProfileImages,phone,token;

    public Data_Firebase() {
    }

    public Data_Firebase(String surname, String status, String email, String fullname, String barthday, String city, String gender, String country, String langudage, String address, String myself, String coverimage, String profileImages, String phone, String token) {
        this.surname = surname;
        this.status = status;
        this.email = email;
        this.fullname = fullname;
        this.barthday = barthday;
        this.city = city;
        this.gender = gender;
        this.country = country;
        this.langudage = langudage;
        this.address = address;
        this.myself = myself;
        this.coverimage = coverimage;
        ProfileImages = profileImages;
        this.phone = phone;
        this.token = token;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBarthday() {
        return barthday;
    }

    public void setBarthday(String barthday) {
        this.barthday = barthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLangudage() {
        return langudage;
    }

    public void setLangudage(String langudage) {
        this.langudage = langudage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMyself() {
        return myself;
    }

    public void setMyself(String myself) {
        this.myself = myself;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(String coverimage) {
        this.coverimage = coverimage;
    }

    public String getProfileImages() {
        return ProfileImages;
    }

    public void setProfileImages(String profileImages) {
        ProfileImages = profileImages;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
