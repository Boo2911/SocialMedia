package com.example.socialmedia.Models;

public class FriendsModel {
    private String followedBy;
    private long followedAt;

    public FriendsModel() {
    }

    public FriendsModel(String followedBy, long followAt) {
        this.followedBy = followedBy;
        this.followedAt = followAt;
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(long followedAt) {
        this.followedAt = followedAt;
    }
}
