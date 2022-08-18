//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.base;

import java.io.File;
import java.util.*;

/**
 * @author luofei86@gmail.com
 * Generate 2020/1/11
 */
public class CreateTableSqlFileParser {
    /**
     * @param createTableSqlFilePath sqlPath
     * @return SqlCreateTableModel
     */
    public static SqlCreateTableModel parseSqlCreateTable(String createTableSqlFilePath) throws Exception {
        File file = new File(createTableSqlFilePath);
        List<String> sqlFiles = DragonUtils.readLines(file);
        return parseSqlCreateTable(sqlFiles);
    }

    public static SqlCreateTableModel parseSqlCreateTable(List<String> sqlFiles) throws Exception {
        boolean keyStart = false;
        List<DbFieldInfo> dbFieldInfoList = new ArrayList<>();
        String tableName = "";
        Set<String> keyColumns = new HashSet<>();
        Set<List<String>> ukColumns = new HashSet<>();
        Set<String> pkColumns = new HashSet<>();
        List<List<DbFieldInfo>> ukDbFieldInfoList = new ArrayList<>();

        for (String sql : sqlFiles) {
            sql = sql.trim();
            if (DragonUtils.startsWithIgnoreCase(sql, DragonUtils.CREATE_TABLE_KEY_SYMBOL)) {
                tableName = findTableName(sql);
                continue;
            }
            if (DragonUtils.strEquals(sql, DragonUtils.SQL_EXECUTE_END_SYMBOL)) {
                break;
            }
            if (!keyStart) {
                keyStart = isKeyStart(sql);
            }
            if (!keyStart) {
                String columnName = findSqlColumn(sql);
                String columnType = findSqlColumnType(sql, columnName);
                boolean unsigned = findSqlTypeUnsigned(sql);
                boolean canNull = findSqlColumnCanNull(sql);
                boolean ignoreOutputInJava = false;
                boolean autoIncrement = false;
                if (DragonUtils.strEquals(columnName, DragonUtils.ID_COLUMN)) {
                    autoIncrement = DragonUtils.contains(sql, DragonUtils.AUTO_INCREMENT_SYMBOL);
                    canNull = false;
                    ignoreOutputInJava = true;
                } else if (DragonUtils.strEquals(columnName, DragonUtils.DEL_FLAG_COLUMN) || DragonUtils
                        .strEquals(columnName, DragonUtils.UPDATE_TIME_COLUMN) || DragonUtils
                        .strEquals(columnName, DragonUtils.CREATE_TIME_COLUMN)) {
                    canNull = false;
                    ignoreOutputInJava = true;
                }
                dbFieldInfoList.add(new DbFieldInfo(columnName, columnType,
                        DragonUtils.convertToClassAttribute(columnName),
                        findJavaType(columnType, canNull, unsigned), findJdbcType(columnType, unsigned),
                        canNull, unsigned, ignoreOutputInJava, autoIncrement));
            } else {
                if (DragonUtils.contains(sql, DragonUtils.KEY_SYMBOL)) {
                    keyColumns.addAll(buildAllKeys(sql));
                }
                if (DragonUtils.contains(sql, DragonUtils.PRIMARY_KEY_SYMBOL)) {
                    pkColumns.addAll(buildAllKeys(sql));
                }
                if (DragonUtils.contains(sql, DragonUtils.UNIQUE_KEY_SYMBOL)) {
                    ukColumns.add(buildAllKeys(sql));
                }
            }
        }
        for (DbFieldInfo dbFieldInfo : dbFieldInfoList) {
            if (keyColumns.contains(dbFieldInfo.getColumn())) {
                dbFieldInfo.setIndexKey(true);
            }
            if (pkColumns.contains(dbFieldInfo.getColumn())) {
                dbFieldInfo.setPrimaryKey(true);
                dbFieldInfo.setIndexKey(true);
            }
            for (List<String> ukColumn : ukColumns) {
                if (ukColumn.contains(dbFieldInfo.getColumn())) {
                    dbFieldInfo.setUniqueKey(true);
                    dbFieldInfo.setIndexKey(true);
                }
            }
        }
        for (List<String> ukColumn : ukColumns) {
            List<DbFieldInfo> innerUkDbFieldInfoList = new ArrayList<>();
            for (String s : ukColumn) {
                for (DbFieldInfo dbFieldInfo : dbFieldInfoList) {
                    if (DragonUtils.strEquals(s, dbFieldInfo.getColumn())) {
                        innerUkDbFieldInfoList.add(dbFieldInfo);
                        break;
                    }
                }
            }
            if (!innerUkDbFieldInfoList.isEmpty()) {
                ukDbFieldInfoList.add(innerUkDbFieldInfoList);
            }
        }
        return new SqlCreateTableModel(tableName, dbFieldInfoList, ukDbFieldInfoList);
    }

