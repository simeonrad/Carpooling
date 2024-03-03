package com.telerikacademy.web.carpooling.models;

public class TravelCommentDto {
    private int travelId;
    private String comment;

    public TravelCommentDto() {

    }

    public int getTravelId() {
        return travelId;
    }

    public void setTravelId(int travelId) {
        this.travelId = travelId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
