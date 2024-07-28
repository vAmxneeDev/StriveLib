package it.vamxneedev.strivelib.utilities.database;

public class Query {
    private String query;

    public Query(String query){
        this.query = query;
    }

    public String getQuery(){
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}