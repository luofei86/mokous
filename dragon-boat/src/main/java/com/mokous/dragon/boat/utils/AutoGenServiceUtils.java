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
public class AutoGenServiceUtils {
    public static void genFile(FileCreateModel fileCreateModel, boolean hasUk) throws IOException {
        System.out.println("********Start Gen Service File********");
        List<String> values = new ArrayList<>();
        values.add(DragonUtils.buildHeadComment());
        values.add(DragonUtils.buildPackageStatement(fileCreateModel.getServiceJavaPath()));
        values.add("");
        values.add(buildImport(fileCreateModel, hasUk));
        values.add("");
        values.add(DragonUtils.buildClassComment());
        values.add(buildClass(fileCreateModel));
        values.add("");
        if (hasUk) {
            values.add(buildQueryByUniqueKey(fileCreateModel));
        }
        values.add("");
        values.add(DragonUtils.buildJavaEnd());
        File domainFile = new File(fileCreateModel.getServiceFilePath(), fileCreateModel.getServiceName() + ".java");
        DragonUtils
                .writeLines(domainFile,
                        "utf8", values);
        System.out.println("********End Gen Service File********");
    }

    private static String buildQueryByUniqueKey(FileCreateModel fileCreateModel) {
        StringBuilder sb = new StringBuilder();
        sb.append("\tpublic abstract ").append(fileCreateModel.getDomain()).append(" getByUniqueKey(")
                .append(fileCreateModel.getDomain()).append(" g) throws BizException;");
        return sb.toString();
    }

    private static String buildClass(FileCreateModel fileCreateModel) {
        return "public abstract class " + fileCreateModel.getServiceName() + " extends Abstract" + fileCreateModel
                .getServiceSupportType() + "Service<" + fileCreateModel.getDomain() + "> {";
    }

    private static String buildImport(FileCreateModel fileCreateModel, boolean hasUk) {
        StringBuilder sb = new StringBuilder();
        if (hasUk) {
            sb.append(DragonUtils
                    .buildImport(DragonUtils.buildPackage(Application.GROUP_ID, "base.exception.BizException")));
        }
        sb.append(DragonUtils.buildImport(DragonUtils
                .buildPackage(Application.GROUP_ID, "db.service",
                        "Abstract" + fileCreateModel.getServiceSupportType() + "Service")));
        sb.append(DragonUtils.buildImport(
                DragonUtils.buildPackage(fileCreateModel.getDomainJavaPath(), fileCreateModel.getDomain())));
        return sb.toString();
    }
}
