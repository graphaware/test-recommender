package com.graphaware.estest.recommender;

import com.graphaware.common.util.IterableUtils;
import com.graphaware.reco.generic.config.KeyValueConfig;
import com.graphaware.reco.generic.config.SimpleConfig;
import com.graphaware.reco.generic.engine.TopLevelRecommendationEngine;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.reco.generic.web.ConfigParser;
import com.graphaware.reco.generic.web.KeyValueConfigParser;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationController {

    private final GraphDatabaseService database;
    private final ConfigParser<KeyValueConfig> parser = new KeyValueConfigParser(":");

    private final TopLevelRecommendationEngine<Node, Node> recoEngine = new MovieRecommendationEngine();

    @Autowired
    public RecommendationController(GraphDatabaseService database) {
        this.database = database;
    }

    @RequestMapping(value = "/recommendation/movie/user/{objectId}", method = RequestMethod.GET)
    public List<MovieRecommendation> recommendPosts(
            @PathVariable(value = "objectId") long userId, @RequestParam(defaultValue = "10") int limit) {
        List<Recommendation<Node>> recommendation;
        try (Transaction tx = database.beginTx()) {
            Node user = findUserById(userId);
            recommendation = recoEngine.recommend(user, new SimpleConfig(limit));
            final List<MovieRecommendation> result = convert(recommendation, "objectId");
            return result;
        }
    }

    @RequestMapping(value = "/recommendation/movie/filter/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public List<RecommendationReduced> filter(@PathVariable long userId, @RequestParam("ids") String[] ids, @RequestParam(defaultValue = "10") int limit, @RequestParam("keyProperty") String keyProperty, @RequestParam(defaultValue = "") String config) {
        try (Transaction tx = database.beginTx()) {
            final List<Recommendation<Node>> recommendations = recoEngine.recommend(findUserById(userId), parser.produceConfig(limit, config));
            return convert(recommendations, ids, keyProperty);
        }
    }

    private List<MovieRecommendation> convert(List<Recommendation<Node>> recommendations, String keyProperty) {
        List<MovieRecommendation> result = new LinkedList<>();

        recommendations.stream().forEach((recommendation)
                -> {
            result.add(new MovieRecommendation((Long) recommendation.getItem().getProperty(keyProperty), (String) recommendation.getItem().getProperty("title"), recommendation.getScore()));
        });

        return result;
    }

    private Node findUserById(long userId) {
        return IterableUtils.getSingle(database.findNodes(DynamicLabel.label("User"), "objectId", userId), "User with name " + userId + " does not exist.");
    }

    private List<RecommendationReduced> convert(List<Recommendation<Node>> recommendations, String[] ids, String keyProperty) {
        List<RecommendationReduced> result = new LinkedList<>();
        List<String> asList = Arrays.asList(ids);

        for (Recommendation<Node> recommendation : recommendations) {
            if (asList.contains(String.valueOf(recommendation.getItem().getProperty(keyProperty)))) {
                result.add(new RecommendationReduced(
                        recommendation.getItem().getId(),
                        (Long) recommendation.getItem().getProperty("objectId"),
                        String.valueOf(recommendation.getItem().getProperty(keyProperty)),
                        recommendation.getScore().getTotalScore()));
            }
        }

        return result;
    }
}
