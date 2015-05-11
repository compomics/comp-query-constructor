package net.sourceforge.squirrel_sql.plugins.compomics;

import com.google.common.base.Joiner;

import java.util.*;

/**
 * This class constructs a query with the given select clause, parameters (in, like, from, to and between parts) and
 * order by clause(s).
 * <p/>
 * Created by Niels Hulstaert on 30/04/15.
 */
public class QueryConstructor {

    private static final String QUERY_END = ";";
    private static final String SPACE = " ";
    private static final String SELECT = "select ";
    private static final String WHERE = "where ";
    private static final String FROM = "from ";
    private static final String IN_OPEN = "in ('";
    private static final String IN_CLOSE = "')";
    private static final String LIKE = "like '%";
    private static final String BETWEEN = "between '";
    private static final String AND = "and ";
    private static final String AND_BETWEEN = "' and '";
    private static final String ANY_CLOSE = "%'";
    private static final String EQUALS_SIGN = " = '";
    private static final String FROM_SIGN = " >= '";
    private static final String TO_SIGN = " <= '";
    private static final String ORDER = "order by ";

    /**
     * The query input needed to construct the query.
     */
    private QueryInput queryInput;

    public QueryConstructor(QueryInput queryInput) {
        this.queryInput = queryInput;
    }

    /**
     * Construct the query.
     *
     * @return the query String
     */
    public String construct() {
        StringBuilder query = new StringBuilder();

        query.append(SELECT);
        query.append(queryInput.getSelectClause());
        query.append(System.lineSeparator());
        query.append(FROM);
        query.append(queryInput.getTable());
        query.append(System.lineSeparator());
        if (!queryInput.getEqualsParams().isEmpty() || !queryInput.getLikeParams().isEmpty() || !queryInput.getFromParams().isEmpty() || !queryInput.getToParams().isEmpty() || !queryInput.getBetweenParams().isEmpty()) {
            query.append(WHERE);
            query.append(constructEqualsParamsPart());
            if (!queryInput.getLikeParams().isEmpty()) {
                query.append(AND);
            }
            query.append(constructLikeParamsPart());
            if (!queryInput.getFromParams().isEmpty()) {
                query.append(AND);
            }
            query.append(constructFromParamsPart());
            if (!queryInput.getToParams().isEmpty()) {
                query.append(AND);
            }
            query.append(constructToParamsPart());
            if (!queryInput.getBetweenParams().isEmpty()) {
                query.append(AND);
            }
            query.append(constructBetweenParamsPart());
        }
        if (!queryInput.getOrderParams().isEmpty()) {
            query.append(constructOrderParamsPart());
        }
        query.append(QUERY_END);

        return query.toString();
    }

    /**
     * Construct the "equals" parameters part. If there's one parameter, use a "columnName = 'param'" construction, else
     * use "columnName IN('param1', 'param2', 'param3', ...)".
     *
     * @return
     */
    private String constructEqualsParamsPart() {
        Joiner outerJoiner = Joiner.on(AND);
        List<String> buildedParts = new ArrayList<String>();

        for (Map.Entry<String, List<String>> entry : queryInput.getEqualsParams().entrySet()) {
            StringBuilder buildedPart = new StringBuilder();
            String column = entry.getKey();
            List<String> params = entry.getValue();

            buildedPart.append(column).append(SPACE);
            if (params.size() == 1) {
                buildedPart.append(EQUALS_SIGN).append(params.get(0)).append("'").append(System.lineSeparator());
            } else {
                buildedPart.append(IN_OPEN);
                Joiner innerJoiner = Joiner.on("', '");
                buildedPart.append(innerJoiner.join(params)).append(IN_CLOSE).append(System.lineSeparator());
            }
            buildedParts.add(buildedPart.toString());
        }

        return outerJoiner.join(buildedParts);
    }

    /**
     * Construct the "like" parameters part. If there's one parameter, use a "like '%param%'" construction, else use
     * "columName like '%param1%' or columnName like '%param1%'".
     *
     * @return
     */
    private String constructLikeParamsPart() {
        Joiner outerJoiner = Joiner.on(AND);
        List<String> buildedParts = new ArrayList<String>();

        for (Map.Entry<String, List<String>> entry : queryInput.getLikeParams().entrySet()) {
            StringBuilder buildedPart = new StringBuilder();
            String column = entry.getKey();
            List<String> params = entry.getValue();

            buildedPart.append(column).append(SPACE);
            buildedPart.append(LIKE);
            Joiner innerJoiner = Joiner.on("%' or " + column + " like '%");
            buildedPart.append(innerJoiner.join(params)).append(ANY_CLOSE).append(System.lineSeparator());
            buildedParts.add(buildedPart.toString());
        }

        return outerJoiner.join(buildedParts);
    }

