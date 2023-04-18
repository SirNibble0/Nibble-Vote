package com.example.nibblevote.model;

public class UserVoteModel {
    private String finish, deviceIp, pos, id;

    public UserVoteModel() {
    }

    public UserVoteModel(String finish, String deviceIp, String pos, String id) {
        this.finish = finish;
        this.deviceIp = deviceIp;
        this.pos = pos;
        this.id = id;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
}
