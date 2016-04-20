package com.graphaware.estest.recommender;

import com.graphaware.reco.generic.engine.RecommendationEngine;
import com.graphaware.reco.neo4j.engine.CypherEngine;
import com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingRecommendationEngine;

import java.util.Arrays;
import java.util.List;
import org.neo4j.graphdb.Node;

public class MovieRecommendationEngine extends Neo4jTopLevelDelegatingRecommendationEngine {

    public MovieRecommendationEngine() {
        super();
    }

    @Override
    public String name() {
        return "movieRecommendationEngine";
    }

    @Override
    protected List<RecommendationEngine<Node, Node>> engines() {
        final CypherEngine cypherEngine = new CypherEngine("reco",
                "MATCH (input:User) WHERE id(input) = {id}\n"
                + "MATCH p=(input)-[r:LIKES]->(movie)<-[r2:LIKES]-(other)\n"
                + "WITH other, collect(p) as paths\n"
                + "WITH other, reduce(x=0, p in paths | x + reduce(i=0, r in rels(p) | i+r.rating)) as score\n"
                + "WITH other, score\n"
                + "ORDER BY score DESC\n"
                + "MATCH (other)-[:LIKES]->(reco)\n"
                + "RETURN reco\n"
                + "LIMIT 500");
        return Arrays.<RecommendationEngine<Node, Node>>asList(cypherEngine);
    }
}
