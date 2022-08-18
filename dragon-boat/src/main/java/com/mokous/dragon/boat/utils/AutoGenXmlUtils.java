//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.utils;

import com.mokous.dragon.boat.base.DragonUtils;
import com.mokous.dragon.boat.base.DbFieldInfo;
import com.mokous.dragon.boat.base.FileCreateModel;
import com.mokous.dragon.boat.enums.EnumCompareType;
import com.mokous.dragon.boat.enums.EnumInsertType;
import com.mokous.dragon.boat.enums.EnumQueryType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/11
 */
public class AutoGenXmlUtils {
    private static final int LINE_MAX_COLUMNS = 9;

    public static void genFile(FileCreateModel fileCreateModel, String tableName, List<DbFieldInfo> attributes,
                               boolean hasUk, boolean insertReturnId) throws IOException {
        System.out.println("********Start gen xml file********");
        List<String> values = new ArrayList<>();
        values.add(AutoGenConfigXmlUtils.buildIbatisHeadComment());
        values.add(buildIbatisDoctype());
        values.add(buildMapperHead(fileCreateModel.getDomain()));
        values.add(buildResultMap(fileCreateModel, attributes));
        values.add("");
        values.add(buildInsert(tableName, fileCreateModel, attributes, insertReturnId));
        values.add(buildInsertBatch(tableName, fileCreateModel, attributes));

        values.add(buildInsertOrIgnore(tableName, fileCreateModel, attributes, insertReturnId));
        values.add(buildInsertOrIgnoreBatch(tableName, fileCreateModel, attributes));

        values.add(buildInsertOrUpdate(tableName, fileCreateModel, attributes, insertReturnId));
        values.add(buildInsertOrUpdateBatch(tableName, fileCreateModel, attributes));
        values.add(buildUpdateSql(tableName, fileCreateModel, attributes));
        values.add(buildQueryByIdSql(tableName, fileCreateModel, attributes));
        values.add(buildQueryListByIdsSql(tableName, fileCreateModel, attributes));
        values.add(buildQueryListByStartIdSql(tableName, fileCreateModel, attributes));
        values.add(buildQueryListByStartIdFilterSql(tableName, fileCreateModel, attributes));
        values.add(buildQueryListByEndIdFilterSql(tableName, fileCreateModel, attributes));
        values.add(buildQueryListByFilterSql(tableName, fileCreateModel, attributes));
        values.add(buildCountByFilterSql(tableName, fileCreateModel, attributes));
        if (hasUk) {
            values.add(buildQueryBuUniqueKeySql(tableName, fileCreateModel, attributes));
        }
        values.add("");
        values.add(getMapperEnd());
        DragonUtils
                .writeLines(new File(fileCreateModel.getXmlFilePath(), fileCreateModel.getIbatisXmlFileName()), "utf8",
                        values);
        System.out.println("********End gen xml file********");
    }

    private static String getMapperEnd() {
        return "</mapper>";
    }

