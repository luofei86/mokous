//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.base;

import com.mokous.dragon.boat.base.DragonUtils;
import com.mokous.dragon.boat.enums.EnumsSupportServiceType;
import com.mokous.dragon.boat.utils.Application;
import lombok.Data;

import java.io.File;

/**
 * @author luofei
 * Generate 2020/1/11
 */
@Data
public class FileCreateModel {
    private String domain;
    private String serviceSupportType;
    private String domainAsAttribute;
    private String domainToUppercase;
    private String domainJavaPath;
    private String domainFilePath;
    private String daoJavaPath;
    private String daoFilePath;
    private String daoImplJavaPath;
    private String daoImplFilePath;
    private String serviceJavaPath;
    private String serviceFilePath;
    private String serviceTestFilePath;
    private String serviceImplJavaPath;
    private String serviceImplFilePath;
    private String xmlFilePath;
    private String ibatisXmlFileName;
    private String ibatisConfigXmlFileName;
    private String abstractDaoJavaPath;
    private String projectAsCamelCase;
    private String baseDaoJavaPath;
    private String daoName;
    private String daoNameAsAttribute;
    private String daoImplName;
    private String serviceName;
    private String serviceNameAsAttribute;
    private String serviceTestName;
    private String serviceImplName;
    private String project;
    private String abstractDaoName;

    public FileCreateModel(String domainName, String serviceSupportType, String project, String baseFilePath,
            String bizFilePath, String abstractDaoPath, String ibatisConfigurePath, String ibatisPath) {
        String projectModelTestFilePath = bizFilePath.replace("main", "test");
        this.domain = domainName;
        this.project = project;
        if (EnumsSupportServiceType.valueOfKey(serviceSupportType) != null) {
            this.serviceSupportType = serviceSupportType;
        } else {
            this.serviceSupportType = "Date";
        }

        this.domainAsAttribute = DragonUtils.buildAttributeValue(domainName);
        this.domainToUppercase = this.domain.toUpperCase();
        this.domainFilePath = DragonUtils.buildFile(baseFilePath);
        this.domainJavaPath = DragonUtils.buildPackage(filePathToJavaPath(baseFilePath));
        this.baseDaoJavaPath = DragonUtils.buildPackage(Application.GROUP_ID, "dao");

        this.daoFilePath = DragonUtils.buildFile(bizFilePath, "dao");
        this.daoJavaPath = DragonUtils.buildPackage(filePathToJavaPath(bizFilePath), "dao");

        this.daoImplFilePath = DragonUtils.buildFile(bizFilePath, "dao", "impl");
        this.daoImplJavaPath = DragonUtils.buildPackage(filePathToJavaPath(bizFilePath), "dao", "impl");

        this.serviceFilePath = DragonUtils.buildFile(bizFilePath, "service");
        this.serviceJavaPath = DragonUtils.buildPackage(filePathToJavaPath(bizFilePath), "service");

        this.serviceImplFilePath = DragonUtils.buildFile(bizFilePath, "service", "impl");
        this.serviceImplJavaPath = DragonUtils.buildPackage(filePathToJavaPath(bizFilePath), "service", "impl");

        this.xmlFilePath = ibatisPath;
        this.ibatisXmlFileName = "ibatis-" + project + "-" + domainName.toLowerCase() + ".xml";
        String[] arr = ibatisConfigurePath.split(File.separator);

        this.ibatisConfigXmlFileName = arr[arr.length - 1];

        this.daoName = domainName + "Dao";
        this.daoNameAsAttribute = DragonUtils.buildAttributeValue(this.daoName);

        this.daoImplName = this.daoName + "Impl";

        this.serviceName = domainName + "Service";
        this.serviceNameAsAttribute = DragonUtils.buildAttributeValue(this.serviceName);

        this.serviceImplName = this.serviceName + "Impl";
        this.serviceTestName = this.serviceName + "Test";

        String filePathToJava = filePathToJavaPath(abstractDaoPath);
        String[] abstractDao = filePathToJava.split("\\.");
        this.abstractDaoName = abstractDao[abstractDao.length - 2];
        this.abstractDaoJavaPath = filePathToJava
                .substring(0, filePathToJava.indexOf(DragonUtils.JAVA_SRC_FOLDER_SYMBOL + abstractDaoName));
    }

    private String filePathToJavaPath(String baseFilePath) {
        String srcMainJava = "src" + File.separator + "main" + File.separator + "java" + File.separator;
        String result = baseFilePath.substring(baseFilePath.indexOf(srcMainJava) + 14)
                .replace(String.valueOf(File.separatorChar), DragonUtils.JAVA_SRC_FOLDER_SYMBOL);
        if (result.endsWith(DragonUtils.JAVA_SRC_FOLDER_SYMBOL)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    @Override
    public String toString() {
        return "FileCreateModel{" +
                "domain='" + domain + '\'' +
                ", serviceSupportType='" + serviceSupportType + '\'' +
                ", domainAsAttribute='" + domainAsAttribute + '\'' +
                ", domainToUppercase='" + domainToUppercase + '\'' +
                ", domainJavaPath='" + domainJavaPath + '\'' +
                ", domainFilePath='" + domainFilePath + '\'' +
                ", daoJavaPath='" + daoJavaPath + '\'' +
                ", daoFilePath='" + daoFilePath + '\'' +
                ", daoImplJavaPath='" + daoImplJavaPath + '\'' +
                ", daoImplFilePath='" + daoImplFilePath + '\'' +
                ", serviceJavaPath='" + serviceJavaPath + '\'' +
                ", serviceFilePath='" + serviceFilePath + '\'' +
                ", serviceTestFilePath='" + serviceTestFilePath + '\'' +
                ", serviceImplJavaPath='" + serviceImplJavaPath + '\'' +
                ", serviceImplFilePath='" + serviceImplFilePath + '\'' +
                ", xmlFilePath='" + xmlFilePath + '\'' +
                ", ibatisXmlFileName='" + ibatisXmlFileName + '\'' +
                ", ibatisConfigXmlFileName='" + ibatisConfigXmlFileName + '\'' +
                ", abstractDaoJavaPath='" + abstractDaoJavaPath + '\'' +
                ", projectAsCamelCase='" + projectAsCamelCase + '\'' +
                ", baseDaoJavaPath='" + baseDaoJavaPath + '\'' +
                ", daoName='" + daoName + '\'' +
                ", daoNameAsAttribute='" + daoNameAsAttribute + '\'' +
                ", daoImplName='" + daoImplName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceNameAsAttribute='" + serviceNameAsAttribute + '\'' +
                ", serviceTestName='" + serviceTestName + '\'' +
                ", serviceImplName='" + serviceImplName + '\'' +
                ", project='" + project + '\'' +
                ", abstractDaoName='" + abstractDaoName + '\'' +
                '}';
    }
}
