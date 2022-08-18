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
 * Generate 2020/1/11
 */
public class AutoGenConfigXmlUtils {
    public static String buildIbatisHeadComment() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    }

    public static void genOrAddToConfigFile(FileCreateModel fileCreateModel) throws IOException {
        File file = new File(fileCreateModel.getXmlFilePath(), fileCreateModel.getIbatisConfigXmlFileName());
        List<String> values = new ArrayList<>();
        if (file.exists()) {
            System.out.println("********Start add to configure xml file********");
            List<String> preValues = DragonUtils.readLines(file);
            for (String preValue : preValues) {
                if (DragonUtils.contains(preValue, fileCreateModel.getIbatisXmlFileName())) {
                    return;
                }
                if (preValue.contains("</mappers>")) {
                    values.add(buildMapper(fileCreateModel));
                }
                values.add(preValue);
            }
        } else {
            System.out.println("********Start add to configure xml file********");
            values.add(buildIbatisHeadComment());
            values.add(buildConfigureDocType());
            values.add(buildConfigureHead());
            values.add(buildMappersHead());
            values.add(buildMapper(fileCreateModel));
            values.add(buildMappersEnd());
            values.add(buildConfigureEnd());

        }
        DragonUtils.writeLines(file, "utf8", values);
        System.out.println("********End add to configure xml file********");
    }

    private static String buildConfigureDocType() {
        return "<!DOCTYPE configuration PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-config.dtd\">";

    }

    private static String buildConfigureHead() {
        return "<configuration>";
    }

    private static String buildMappersHead() {
        return "\t<mappers>";
    }

    private static String buildMappersEnd() {
        return "\t</mappers>";
    }

    private static String buildConfigureEnd() {
        return "</configuration>";
    }

    private static String buildMapper(FileCreateModel fileCreateModel) {
        return "\t\t<mapper resource=\"ibatis/" + fileCreateModel.getIbatisXmlFileName() + "\"/>";
    }
}