    private static List<String> buildAllKeys(String sql) {
        sql = sql.trim();
        sql = DragonUtils
                .substringBetween(sql, DragonUtils.OPEN_PARENTHESIS, DragonUtils.CLOSED_PARENTHESIS);
        int openParenthesisIndex = sql.indexOf(DragonUtils.OPEN_PARENTHESIS);
        if (openParenthesisIndex != DragonUtils.INDEX_NOT_FOUND) {
            sql = sql.substring(0, openParenthesisIndex);
        }
        String[] columns = DragonUtils.split(sql, DragonUtils.COMMA_SEPARATE_SYMBOL);
        List<String> keyColumns = new ArrayList<>();
        for (String column : columns) {
            column = column.trim();
            String keyColumn = DragonUtils.substringBetween(column, DragonUtils.ESCAPE_SQL_SYMBOL);
            if (DragonUtils.notEmptyAndNull(keyColumn)) {
                keyColumns.add(keyColumn);
            } else {
                keyColumns.add(column);
            }
        }
        return keyColumns;
    }

    private static String findJavaType(String columnType, boolean canNull, boolean unsigned) throws Exception {
        int typeLenIndex = columnType.indexOf(DragonUtils.OPEN_PARENTHESIS);
        if (typeLenIndex != DragonUtils.INDEX_NOT_FOUND) {
            columnType = columnType.substring(0, typeLenIndex);
        }
        if (DragonUtils.strEquals(columnType, "int") && unsigned) {
            if (canNull) {
                return Long.class.getSimpleName();
            } else {
                return long.class.getSimpleName();
            }
        }
        if (DragonUtils.strEquals(columnType, "int", "tinyint", "smallint", "mediumint")) {
            if (canNull) {
                return Integer.class.getSimpleName();
            } else {
                return int.class.getSimpleName();
            }
        }

        if (DragonUtils.strEquals(columnType, "bigint")) {
            if (canNull) {
                return Long.class.getSimpleName();
            } else {
                return long.class.getSimpleName();
            }
        }


        if (DragonUtils.strEquals(columnType, "float", "decimal")) {
            if (canNull) {
                return Float.class.getSimpleName();
            } else {
                return float.class.getSimpleName();
            }
        }
        if (DragonUtils.strEquals(columnType, "double")) {
            if (canNull) {
                return Double.class.getSimpleName();
            } else {
                return double.class.getSimpleName();
            }
        }

        if (DragonUtils.strEquals(columnType, "varchar", "char", "text", "tinytext", "longtext", "mediumtext")) {
            return String.class.getSimpleName();
        }

        if (DragonUtils.strEquals(columnType, "date", "datetime", "year", "time", "timestamp")) {
            return Date.class.getSimpleName();
        }
        throw new Exception("暂不支持的Mysql-Java类型" + columnType);
    }

