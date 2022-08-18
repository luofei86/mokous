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
public class AutoGenServiceImplUtils {
    public static void genFile(FileCreateModel fileCreateModel, boolean hasUk) throws IOException {
        System.out.println("********Start Gen Service Impl File********");
        List<String> values = new ArrayList<>();
        values.add(DragonUtils.buildHeadComment());
        values.add(DragonUtils.buildPackageStatement(fileCreateModel.getServiceImplJavaPath()));
        values.add("");
        values.add(buildImport(fileCreateModel, hasUk));
        values.add("");
        values.add(DragonUtils.buildClassComment());
        values.add(buildServiceAnnotation());
        values.add(buildClass(fileCreateModel));
        values.add(buildAutowired(fileCreateModel));
        values.add(buildOverrideGetDao(fileCreateModel));
        values.add("");
        if (hasUk) {
            values.add(buildQueryByUniqueKey(fileCreateModel));
        }
        values.add("");
        values.add(DragonUtils.buildJavaEnd());
        File domainFile = new File(fileCreateModel.getServiceImplFilePath(),
                fileCreateModel.getServiceImplName() + ".java");
        DragonUtils
                .writeLines(domainFile,
                        "utf8", values);
        System.out.println("********End Gen Service Impl File********");
    }

    private static String buildOverrideGetDao(FileCreateModel fileCreateModel) {
        return "\t@Override" + DragonUtils.LINE_SEPARATOR + "\tpublic CommonDao<" + fileCreateModel.getDomain()
                + "> getCommonDao(" + fileCreateModel.getDomain() + " g) {" + DragonUtils.LINE_SEPARATOR
                + "\t\treturn " + fileCreateModel.getDaoNameAsAttribute() + ";" + DragonUtils.LINE_SEPARATOR
                + "\t}";
    }


    private static String buildAutowired(FileCreateModel fileCreateModel) {
        return "\t@Autowired" + DragonUtils.LINE_SEPARATOR + "\tprivate " + fileCreateModel.getDaoName() + " "
                + fileCreateModel.getDaoNameAsAttribute() + ";" + DragonUtils.LINE_SEPARATOR;
    }

    private static String buildServiceAnnotation() {
        return "@Service";
    }

    private static String buildQueryByUniqueKey(FileCreateModel fileCreateModel) {
        StringBuilder sb = new StringBuilder();
        sb.append("\tpublic ").append(fileCreateModel.getDomain()).append(" getByUniqueKey(")
                .append(fileCreateModel.getDomain()).append(" g) throws BizException{");
        sb.append("\t\treturn ").append(fileCreateModel.getDaoNameAsAttribute()).append(".queryByUniqueKey(g);");
        sb.append("\t}");
        return sb.toString();
    }

    private static String buildClass(FileCreateModel fileCreateModel) {
        return "public class " + fileCreateModel.getServiceImplName() + " extends " + fileCreateModel.getServiceName()
                + " {";
    }

    private static String buildImport(FileCreateModel fileCreateModel, boolean hasUk) {
        StringBuilder sb = new StringBuilder();
        if (hasUk) {
            sb.append(DragonUtils
                    .buildImport(DragonUtils.buildPackage(Application.GROUP_ID, "base.exception.BizException")));
        }
        sb.append("import org.springframework.stereotype.Service;").append(DragonUtils.LINE_SEPARATOR);
        sb.append("import org.springframework.beans.factory.annotation.Autowired;")
                .append(DragonUtils.LINE_SEPARATOR);
        sb.append(DragonUtils.buildImport(
                DragonUtils.buildPackage(fileCreateModel.getServiceJavaPath(), fileCreateModel.getServiceName())));
        sb.append(DragonUtils.buildImport(
                DragonUtils.buildPackage(fileCreateModel.getDaoJavaPath(), fileCreateModel.getDaoName())));
        sb.append(DragonUtils.buildImport(
                DragonUtils.buildPackage(fileCreateModel.getDomainJavaPath(), fileCreateModel.getDomain())));
        sb.append(DragonUtils
                .buildImport(DragonUtils.buildPackage(Application.GROUP_ID, "db.dao.CommonDao")));
        return sb.toString();
    }
}
