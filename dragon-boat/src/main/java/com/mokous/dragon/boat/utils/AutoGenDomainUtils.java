//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.utils;

import com.mokous.dragon.boat.base.DragonUtils;
import com.mokous.dragon.boat.base.DbFieldInfo;
import com.mokous.dragon.boat.base.FileCreateModel;
import com.mokous.dragon.boat.base.SqlCreateTableModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/11
 */
public class AutoGenDomainUtils {

    public static void genFile(FileCreateModel fileCreateModel, SqlCreateTableModel sqlCreateTableModel,
                               List<DbFieldInfo> attributes) throws IOException {
        List<String> values = new ArrayList<>();
        values.add(DragonUtils.buildHeadComment());
        values.add(DragonUtils.buildPackageStatement(fileCreateModel.getDomainJavaPath()));
        values.add(buildImport(fileCreateModel, attributes, !sqlCreateTableModel.getUks().isEmpty()));
        values.add(DragonUtils.buildClassComment());
        if (!sqlCreateTableModel.getUks().isEmpty()) {
            values.add(buildUkAnnotation(sqlCreateTableModel.getUks()));
        }
        values.add(buildClass(fileCreateModel.getDomain()));
        values.add(buildSerialVersionId());
        values.add(buildDomainFile(attributes));
        values.add(buildToString(attributes, fileCreateModel.getDomain()));
        values.add(DragonUtils.buildJavaEnd());
        File domainFile = new File(fileCreateModel.getDomainFilePath(), fileCreateModel.getDomain() + ".java");
        DragonUtils
                .writeLines(domainFile,
                        "utf8", values);
        System.out.println("********End Gen Domain File********");
    }

    private static String buildToString(List<DbFieldInfo> attributes, String domain) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t@Override").append(DragonUtils.LINE_SEPARATOR);
        sb.append("\tpublic String toString(){").append(DragonUtils.LINE_SEPARATOR);
        sb.append("\t\treturn \"").append(domain).append("[");
        for (DbFieldInfo attribute : attributes) {
            if (attribute.isIgnoreOutputInJava()) {
                continue;
            }
            sb.append(attribute.getName()).append(" = \"").append("+").append(attribute.getName()).append(" +\",");
        }
        sb.append(" toString() = \"").append("+").append("super.toString()+ \"");
        sb.append("]\";").append(DragonUtils.LINE_SEPARATOR).append("\t}");
        return sb.toString();
    }

    private static String buildDomainFile(List<DbFieldInfo> attributes) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        for (DbFieldInfo attribute : attributes) {
            if (attribute.isIgnoreOutputInJava()) {
                continue;
            }
            buildAttributeDeclare(sb, attribute);
            buildGetMethod(sb1, attribute);
            buildSetMethod(sb1, attribute);
        }
        return sb.append(sb1).toString();
    }

    private static void buildSetMethod(StringBuilder sb, DbFieldInfo attribute) {
        sb.append("\tpublic void set").append(DragonUtils.buildCameCaseValue(attribute.getName())).append("(")
                .append(attribute.getType()).append(" ").append(attribute.getName()).append(") {")
                .append("\t\t").append("this.").append(attribute.getName()).append(" = ")
                .append(attribute.getName()).append(";").append(DragonUtils.LINE_SEPARATOR).append("\t}").append(
                DragonUtils.LINE_SEPARATOR);
    }

    private static void buildGetMethod(StringBuilder sb, DbFieldInfo attribute) {
        sb.append(
                "\tpublic " + attribute.getType() + " get" + DragonUtils.buildCameCaseValue(attribute.getName())
                        + "(){").append(DragonUtils.LINE_SEPARATOR).append("\t\t").append("return this.")
                .append(attribute.getName()
                ).append(";").append(DragonUtils.LINE_SEPARATOR).append("\t}").append(DragonUtils.LINE_SEPARATOR);
    }

    private static void buildAttributeDeclare(StringBuilder sb, DbFieldInfo attribute) {
        sb.append("\tprivate " + attribute.getType() + " " + attribute.getName() + ";"
                + DragonUtils.LINE_SEPARATOR);
    }

    private static String buildClass(String domain) {
        return "public class " + domain + " extends DbDomain {";
    }

    private static String buildSerialVersionId() {
        return "\tprivate static final long serialVersionUID = " + DragonUtils.nextLong() + "L;";
    }

    private static String buildUkAnnotation(List<List<DbFieldInfo>> uks) {
        String result = "";
        for (List<DbFieldInfo> uk : uks) {
            String ukString = "";
            for (DbFieldInfo dbFieldInfo : uk) {
                if (ukString.length() == 0) {
                    ukString = "\"" + dbFieldInfo.getName() + "\"";
                } else {
                    ukString += ", " +  "\"" + dbFieldInfo.getName() + "\"";
                }
            }
            if (ukString.length() == 0) {
                continue;
            }
            if (result.length() != 0) {
                result += DragonUtils.LINE_SEPARATOR;
            }
            return "@UNIQUE_KEY(values = {" + ukString + "})";
        }
        return result;
    }

    private static String buildImport(FileCreateModel fileCreateModel, List<DbFieldInfo> attributes, boolean hasUk) {
        String values = DragonUtils
                .buildImport(DragonUtils.buildPackage(Application.GROUP_ID, "base.domain.model.DbDomain"));
        for (DbFieldInfo dbFieldInfo : attributes) {
            if (DragonUtils.strEquals(dbFieldInfo.getType(), "Date") && !DragonUtils
                    .strEquals(DragonUtils.UPDATE_TIME_COLUMN, dbFieldInfo.getColumn())
                    && !DragonUtils.strEquals(DragonUtils.CREATE_TIME_COLUMN, dbFieldInfo.getColumn())) {
                values += DragonUtils.buildImport("java.util.Date");
                break;
            }
        }
        if (hasUk) {
            values += DragonUtils
                    .buildImport(DragonUtils.buildPackage(Application.GROUP_ID, "db.enums.UNIQUE_KEY"));
        }
        return values;
    }
}