    private static String findJdbcType(String columnType, boolean unsigned) throws Exception {
        int typeLenIndex = columnType.indexOf(DragonUtils.OPEN_PARENTHESIS);
        if (typeLenIndex != DragonUtils.INDEX_NOT_FOUND) {
            columnType = columnType.substring(0, typeLenIndex);
        }
        if (DragonUtils.strEquals(columnType, "int") && unsigned) {
            return "BIGINT";
        }
        if (DragonUtils.strEquals(columnType, "int", "tinyint", "smallint", "mediumint")) {
            return "INTEGER";
        }

        if (DragonUtils.strEquals(columnType, "bigint")) {
            return "BIGINT";
        }


        if (DragonUtils.strEquals(columnType, "float", "decimal")) {
            return "FLOAT";
        }
        if (DragonUtils.strEquals(columnType, "double")) {
            return "DOUBLE";
        }

        if (DragonUtils.strEquals(columnType, "varchar", "char", "text", "tinytext", "longtext", "mediumtext")) {
            return "VARCHAR";
        }

        if (DragonUtils.strEquals(columnType, "date", "datetime", "year", "time", "timestamp")) {
            return "TIMESTAMP";
        }
        throw new Exception("暂不支持的Mysql-Jdbc类型" + columnType);
    }

    private static boolean findSqlColumnCanNull(String sql) {
        sql = sql.trim().toUpperCase();
        int nullIndex = sql.indexOf("NULL ");
        if (nullIndex <= DragonUtils.INDEX_NOT_FOUND) {
            nullIndex = sql.indexOf("NULL,");
        }
        if (nullIndex <= DragonUtils.INDEX_NOT_FOUND) {
            return true;
        }
        sql = sql.substring(0, nullIndex).trim();
        String[] sqls = sql.split(" ");
        for (int i = 1; i < sqls.length; i++) {
            if (DragonUtils.strEquals(sqls[i], "NOT")) {
                return false;
            }
        }
        return true;
    }

    private static boolean findSqlTypeUnsigned(String sql) {
        return DragonUtils.contains(sql, DragonUtils.UNSIGNED_SQL_SYMBOL);
    }

    private static String findSqlColumnType(String sql, String columnName) {
        sql = sql.trim();
        String typeStart = sql.substring(sql.indexOf(" ", sql.indexOf(columnName)));
        typeStart = typeStart.trim();
        int nextSpaceIndex = typeStart.indexOf(" ");
        String type;
        if (nextSpaceIndex == DragonUtils.INDEX_NOT_FOUND) {
            type = DragonUtils.replace(columnName, ",", "");
        } else {
            type = typeStart.substring(0, nextSpaceIndex);
        }
        int typeLenIndex = type.indexOf("(");
        if (typeLenIndex > DragonUtils.INDEX_NOT_FOUND) {
            type = type.substring(0, typeLenIndex);
        }
        return type;
    }

    private static boolean isKeyStart(String sql) {
        if (DragonUtils.startsWithIgnoreCase(sql, DragonUtils.PRIMARY_KEY_SYMBOL)) {
            return true;
        }
        if (DragonUtils.startsWithIgnoreCase(sql, DragonUtils.UNIQUE_KEY_SYMBOL)) {
            return true;
        }
        return DragonUtils.startsWithIgnoreCase(sql, DragonUtils.KEY_SYMBOL);
    }

    private static String findSqlColumn(String sql) throws Exception {
        sql = sql.trim();
        String column = DragonUtils.substringBetween(sql, DragonUtils.ESCAPE_SQL_SYMBOL);
        if (DragonUtils.notEmptyAndNull(column)) {
            return column;
        }
        int endIndex = column.indexOf(" ");
        if (endIndex > DragonUtils.INDEX_NOT_FOUND) {
            return column.substring(0, endIndex);
        }
        throw new Exception("未知的建表列语句，" + sql);
    }

    private static String findTableName(String sql) {
        String name = DragonUtils.substringBetween(sql, DragonUtils.ESCAPE_SQL_SYMBOL);
        if (DragonUtils.notEmptyAndNull(name)) {
            return name;
        }
        String[] sqls = DragonUtils.split(sql," ");
        for (int i = sqls.length - 1; i >= 0; i--) {
            if(DragonUtils.strEquals(sqls[i], " ")){
                continue;
            }
            if(DragonUtils.strEquals(sqls[i], "(")){
                continue;
            }
            return sqls[i];
        }
        return "";
    }
}