    private static String buildQueryBuUniqueKeySql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.QUERY_BY_UNIQUE_KEY_FILTER);
    }

    private static String buildCountByFilterSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.COUNT_BY_DOMAIN_FILTER,
                "resultType=\"java.lang.Long\">");
    }

    private static String buildQueryListByFilterSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.QUERY_BY_DOMAIN_FILTER);
    }

    private static String buildQueryListByEndIdFilterSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.QUERY_BY_END_ID_FILTER);

    }

    private static String buildQueryListByStartIdFilterSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.QUERY_BY_START_ID_FILTER);

    }

    private static String buildQueryListByStartIdSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.QUERY_BY_START_ID);

    }

    private static String buildQueryListByIdsSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.QUERY_BY_IDS);
    }

    private static String buildQueryByIdSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, EnumQueryType.QUERY_BY_ID);
    }

    private static String buildNormalQuery(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes, EnumQueryType enumQueryType) {
        return buildNormalQuery(tableName, fileCreateModel, attributes, enumQueryType,
                "resultMap=\"" + buildQueryResult(enumQueryType, fileCreateModel) + "\">");
    }

    private static String buildNormalQuery(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes, EnumQueryType enumQueryType, String resultStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t<select id=\"").append(enumQueryType.getPrefix()).append(fileCreateModel.getDomain())
                .append(enumQueryType.getSuffix())
                .append("\" parameterType=\"")
                .append(buildQueryParameter(enumQueryType, attributes)).append("\" ").append(resultStr)
                .append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\tSELECT").append(enumQueryType.getAction()).append(" FROM `").append(tableName).append("` ")
                .append(DragonUtils.LINE_SEPARATOR);
        if (enumQueryType == EnumQueryType.QUERY_BY_ID || enumQueryType == EnumQueryType.QUERY_BY_START_ID
                || enumQueryType == EnumQueryType.QUERY_BY_START_ID_FILTER
                || enumQueryType == EnumQueryType.QUERY_BY_END_ID_FILTER) {
            sb.append(buildPrimaryQuerySql(attributes, enumQueryType.getValue())).append(DragonUtils.LINE_SEPARATOR);
        } else if (enumQueryType == EnumQueryType.QUERY_BY_IDS) {
            DbFieldInfo pkDbFieldInfo = null;
            for (DbFieldInfo attribute : attributes) {
                if (attribute.isPrimaryKey()) {
                    pkDbFieldInfo = attribute;
                }
            }
            if (pkDbFieldInfo == null) {
                return "";
            }
            sb.append(
                    "\t\t<foreach collection=\"ids\" item=\"item\" index=\"index\" close=\")\" separator=\",\" open=\" WHERE `")
                    .append(pkDbFieldInfo.getColumn()).append("` in (\">").append(DragonUtils.LINE_SEPARATOR);
            sb.append("\t\t\t#{item}").append(DragonUtils.LINE_SEPARATOR);

            sb.append("\t\t</foreach>").append(DragonUtils.LINE_SEPARATOR);
        } else if (enumQueryType == EnumQueryType.QUERY_BY_DOMAIN_FILTER
                || enumQueryType == EnumQueryType.COUNT_BY_DOMAIN_FILTER) {
            buildDomainFilter(sb, attributes, enumQueryType);
            if (enumQueryType == EnumQueryType.QUERY_BY_DOMAIN_FILTER) {
                sb.append("\t\t").append("ORDER BY `${orderColumn}` ${orderColumnSort} LIMIT #{start}, #{size};")
                        .append(DragonUtils.LINE_SEPARATOR);
            }
        } else if (enumQueryType == EnumQueryType.QUERY_BY_UNIQUE_KEY_FILTER) {
            buildDomainFilter(sb, attributes, enumQueryType);
        }

        if (enumQueryType == EnumQueryType.QUERY_BY_START_ID_FILTER
                || enumQueryType == EnumQueryType.QUERY_BY_END_ID_FILTER) {
            buildSqlFilters(sb, attributes);
            buildSqlOrder(sb, attributes, enumQueryType);
            buildLimit(sb);
        }
        sb.append("\t</select>").append(DragonUtils.LINE_SEPARATOR);
        return sb.toString();
    }

    private static String buildQueryResult(EnumQueryType enumQueryType, FileCreateModel fileCreateModel) {
        if (enumQueryType == EnumQueryType.COUNT_BY_DOMAIN_FILTER) {
            return "java.lang.Long";
        }
        return fileCreateModel.getDomainAsAttribute();
    }

    private static void buildDomainFilter(StringBuilder sb, List<DbFieldInfo> attributes,
            EnumQueryType enumQueryType) {
        sb.append("\t\t").append("<trim prefix=\" WHERE \" prefixOverrides ='AND|OR'>")
                .append(DragonUtils.LINE_SEPARATOR);
        buildSqlFilter(sb, "java.lang.String", "delFlag", "del_flag");
        if (enumQueryType == EnumQueryType.QUERY_BY_UNIQUE_KEY_FILTER) {
            List<DbFieldInfo> uks = new ArrayList<>();
            for (DbFieldInfo attribute : attributes) {
                if (attribute.isUniqueKey()) {
                    uks.add(attribute);
                }
            }
            buildSqlFilters(sb, uks);
        } else {
            buildSqlFilters(sb, attributes);
        }
        sb.append("\t\t</trim>").append(DragonUtils.LINE_SEPARATOR);
    }

    private static void buildLimit(StringBuilder sb) {
        sb.append("\t\tLIMIT #{size}").append(DragonUtils.LINE_SEPARATOR);
    }

    private static void buildSqlOrder(StringBuilder sb, List<DbFieldInfo> attributes, EnumQueryType enumQueryType) {
        boolean findPk = false;
        sb.append("\t\t");
        for (DbFieldInfo attribute : attributes) {
            if (attribute.isPrimaryKey()) {
                if (!findPk) {
                    sb.append(" ORDER BY `" + attribute.getColumn() + "` ");
                } else {
                    sb.append(", `" + attribute.getColumn() + "` ");
                }
                findPk = true;
            }
        }
        if (!findPk) {
            sb.append(" ORDER BY `id` ");
        }
        if (enumQueryType == EnumQueryType.QUERY_BY_START_ID
                || enumQueryType == EnumQueryType.QUERY_BY_START_ID_FILTER) {
            sb.append(" ASC ");
        } else {
            sb.append(" DESC");
        }
        sb.append(DragonUtils.LINE_SEPARATOR);
    }

    private static void buildSqlFilters(StringBuilder sb, List<DbFieldInfo> attributes) {
        for (DbFieldInfo attribute : attributes) {
            if (attribute.isIndexKey()) {
                buildSqlFilter(sb, attribute);
            }
        }
    }

    private static void buildSqlFilter(StringBuilder sb, DbFieldInfo attribute) {
        buildSqlFilter(sb, attribute.getType(), attribute.getName(), attribute.getColumn());
    }

    private static void buildSqlFilter(StringBuilder sb, String type, String name, String column) {
        if (DragonUtils.strEquals(type, "Date")) {
            sb.append("\t\t\t").append("<if test=\"").append(name).append("St != null\">")
                    .append(DragonUtils.LINE_SEPARATOR);
            sb.append("\t\t\t\t").append(" AND `").append(column).append("` &gt;= #{")
                    .append(name).append("St}").append(DragonUtils.LINE_SEPARATOR);
            sb.append("\t\t\t</if>").append(DragonUtils.LINE_SEPARATOR);
            sb.append("\t\t\t").append("<if test=\"").append(name).append("Et != null\">")
                    .append(DragonUtils.LINE_SEPARATOR);
            sb.append("\t\t\t\t").append(" AND `").append(column).append("` &lt; #{")
                    .append(name).append("Et}").append(DragonUtils.LINE_SEPARATOR);
        } else {
            sb.append("\t\t\t").append("<if test=\"").append(name).append(" != null\">")
                    .append(DragonUtils.LINE_SEPARATOR);
            sb.append("\t\t\t\t").append(" AND `").append(column).append("` = #{")
                    .append(name).append("}").append(DragonUtils.LINE_SEPARATOR);
        }
        sb.append("\t\t\t</if>").append(DragonUtils.LINE_SEPARATOR);
    }

    private static String buildQueryParameter(EnumQueryType enumQueryType, List<DbFieldInfo> attributes) {
        if (enumQueryType == EnumQueryType.QUERY_BY_IDS || enumQueryType == EnumQueryType.QUERY_BY_START_ID
                || enumQueryType == EnumQueryType.QUERY_BY_START_ID_FILTER
                || enumQueryType == EnumQueryType.QUERY_BY_END_ID_FILTER
                || enumQueryType == EnumQueryType.QUERY_BY_DOMAIN_FILTER
                || enumQueryType == EnumQueryType.COUNT_BY_DOMAIN_FILTER
                || enumQueryType == EnumQueryType.QUERY_BY_UNIQUE_KEY_FILTER) {
            return "java.util.Map";
        }
        if (enumQueryType == EnumQueryType.QUERY_BY_ID) {
            String parameterType = "";
            for (DbFieldInfo attribute : attributes) {
                if (attribute.isPrimaryKey()) {
                    parameterType = "java.lang.Integer";
                }
            }
            if (DragonUtils.emptyOrNull(parameterType)) {
                parameterType = "java.lang.Integer";
            }
            return parameterType;
        }
        return null;
    }

    private static String buildUpdateSql(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t<update id=\"update").append(fileCreateModel.getDomain() + "Status\" parameterType=\"")
                .append(DragonUtils.buildPackage(fileCreateModel.getDomainJavaPath(), fileCreateModel.getDomain()))
                .append("\">");
        sb.append(DragonUtils.LINE_SEPARATOR).append("\t\t<![CDATA[").append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\t\tUPDATE `").append(tableName).append("` SET `del_flag` = #{delFlag}");
        sb.append(buildPrimaryQuerySql(attributes)).append(DragonUtils.LINE_SEPARATOR).append("\t\t]]>")
                .append(DragonUtils.LINE_SEPARATOR).append("\t</update>").append(DragonUtils.LINE_SEPARATOR);
        return sb.toString();
    }

    private static String buildPrimaryQuerySql(List<DbFieldInfo> attributes) {
        return buildPrimaryQuerySql(attributes, EnumCompareType.EQUALS);
    }


    private static String buildPrimaryQuerySql(List<DbFieldInfo> attributes, EnumCompareType enumCompareType) {
        String pkQuerySql = "";
        for (DbFieldInfo attribute : attributes) {
            if (attribute.isPrimaryKey()) {
                if (pkQuerySql.length() != 0) {
                    pkQuerySql += " AND ";
                }
                pkQuerySql += "`" + attribute.getColumn() + "` " + enumCompareType.getValue() + " #{" + enumCompareType
                        .getParameter()
                        + "}";
            }
        }
        if (pkQuerySql.length() == 0) {
            return "\t\tWHERE `id` " + enumCompareType.getValue() + " #{" + enumCompareType.getParameter() + "}";
        }
        return "\t\tWHERE " + pkQuerySql;
    }

    private static String buildInsertOrUpdateBatch(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildInsert(tableName, fileCreateModel, attributes, EnumInsertType.INSERT_OR_UPDATE, true,
                canInsertOrUpdate(attributes), false);
    }

    private static String buildInsertOrUpdate(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes, boolean insertReturnId) {
        return buildInsert(tableName, fileCreateModel, attributes, EnumInsertType.INSERT_OR_UPDATE, false,
                canInsertOrUpdate(attributes), insertReturnId);
    }

    private static boolean canInsertOrUpdate(List<DbFieldInfo> attributes) {
        for (DbFieldInfo attribute : attributes) {
            if (attribute.isUniqueKey()) {
                return true;
            }
            if (attribute.isAutoIncrement()) {
                continue;
            }
            if (attribute.isPrimaryKey() && !attribute.isAutoIncrement()) {
                return true;
            }
        }
        return false;
    }

    private static String buildInsertOrIgnoreBatch(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildInsert(tableName, fileCreateModel, attributes, EnumInsertType.INSERT_OR_IGNORE, true, false, false);

    }

    private static String buildInsertOrIgnore(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes, boolean insertReturnId) {
        return buildInsert(tableName, fileCreateModel, attributes, EnumInsertType.INSERT_OR_IGNORE, false, false, insertReturnId);
    }

    private static String buildInsertBatch(String tableName, FileCreateModel fileCreateModel,
            List<DbFieldInfo> attributes) {
        return buildInsert(tableName, fileCreateModel, attributes, EnumInsertType.INSERT, true, false, false);
    }


    private static String buildForeachColumnValues(List<DbFieldInfo> attributes) {
        StringBuilder sb = new StringBuilder().append("VALUES").append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\t<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">")
                .append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\t\t").append(buildItemColumnValues(attributes)).append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\t</foreach>");
        return sb.toString();
    }

    private static String buildItemColumnValues(List<DbFieldInfo> attributes) {
        return buildColumnValues(attributes, true);
    }

    private static String buildInsert(String tableName, FileCreateModel fileCreateModel, List<DbFieldInfo> attributes,
            boolean insertReturnId) {
        return buildInsert(tableName, fileCreateModel, attributes, EnumInsertType.INSERT, false, false, insertReturnId);
    }

    private static String buildInsert(String tableName, FileCreateModel fileCreateModel, List<DbFieldInfo> attributes,
            EnumInsertType insertType, boolean batch, boolean update, boolean insertReturnId) {
        StringBuilder sb = new StringBuilder();
        String insertStr = insertType == EnumInsertType.INSERT ? "insert" :
                insertType == EnumInsertType.INSERT_OR_IGNORE ? "insertOrIgnore" : "insertOrUpdate";
        String insertSql =
                insertType == EnumInsertType.INSERT || insertType == EnumInsertType.INSERT_OR_UPDATE ? "INSERT INTO" :
                        "INSERT IGNORE INTO";
        String batchStr = batch ? "Batch" : "";
        sb.append("\t<insert id=\"").append(insertStr).append(fileCreateModel.getDomain()).append(batchStr)
                .append("\" parameterType=\"")
                .append(DragonUtils.buildPackage(fileCreateModel.getDomainJavaPath(), fileCreateModel.getDomain()))
                .append("\">").append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\t").append(insertSql).append(" `").append(tableName).append("`")
                .append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\t(").append(buildColumnNames(attributes)).append(DragonUtils.LINE_SEPARATOR);
        if (batch) {
            sb.append("\t\t").append(buildForeachColumnValues(attributes)).append(DragonUtils.LINE_SEPARATOR);
        } else {
            sb.append("\t\t").append(buildColumnValues(attributes)).append(DragonUtils.LINE_SEPARATOR);
        }
        if (insertType == EnumInsertType.INSERT_OR_UPDATE && update) {
            sb.append("\t\t").append(" ON DUPLICATE KEY UPDATE ");
            boolean firstUpdate = true;
            for (int i = 0; i < attributes.size(); i++) {
                DbFieldInfo dbFieldInfo = attributes.get(i);
                if (dbFieldInfo.isUniqueKey() || dbFieldInfo.isPrimaryKey()) {
                    continue;
                }
                if (DragonUtils.strEquals(dbFieldInfo.getName(), DragonUtils.UPDATE_TIME) || DragonUtils
                        .strEquals(dbFieldInfo.getName(), DragonUtils.CREATE_TIME)) {
                    continue;
                }
                if (!firstUpdate) {
                    sb.append(", ");
                } else {
                    firstUpdate = false;
                }

                if (i != 0 && i % 2 == 0) {
                    sb.append(DragonUtils.LINE_SEPARATOR);
                    if (i != attributes.size() - 1) {
                        sb.append("\t\t");
                    }
                }
                String append;
                if (batch) {
                    append = " = VALUES(`" + dbFieldInfo.getColumn() + "`)";
                } else {
                    append = " = #{" + dbFieldInfo.getName() + "}";
                }
                sb.append("`").append(dbFieldInfo.getColumn()).append("`").append(append);
            }
            sb.append(DragonUtils.LINE_SEPARATOR);
        }
        if (insertReturnId) {
            sb.append("\t\t<selectKey resultType=\"java.lang.Integer\" keyProperty=\"id\">")
                    .append(DragonUtils.LINE_SEPARATOR).append("\t\t\tSELECT @@IDENTITY AS id")
                    .append(DragonUtils.LINE_SEPARATOR).append("\t\t</selectKey>").append(DragonUtils.LINE_SEPARATOR);
        }
        sb.append("\t</insert>");
        return sb.toString();
    }

    private static String buildColumnValues(List<DbFieldInfo> attributes) {
        return buildColumnValues(attributes, false);
    }



    private static String buildColumnValues(List<DbFieldInfo> attributes, boolean batch) {
        StringBuilder sb = batch ? new StringBuilder("(") : new StringBuilder("VALUES (");
        String assignSymbol = batch ? "#{item." : "#{";
        for (int i = 0; i < attributes.size(); i++) {
            if (i != 0 && i % LINE_MAX_COLUMNS == 0) {
                sb.append(DragonUtils.LINE_SEPARATOR);
                if (i != attributes.size() - 1) {
                    sb.append("\t\t");
                }
            }
            DbFieldInfo dbFieldInfo = attributes.get(i);
            if (DragonUtils.strEquals(dbFieldInfo.getColumn(), DragonUtils.ID_COLUMN)) {
                if (dbFieldInfo.isAutoIncrement()) {
                    sb.append("null");
                } else {
                    sb.append(assignSymbol).append("id}");
                }
            } else {
                if (dbFieldInfo.isIgnoreOutputInJava() && DragonUtils.strEquals(dbFieldInfo.getType(), "Date")) {
                    sb.append("now()");
                } else {
                    sb.append(assignSymbol).append(dbFieldInfo.getName()).append("}");
                }
            }
            if (i == attributes.size() - 1) {
                break;
            } else {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private static String buildColumnNames(List<DbFieldInfo> attributes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < attributes.size(); i++) {
            if (i != 0 && i % LINE_MAX_COLUMNS == 0) {
                sb.append(DragonUtils.LINE_SEPARATOR);
                if (i != attributes.size() - 1) {
                    sb.append("\t\t");
                }
            }
            DbFieldInfo dbFieldInfo = attributes.get(i);
            buildSqlColumn(sb, dbFieldInfo);
            if (i == attributes.size() - 1) {
                break;
            } else {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private static void buildSqlColumn(StringBuilder sb, DbFieldInfo dbFieldInfo) {
        sb.append("`").append(dbFieldInfo.getColumn()).append("`");
    }

    private static String buildResultMap(FileCreateModel fileCreateModel, List<DbFieldInfo> attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t<resultMap type=\"");
        sb.append(DragonUtils.buildPackage(fileCreateModel.getDomainJavaPath(), fileCreateModel.getDomain()));
        sb.append("\" id=\"").append(fileCreateModel.getDomainAsAttribute()).append("\">")
                .append(DragonUtils.LINE_SEPARATOR);
        for (DbFieldInfo attribute : attributes) {
            sb.append("\t\t").append("<result column=\"").append(attribute.getColumn()).append("\" property=\"")
                    .append(attribute.getName()).append("\" jdbcType=\"").append(attribute.getJdbcType()).append("\"/>")
                    .append(DragonUtils.LINE_SEPARATOR);
        }
        sb.append("\t</resultMap>");
        return sb.toString();
    }

    private static String buildMapperHead(String domain) {
        return "<mapper namespace=\"T_" + domain.toUpperCase() + "\">";
    }

    private static String buildIbatisDoctype() {
        return "<!DOCTYPE mapper PUBLIC \"-//mybatis.com//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">";
    }
}
