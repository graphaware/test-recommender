package com.graphaware.estest.recommender;

public class RecommendationReduced {

    private long nodeId;
    private String objectId;
    private String item;
    private float score;

    public RecommendationReduced() {
    }

    public RecommendationReduced(long nodeId, long objectId, String item, float score) {
        this.objectId = String.valueOf(objectId);
        this.item = item;
        this.score = score;
        this.nodeId = nodeId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public long getNodeId() {
        return nodeId;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

}
