// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.iauth.model;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
public class AppleIdAuthParameter {
    public static final int AUTH_NEED = 1;
    public static final int AUTH_NO_NEED = 0;
    public static final int USE_NEW_LOGALGORITHM_APPLESESSION_VALUE = 1;

    private String email;
    private String passwd;
    private int auth;
    private String machine;
    private Integer appleSession;

    /**
     * appleSession will work after clientVersion 2.2.46743
     * 
     * @param email
     * @param passwd
     * @param machine
     * @param auth
     * @param appleSession
     */
    public AppleIdAuthParameter(String email, String passwd, String machine, int auth, Integer appleSession) {
        super();
        this.email = email;
        this.passwd = passwd;
        this.machine = machine;
        this.auth = auth;
        this.appleSession = appleSession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        return "AppleIdAuthParameter [email=" + email + ", passwd=" + passwd + ", auth=" + auth + ", machine="
                + machine + ", appleSession=" + appleSession + "]";
    }

    public Integer getAppleSession() {
        return appleSession;
    }

    public void setAppleSession(Integer appleSession) {
        this.appleSession = appleSession;
    }

}
