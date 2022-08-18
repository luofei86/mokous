//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.utils;

import com.mokous.dragon.boat.base.*;

/**
 * @author luofei
 * Generate 2020/01/11
 */
public class Application {

    public static final String GROUP_ID = "com.mokous";

    public Application() {
    }

    public static void main(String[] args) throws Exception {
        if (DragonUtils.emptyOrNull(args)) {
            System.err.println("");
            return;
        }

        if (args.length < 10) {
            return;
        }
        for (int i = 0; i < args.length; ) {
            if (args[i] == null || args[i].length() == 0) {
                i++;
                continue;
            }
            String domainName = args[i++];
            String serviceSupportType = args[i++];
            String project = args[i++];
            String baseFilePath = args[i++];
            String bizFilePath = args[i++];
            String abstractDaoPath = args[i++];
            String ibatisConfigurePath = args[i++];
            String ibatisPath = args[i++];
            String createTableSqlFilePath = args[i++];
            String insertReturnId = args[i++];
            System.out.println("Start parse " + createTableSqlFilePath);
            SqlCreateTableModel sqlCreateTableModel = CreateTableSqlFileParser
                    .parseSqlCreateTable(createTableSqlFilePath);
            boolean hasUk = false;
            for (DbFieldInfo dbFieldInfo : sqlCreateTableModel.getAttributes()) {
                if (dbFieldInfo.isUniqueKey()) {
                    hasUk = true;
                    break;
                }
            }

            FileCreateModel fileCreateModel = new FileCreateModel(domainName, serviceSupportType, project, baseFilePath,
                    bizFilePath, abstractDaoPath, ibatisConfigurePath, ibatisPath);
            AutoGenDomainUtils.genFile(fileCreateModel, sqlCreateTableModel, sqlCreateTableModel.getAttributes());
            AutoGenXmlUtils
                    .genFile(fileCreateModel, sqlCreateTableModel.getTableName(), sqlCreateTableModel.getAttributes(),
                            hasUk, Boolean.parseBoolean(insertReturnId));
            AutoGenConfigXmlUtils.genOrAddToConfigFile(fileCreateModel);
            AutoGenDaoUtils.genFile(fileCreateModel, hasUk);
            AutoGenDaoImplUtils.genFile(fileCreateModel, hasUk);
            AutoGenServiceUtils.genFile(fileCreateModel, hasUk);
            AutoGenServiceImplUtils.genFile(fileCreateModel, hasUk);
            AutoGenServiceTestUtils.genFile(fileCreateModel, hasUk);

            System.out.println("End parse " + createTableSqlFilePath);

        }
    }

}
