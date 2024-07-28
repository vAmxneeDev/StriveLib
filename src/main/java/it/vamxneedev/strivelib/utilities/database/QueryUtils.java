package it.vamxneedev.strivelib.utilities.database;

import it.vamxneedev.strivelib.enums.DatabaseType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryUtils {
    public static String constructQueryTableCreate(String tableName, List<Pair<String, String>> columns, String primaryKey){
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
        for (Pair<String, String> pair : columns){
            query = query + "`" + pair.getLeft() + "` " + pair.getRight() +", ";
        }
        if (primaryKey != null){
            query = query + " PRIMARY KEY (`"+ primaryKey + "`)";
            query = query + ");";
        } else {
            query = query.substring(0, query.length()-2) + ");";
        }
        return query;
    }

    public static String constructQueryForeignKey(String tableName, String fk, String tableRef, String pk) {
        return "ALTER TABLE " + tableName + " ADD FOREIGN KEY (`" + fk + "`) REFERENCES `" + tableRef + "` (`" + pk + "`);";
    }

    public static String constructQueryRowGet(String table, String indexColumn, Object indexValue) {
        String query;
        if (indexValue instanceof Number){
            query = "SELECT * " +
                    "FROM `"+ table+ "` " +
                    "WHERE "+ indexColumn + " = " + indexValue + " " +
                    "LIMIT 1";
        } else {
            query = "SELECT * " +
                    "FROM `" + table + "` " +
                    "WHERE " + indexColumn + " = '" + indexValue.toString() + "' " +
                    "LIMIT 1";
        }
        return query;
    }

    public static String constructQueryRowsGet(String table, String indexColumn, Object indexValue, Object limit){
        String query;
        if (indexValue instanceof Number){
            query = "SELECT * " +
                    "FROM `"+table+"` " +
                    "WHERE `"+indexColumn+"` = " + indexValue + " ";
        } else {
            query = "SELECT * " +
                    "FROM `"+table+"` " +
                    "WHERE `"+indexColumn+"` = '" + indexValue.toString() + "' ";
        }
        if (!(limit.equals(0) || limit.equals("0"))){
            query = query + "LIMIT " + limit.toString() + "";
        }
        return query;
    }

    public static String constructQueryRowsGet(String table, String indexColumn, Object indexValue, Object limit, Map<String, Object> additionalConditions){
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT * FROM `").append(table).append("` WHERE `").append(indexColumn).append("` = ");
        if (indexValue instanceof Number){
            queryBuilder.append(indexValue);
        } else {
            queryBuilder.append("'").append(indexValue).append("'");
        }

        if (additionalConditions != null && !additionalConditions.isEmpty()) {
            for (Map.Entry<String, Object> entry : additionalConditions.entrySet()) {
                String column = entry.getKey();
                Object value = entry.getValue();
                queryBuilder.append(" AND `").append(column).append("` = ");
                if (value instanceof Number){
                    queryBuilder.append(value);
                } else {
                    queryBuilder.append("'").append(value).append("'");
                }
            }
        }

        if (!(limit.equals(0) || limit.equals("0"))){
            queryBuilder.append(" LIMIT ").append(limit);
        }

        return queryBuilder.toString();
    }

    public static String constructQueryRowRemove(String table, Pair pair, DatabaseType databaseType){
        String query;
        String column = (String)pair.getLeft();
        Object value = pair.getRight();
        if (value instanceof Number) {
            query = "DELETE FROM `" + table + "` "+
                    "WHERE `" + column + "` = " + value + " ";
        } else {
            query = "DELETE FROM `" + table + "` " +
                    "WHERE `" + column + "` = '" + value + "' ";
        }
        if (databaseType != DatabaseType.SQLITE){
            query = query + "LIMIT 1";
        }
        return query;
    }

    public static String constructQueryRowRemove(String table, String column, Object value, DatabaseType databaseType){
        String query;
        if (value instanceof Number) {
            query = "DELETE FROM `" + table + "` "+
                    "WHERE `" + column + "` = " + value + " ";
        } else {
            query = "DELETE FROM `" + table + "` " +
                    "WHERE `" + column + "` = '" + value + "' ";
        }
        if (databaseType != DatabaseType.SQLITE){
            query = query + "LIMIT 1";
        }
        return query;
    }

    public static String constructQueryRowsRemove(String table, String column, Object value, Object limit){
        String query;
        if (value instanceof Number) {
            query = "DELETE FROM `" + table + "` "+
                    "WHERE `" + column + "` = " + value + " ";
        } else {
            query = "DELETE FROM `" + table + "` " +
                    "WHERE `" + column + "` = '" + value + "' ";
        }
        if (!(limit.equals(0) || limit.equals("0"))){
            query = query + "LIMIT " + limit.toString() + "";
        }
        return query;
    }

    public static String constructQueryRowsRemove(String table, Pair pair, Object limit){
        String query;
        String column = (String)pair.getLeft();
        Object value = pair.getRight();
        if (value instanceof Number) {
            query = "DELETE FROM `" + table + "` "+
                    "WHERE `" + column + "` = " + value + " ";
        } else {
            query = "DELETE FROM `" + table + "` " +
                    "WHERE `" + column + "` = '" + value + "' ";
        }
        if (!(limit.equals(0) || limit.equals("0"))){
            query = query + "LIMIT " + limit.toString() + "";
        }
        return query;
    }

    /**
     @param indexColumn - column to "WHERE" query's part
     @param indexValue - value to "WHERE" query's part
     @param valueColumn - column that will be NULLed in row
     */
    public static String constructQueryValueRemove(String table, String indexColumn, Object indexValue, String valueColumn){
        String query;
        if (indexValue instanceof Number){
            query = "UPDATE `" + table + "` " +
                    "SET `" + valueColumn + "` = NULL " +
                    "WHERE `" + table + "`.`" + indexColumn + "` = " + indexValue;
        } else {
            query = "UPDATE `" + table + "` " +
                    "SET `" + valueColumn + "` = NULL " +
                    "WHERE `" + table+"`.`" + indexColumn + "` = '" + indexValue.toString() + "'";
        }
        return query;
    }

    public static String constructQueryValueRemove(String table, Pair indexPair, String column){
        String query;
        if (indexPair.getRight() instanceof Number){
            query = "UPDATE `" + table + "` " +
                    "SET `" + column+"` = NULL " +
                    "WHERE `" + table + "`.`" + indexPair.getLeft() + "` = " + indexPair.getRight();
        } else {
            query = "UPDATE `" + table + "` " +
                    "SET `" + column + "` = NULL " +
                    "WHERE `" + table + "`.`" + indexPair.getLeft() + "` = '" + indexPair.getRight().toString() + "'";
        }
        return query;
    }

    public static String constructQueryValueRemove(String table, Pair indexPair, Pair valuePair){
        String query;
        if (indexPair.getRight() instanceof Number){
            query = "UPDATE `" + table + "` " +
                    "SET `" + valuePair.getLeft() + "` = NULL " +
                    "WHERE `" + table + "`.`" + indexPair.getLeft() + "` = " + indexPair.getRight();
        } else {
            query = "UPDATE `" + table + "` " +
                    "SET `" + valuePair.getLeft() + "` = NULL " +
                    "WHERE `" + table + "`.`" + indexPair.getLeft() + "` = '" + indexPair.getRight().toString() + "'";
        }
        return query;
    }

    public static List<String> constructQuerySingleValueSet(String table, String keyColumn, Object keyValue, String setColumn, Object setValue,
                                                            Boolean hasTableUniqueKey, DatabaseType databaseType){
        List<String> queryList = new ArrayList<>();
        String setValueTrans = setValue+"";
        if (setValue == null || (""+setValue).equalsIgnoreCase("null")){
            setValueTrans = "NULL";
        } else if (!(setValue instanceof Number)) {
            setValueTrans = "'" + setValueTrans + "'";
        }
        String keyValueTrans = keyValue+"";
        if (keyValue == null || (""+keyValue).equalsIgnoreCase("null")){
            keyValueTrans = "NULL";
        } else if (!(keyValue instanceof Number)) {
            keyValueTrans = "'" + keyValueTrans + "'";
        }
        if (databaseType == DatabaseType.MYSQL || databaseType == DatabaseType.MARIADB){
            String query;
            query = "INSERT INTO `"+table+"` "
                    + "(`"+keyColumn+"`, `"+setColumn+"`) "
                    + "VALUES ("+keyValueTrans+", "+setValueTrans+") ";
            if (hasTableUniqueKey) {
                query = query
                        + "ON DUPLICATE KEY UPDATE "
                        + "`"+setColumn+"` = "+setValueTrans+" ";
            }
            queryList.add(query);
        } else if (databaseType == DatabaseType.H2) {
            String begining = "INSERT";
            if (hasTableUniqueKey) {
                begining = "INSERT OR IGNORE";
            }
            String query1 = begining+" INTO "+table+" "
                    + "("+keyColumn+", "+setColumn+") "
                    + "VALUES ("+keyValueTrans+", "+setValueTrans+")";
            if (hasTableUniqueKey) {
                String query2 = "MERGE INTO "+table+" ( "+keyColumn+", "+setColumn+") "
                        + "KEY ("+keyColumn+") "
                        + "VALUES ("+keyValueTrans+", "+setValueTrans+")";
                queryList.add(query2);
            }
            queryList.add(query1);
        } else {
            String begining = "INSERT";
            if (hasTableUniqueKey) {
                begining = "INSERT OR IGNORE";
            }
            String query1 = begining+" INTO `"+table+"` "
                    + "(`"+keyColumn+"`, "+setColumn+") "
                    + "VALUES ("+keyValueTrans+", "+setValueTrans+")";
            if (hasTableUniqueKey) {
                String query2 = "UPDATE `"+table+"` "
                        + "SET `"+setColumn+"` = "+setValueTrans+" "
                        + "WHERE `"+keyColumn+"` = "+keyValueTrans+" ";
                queryList.add(query2);
            }
            queryList.add(query1);
        }
        return queryList;
    }

    public static List<String> constructQuerySingleValueSet(String table, Pair keyPair, Pair setPair, Boolean hasTableUniqueKey, DatabaseType databaseType){
        String keyColumn = (String)keyPair.getLeft(),
                setColumn = (String)setPair.getLeft();
        Object keyValue = keyPair.getRight(),
                setValue = setPair.getRight();
        List<String> queryList = constructQuerySingleValueSet(table, keyColumn, keyValue, setColumn, setValue, hasTableUniqueKey, databaseType);
        return queryList;
    }

    public static List<String> constructQuerySingleValueSum(String table, Pair keyPair, Pair addPair, Boolean hasTableUniqueKey, DatabaseType databaseType){
        List<String> queryList = new ArrayList<>();
        String keyColumn = (String)keyPair.getLeft(),
                setColumn = (String)addPair.getLeft();
        Object keyValue = keyPair.getRight(),
                setValue = addPair.getRight();
        String keyValueTrans = keyValue+"";
        if (keyValue == null || (""+keyValue).equalsIgnoreCase("null")){
            keyValueTrans = "NULL";
        } else if (!(keyValue instanceof Number)) {
            keyValueTrans = "'" + keyValueTrans + "'";
        }
        if (databaseType == DatabaseType.MYSQL || databaseType == DatabaseType.MARIADB){
            String query;
            query = "INSERT INTO `"+table+"` "
                    + "(`"+keyColumn+"`, `"+setColumn+"`) "
                    + "VALUES ("+keyValueTrans+", "+setValue+") ";
            if (hasTableUniqueKey) {
                query = query
                        + "ON DUPLICATE KEY UPDATE "
                        + "`"+setColumn+"` = coalesce("+setColumn+" + "+setValue+", "+setValue+") ";
            }
            queryList.add(query);
        } else if (databaseType == DatabaseType.H2) {
            if (hasTableUniqueKey) {
                String query1, query2;
                query1 = "MERGE INTO "+table+" ("+keyColumn+", "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", 0)";
                query2 = "UPDATE "+ table+" "
                        + "SET "+setColumn+" = coalesce("+setColumn+" + "+setValue+", "+setValue+") "
                        + "WHERE "+keyColumn+" = "+keyValueTrans+" ";
                queryList.add(query1);
                queryList.add(query2);
            } else {
                String query1;
                query1 = "INSERT INTO "+table+" ("+keyColumn+", "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", "+setValue+")";
                queryList.add(query1);
            }
        } else {
            if (hasTableUniqueKey) {
                String query1, query2;
                query1 = "INSERT OR IGNORE INTO `"+table+"` "
                        + "(`"+keyColumn+"`, "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", 0)";
                query2 = "UPDATE `"+ table+"` "
                        + "SET `"+setColumn+"` = coalesce("+setColumn+" + "+setValue+", "+setValue+") "
                        + "WHERE `"+keyColumn+"` = "+keyValueTrans+" ";
                queryList.add(query1);
                queryList.add(query2);
            } else {
                String query1;
                query1 = "INSERT INTO `"+table+"` "
                        + "(`"+keyColumn+"`, "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", "+setValue+")";
                queryList.add(query1);
            }
        }
        return queryList;
    }

    public static List<String> constructQuerySingleValueSubtract(String table, Pair keyPair, Pair addPair, Boolean hasTableUniqueKey, DatabaseType databaseType){
        List<String> queryList = new ArrayList<>();
        String keyColumn = (String)keyPair.getLeft(),
                setColumn = (String)addPair.getLeft();
        Object keyValue = keyPair.getRight(),
                setValue = addPair.getRight();
        String keyValueTrans = keyValue+"";
        if (keyValue == null || (""+keyValue).equalsIgnoreCase("null")){
            keyValueTrans = "NULL";
        } else if (!(keyValue instanceof Number)) {
            keyValueTrans = "'" + keyValueTrans + "'";
        }
        if (databaseType == DatabaseType.MYSQL || databaseType == DatabaseType.MARIADB){
            String query;
            query = "INSERT INTO `"+table+"` "
                    + "(`"+keyColumn+"`, `"+setColumn+"`) "
                    + "VALUES ("+keyValueTrans+", -"+setValue+") ";
            if (hasTableUniqueKey) {
                query = query
                        + "ON DUPLICATE KEY UPDATE "
                        + "`"+setColumn+"` = coalesce("+setColumn+" - "+setValue+", "+setValue+") ";
            }
            queryList.add(query);

        } else if (databaseType == DatabaseType.H2) {
            if (hasTableUniqueKey) {
                String query1, query2;
                query1 = "MERGE INTO "+table+" ("+keyColumn+", "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", 0)";
                query2 = "UPDATE "+ table+" "
                        + "SET "+setColumn+" = coalesce("+setColumn+" - "+setValue+", "+setValue+") "
                        + "WHERE "+keyColumn+" = "+keyValueTrans+" ";
                queryList.add(query1);
                queryList.add(query2);
            } else {
                String query1;
                query1 = "INSERT INTO "+table+" ("+keyColumn+", "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", "+setValue+")";
                queryList.add(query1);
            }
        } else {
            if (hasTableUniqueKey) {
                String query1, query2;
                query1 = "INSERT OR IGNORE INTO `"+table+"` "
                        + "(`"+keyColumn+"`, "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", 0)";
                query2 = "UPDATE `"+ table+"` "
                        + "SET `"+setColumn+"` = coalesce("+setColumn+" - "+setValue+", "+setValue+") "
                        + "WHERE `"+keyColumn+"` = "+keyValueTrans+" ";
                queryList.add(query1);
                queryList.add(query2);
            } else {
                String query1;
                query1 = "INSERT INTO `"+table+"` "
                        + "(`"+keyColumn+"`, "+setColumn+") "
                        + "VALUES ("+keyValueTrans+", "+setValue+")";
                queryList.add(query1);
            }
        }
        return queryList;
    }

    /**
     * <p>If database is not MySQL then REMEMBER to put primary table name on first place</p>
     */
    public static List<String> constructQueryMultipleValuesSet(String table, List<Pair<String, Object>> pairs, Boolean hasTableUniqueKey, DatabaseType databaseType){
        List<String> queryList = new ArrayList<>();
        String flatColumns = "(";
        String flatValues = "(";
        String flatColumnsAndValues = "";
        Boolean first = true;
        for(Pair pair : pairs){
            String column = (String)pair.getLeft();
            Object value = pair.getRight();
            String valueTrans = value+"";
            if (value == null || (""+value).equalsIgnoreCase("null")){
                valueTrans = "NULL";
            } else if (!(value instanceof Number)) {
                valueTrans = "'" + valueTrans + "'";
            }
            if (first){
                flatColumns = flatColumns+"`"+column+"`";
                flatValues = flatValues + valueTrans;
                flatColumnsAndValues = "`"+column+"`=" + valueTrans;
                first = false;
            } else {
                flatColumns = flatColumns+", `"+column+"`";
                flatValues = flatValues + ", " + valueTrans;
                flatColumnsAndValues = flatColumnsAndValues + ", `"+column+"`=" + valueTrans;
            }
        }
        flatColumns = flatColumns + ")";
        flatValues = flatValues + ")";
        if (databaseType == DatabaseType.MYSQL || databaseType == DatabaseType.MARIADB){
            String query;
            query = "INSERT INTO `"+table+"` "
                    + flatColumns+" "
                    + "VALUES "+flatValues+" ";
            if (hasTableUniqueKey){
                query = query
                        + "ON DUPLICATE KEY UPDATE "
                        + flatColumnsAndValues;
            }
            queryList.add(query);
        } else if (databaseType == DatabaseType.H2) {
            String begining = "INSERT";
            if (hasTableUniqueKey) {
                begining = "MERGE INTO";
            }
            String query1 = begining+" "+table+" "
                    + flatColumns
                    + " VALUES "+flatValues;
            if (hasTableUniqueKey) {
                Pair<String, Object> keyPair = pairs.get(0);
                Object keyValue = keyPair.getRight();
                String keyValueTrans = keyValue != null ? keyValue.toString() : "NULL";
                if (!(keyValue instanceof Number)) {
                    keyValueTrans = "'" + keyValueTrans + "'";
                }
                String query2 = "UPDATE "+table+" "
                        + "SET "+flatColumnsAndValues+" "
                        + "WHERE "+keyPair.getLeft()+" = "+keyValueTrans;
                queryList.add(query2);
            }
            queryList.add(query1);
        } else {
            String begining = "INSERT";
            if (hasTableUniqueKey) {
                begining = "INSERT OR IGNORE";
            }
            String query1 = begining+" INTO `"+table+"` "
                    + flatColumns
                    + "VALUES "+flatValues;
            if (hasTableUniqueKey) {
                Object keyValue = pairs.get(0).getRight();
                String keyValueTrans = keyValue+"";
                if (keyValue == null || (""+keyValue).equalsIgnoreCase("null")){
                    keyValueTrans = "NULL";
                } else if (!(keyValue instanceof Number)) {
                    keyValueTrans = "'" + keyValueTrans + "'";
                }
                String query2 = "UPDATE `"+table+"` "
                        + "SET "+flatColumnsAndValues+" "
                        + "WHERE `"+pairs.get(0).getLeft()+"` = "+keyValueTrans+" ";
                queryList.add(query2);
            }
            queryList.add(query1);
        }

        return queryList;
    }

    // Need to convert String query to Query Object
    public static List<Query> convertStringToQuery(List<String> qS) {
        List<Query> listOfQueries = new ArrayList<>();
        for(String query : qS) {
            listOfQueries.add(new Query(query));
        }
        return listOfQueries;
    }
}
