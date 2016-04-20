package com.graphaware.estest.recommender;

import com.graphaware.reco.generic.result.Score;

public class MovieRecommendation {

    private Score score;
    private long objectId;
    private String title;

    public MovieRecommendation() {
    }
    

    public MovieRecommendation(long objectId, String title, Score score) {
        this.objectId = objectId;
        this.score = score;
        this.title = title;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
