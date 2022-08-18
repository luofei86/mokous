//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.db.utils;

import com.mokous.base.exception.BizException;

import java.util.*;

/**
 * @author luofei
 * Generate 2020/2/3
 */
public class DbConstantUtils {
    public static final String CREATE_TABLE_RESULT_MAP_KEY = "Create Table";

    public static String buildShowCreateTableResult(List<?> dbResultList) throws BizException {
        for (Object o : dbResultList) {
            if (o instanceof Map) {
                Map valueMap = (Map) o;
                return valueMap.get(DbConstantUtils.CREATE_TABLE_RESULT_MAP_KEY).toString();
            }
        }
        throw BizException.getInternalException("Show create table 未知的返回结果类型:" + dbResultList + " .");
    }

    public static final String MYSQL_KEYWORD_SELECT = "SELECT ";
    public static final String MYSQL_KEYWORD_LIMIT = " LIMIT ";
    public static final String MYSQL_KEYWORD_WHERE = " WHERE ";
    public static final String MYSQL_KEYWORD_AND = "AND";
    public static final String MYSQL_KEYWORD_OR = "OR";
    public static final String MYSQL_KEYWORD_FROM = " FROM ";
    public static final String MYSQL_KEYWORD_JOIN = " JOIN ";
    public static final String ESCAPE_SQL_SYMBOL = "`";
    public static final String SQL_EXECUTE_END_SYMBOL = ";";
    public static final String UNSIGNED_SQL_SYMBOL = " UNSIGNED ";
    public static final String AUTO_INCREMENT_SYMBOL = "AUTO_INCREMENT";
    public static final String DBNAME_SEPARATE_SYMBOL = "_";
    public static final String OPEN_PARENTHESIS = "(";
    public static final String CLOSED_PARENTHESIS = ")";
    public static final String COMMA_SEPARATE_SYMBOL = ",";

    public static final Set<String> SQL_OP_SET = new HashSet<String >() {{
        add("=");
        add("!=");
        add("<>");
        add(">");
        add("&gt;");
        add(">=");
        add("&gt;=");
        add("<");
        add("&lt;");
        add("<=");
        add("&lt;=");
        add("in");
        add("not in");
        add("exists");
        add("not exists");
        add("like");
        add("not like");
    }};

}
