package net.sourceforge.squirrel_sql.plugins.compomics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains all info
 * <p/>
 * Created by Niels Hulstaert on 4/05/15.
 */
public class QueryInput {

    /**
     * The select clause.
     */
    private String selectClause;
    /**
     * The table name.
     */
    private String table;
    /**
     * The Map of equals params (key: field name; value: the List of param values).
     */
    private Map<String, List<String>> equalsParams = new HashMap<String, List<String>>();
    /**
     * The Map of like params (key: field name; value: the List of param values).
     */
    private Map<String, List<String>> likeParams = new HashMap<String, List<String>>();
    /**
     * The Map of from params (key: column name; value: the param value).
     */
    private Map<String, String> fromParams = new HashMap<String, String>();
    /**
     * The Map of to params (key: column name; value: the param value).
     */
    private Map<String, String> toParams = new HashMap<String, String>();
    /**
     * The Map of between params (key: column name; value: the List of param values (should be exactly 2)).
     */
    private Map<String, List<String>> betweenParams = new HashMap<String, List<String>>();
    /**
     * The list of order field names.
     */
    private Map<String, SortListModel.Sorting> orderParams = new HashMap<String, SortListModel.Sorting>();

    public QueryInput() {
    }

    public String getSelectClause() {
        return selectClause;
    }

    public void setSelectClause(String selectClause) {
        this.selectClause = selectClause;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, List<String>> getEqualsParams() {
        return equalsParams;
    }

    public void setEqualsParams(Map<String, List<String>> equalsParams) {
        this.equalsParams = equalsParams;
    }

    public Map<String, List<String>> getLikeParams() {
        return likeParams;
    }

    public void setLikeParams(Map<String, List<String>> likeParams) {
        this.likeParams = likeParams;
    }

    public Map<String, String> getFromParams() {
        return fromParams;
    }

    public void setFromParams(Map<String, String> fromParams) {
        this.fromParams = fromParams;
    }

    public Map<String, String> getToParams() {
        return toParams;
    }

    public void setToParams(Map<String, String> toParams) {
        this.toParams = toParams;
    }

    public Map<String, List<String>> getBetweenParams() {
        return betweenParams;
    }

    public void setBetweenParams(Map<String, List<String>> betweenParams) {
        this.betweenParams = betweenParams;
    }

    public Map<String, SortListModel.Sorting> getOrderParams() {
        return orderParams;
    }

    public void setOrderParams(Map<String, SortListModel.Sorting> orderParams) {
        this.orderParams = orderParams;
    }
}
