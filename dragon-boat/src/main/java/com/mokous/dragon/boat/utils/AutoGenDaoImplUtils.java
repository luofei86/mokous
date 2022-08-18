//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.utils;

import com.mokous.dragon.boat.base.DragonUtils;
import com.mokous.dragon.boat.base.FileCreateModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class AutoGenDaoImplUtils {
    public static void genFile(FileCreateModel fileCreateModel, boolean hasUk) throws IOException {
        System.out.println("********Start Gen Dao impl File********");
        List<String> values = new ArrayList<>();
        values.add(DragonUtils.buildHeadComment());
        values.add(DragonUtils.buildPackageStatement(fileCreateModel.getDaoImplJavaPath()));
        values.add("");
        values.add(buildImport(fileCreateModel, hasUk));
        values.add(DragonUtils.buildClassComment());
        values.add(buildRepositionAnnotation());
        values.add(buildClass(fileCreateModel));
        values.add("");
        if (hasUk) {
            values.add(buildQueryByUniqueKey(fileCreateModel));
        }
        values.add("");
        values.add(DragonUtils.buildJavaEnd());
        File domainFile = new File(fileCreateModel.getDaoImplFilePath(), fileCreateModel.getDaoImplName() + ".java");
        DragonUtils
                .writeLines(domainFile,
                        "utf8", values);
        System.out.println("********End Gen Dao impl File********");
    }

    private static String buildImport(FileCreateModel fileCreateModel, boolean hasUk) {
        StringBuilder sb = new StringBuilder();
        sb.append("import org.springframework.stereotype.Repository;").append(DragonUtils.LINE_SEPARATOR);
        if (hasUk) {
            sb.append(DragonUtils
                    .buildImport(DragonUtils.buildPackage(Application.GROUP_ID, "base.exception.BizException")))
                    .append(DragonUtils.LINE_SEPARATOR);
            sb.append(DragonUtils
                    .buildImport(DragonUtils.buildPackage(Application.GROUP_ID, "db.utils.SQLUtils")))
                    .append(DragonUtils.LINE_SEPARATOR);
            sb.append(DragonUtils.buildImport(
                    DragonUtils.buildPackage(fileCreateModel.getDomainJavaPath(), fileCreateModel.getDomain())))
                    .append(DragonUtils.LINE_SEPARATOR);
        }
        sb.append(DragonUtils.buildImport(DragonUtils
                .buildPackage(fileCreateModel.getDaoJavaPath(), fileCreateModel.getDaoName())))
                .append(DragonUtils.LINE_SEPARATOR);
        return sb.toString();
    }

    private static String buildRepositionAnnotation() {
        return "@Repository";
    }

    private static String buildClass(FileCreateModel fileCreateModel) {
        return "public class " + fileCreateModel.getDaoImplName() + " extends " + fileCreateModel
                .getDaoName() + "{";
    }

    private static String buildQueryByUniqueKey(FileCreateModel fileCreateModel) {
        String queryByUniqueKey = "query" + fileCreateModel.getDomain() + "ByUniqueKey";
        String sb = "\t@Override" + DragonUtils.LINE_SEPARATOR
                + "\tpublic " + fileCreateModel.getDomain() + " queryByUniqueKey("
                + fileCreateModel.getDomain() + " g) throws BizException {"
                + DragonUtils.LINE_SEPARATOR
                + "\t\ttry{" + DragonUtils.LINE_SEPARATOR
                + "\t\t\treturn SQLUtils.queryObject(getSqlSessionFactory(), \"" + queryByUniqueKey + "\",g);"
                + DragonUtils.LINE_SEPARATOR
                + "\t\t} catch(Exception e){" + DragonUtils.LINE_SEPARATOR
                + "\t\t\tthrow BizException.getSqlException();" + DragonUtils.LINE_SEPARATOR
                + "\t\t}" + DragonUtils.LINE_SEPARATOR
                + "\t}";
        return sb;
    }
}