    /**
     * Construct the "from" part, e.g. "columnName >= 'param'".
     *
     * @return
     */
    private String constructFromParamsPart() {
        Joiner joiner = Joiner.on(AND);

        List<String> buildedParts = new ArrayList<String>();

        for (Map.Entry<String, String> entry : queryInput.getFromParams().entrySet()) {
            StringBuilder buildedPart = new StringBuilder();
            String column = entry.getKey();
            String param = entry.getValue();
            buildedPart.append(column).append(FROM_SIGN).append(param).append("'").append(System.lineSeparator());
            buildedParts.add(buildedPart.toString());
        }

        return joiner.join(buildedParts);
    }

    /**
     * Construct the "from" part, e.g. "columnName <= 'param'".
     *
     * @return
     */
    private String constructToParamsPart() {
        Joiner joiner = Joiner.on(AND);

        List<String> buildedParts = new ArrayList<String>();

        for (Map.Entry<String, String> entry : queryInput.getToParams().entrySet()) {
            StringBuilder buildedPart = new StringBuilder();
            String column = entry.getKey();
            String param = entry.getValue();
            buildedPart.append(column).append(TO_SIGN).append(param).append("'").append(System.lineSeparator());
            buildedParts.add(buildedPart.toString());
        }

        return joiner.join(buildedParts);
    }

    /**
     * Construct the "between" part, e.g. "columnName between 'param1' and 'param2'".
     *
     * @return
     */
    private String constructBetweenParamsPart() {
        Joiner joiner = Joiner.on(AND);

        List<String> buildedParts = new ArrayList<String>();

        for (Map.Entry<String, List<String>> entry : queryInput.getBetweenParams().entrySet()) {
            StringBuilder buildedPart = new StringBuilder();
            String column = entry.getKey();
            List<String> params = entry.getValue();

            buildedPart.append(column).append(SPACE);
            if (params.size() != 2) {
                throw new IllegalArgumentException("A between query part should have exactly 2 parameters.");
            }
            buildedPart.append(BETWEEN).append(params.get(0)).append(AND_BETWEEN).append(params.get(1)).append("'").append(System.lineSeparator());
            buildedParts.add(buildedPart.toString());
        }

        return joiner.join(buildedParts);
    }

    /**
     * Construct the "order" part, e.g. "order by columnName1, columnName2, ...".
     *
     * @return
     */
    private String constructOrderParamsPart() {
        Joiner joiner = Joiner.on(", ");

        List<String> buildedParts = new ArrayList<String>();

        for (Map.Entry<String, SortListModel.Sorting> entry : queryInput.getOrderParams().entrySet()) {
            StringBuilder buildedPart = new StringBuilder();
            String column = entry.getKey();
            SortListModel.Sorting sorting = entry.getValue();

            buildedPart.append(column).append(SPACE).append(sorting.toString());
            buildedParts.add(buildedPart.toString());
        }

        String joined = joiner.join(buildedParts);
        if (joined.isEmpty()) {
            return joined;
        } else {
            return ORDER + joined;
        }
    }

    public static void main(String[] args) {
        String selectClause = "test1, test2, test3";

        String tableName = "testTableName";

        Map<String, List<String>> plainParams = new HashMap<String, List<String>>();
        List<String> plainParams1 = Arrays.asList("p11", "p12");
        plainParams.put("plain1", plainParams1);
        List<String> plainParams2 = Arrays.asList("p21", "p22");
        plainParams.put("plain2", plainParams2);
        List<String> plainParams3 = Arrays.asList("p3");
        plainParams.put("plain3", plainParams3);

        Map<String, List<String>> anyParams = new HashMap<String, List<String>>();
        List<String> anyParams1 = Arrays.asList("a11", "a12");
        anyParams.put("any1", anyParams1);
        List<String> anyParams2 = Arrays.asList("a21", "a22");
        anyParams.put("any2", anyParams2);
        List<String> anyParams3 = Arrays.asList("a3");
        anyParams.put("any3", anyParams3);

        Map<String, String> fromParams = new HashMap<String, String>();
//        fromParams.put("from1", "f1");
//        fromParams.put("from2", "f2");

        Map<String, String> toParams = new HashMap<String, String>();
//        toParams.put("to1", "t1");
//        toParams.put("to2", "t2");

        Map<String, List<String>> betweenParams = new HashMap<String, List<String>>();
        List<String> betweenParams1 = Arrays.asList("b11", "b12");
        betweenParams.put("between1", betweenParams1);
        List<String> betweenParams2 = Arrays.asList("b21", "b22");
        betweenParams.put("between2", betweenParams2);

        Map<String, SortListModel.Sorting> orderParams = new HashMap<String, SortListModel.Sorting>();
//        orderParams.put("order1", SortListModel.Sorting.ASC);
//        orderParams.put("order2", SortListModel.Sorting.DESC);

        QueryInput queryInput = new QueryInput();
        queryInput.setSelectClause(selectClause);
        queryInput.setTable(tableName);
        queryInput.setEqualsParams(plainParams);
        queryInput.setLikeParams(anyParams);
        queryInput.setFromParams(fromParams);
        queryInput.setToParams(toParams);
        queryInput.setBetweenParams(betweenParams);
        queryInput.setOrderParams(orderParams);

        QueryConstructor queryConstructor = new QueryConstructor(queryInput);
        System.out.print(queryConstructor.construct());
    }

}
