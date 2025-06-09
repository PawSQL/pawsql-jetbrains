// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.pawsql.client.icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class PawSQLIcons {
    public static final Icon ICON_24 = IconLoader.getIcon("/icons/pluginIcon.svg", PawSQLIcons.class);
    public static final Icon ICON_d24 = IconLoader.getIcon("/icons/pluginIcon_dark.svg", PawSQLIcons.class);
    //    public static final Icon ICON_r24 = IconLoader.getIcon("/icons/pluginIcon.svg", PawSQLIcons.class);
//    public static final Icon ICON_b24 = IconLoader.getIcon("/icons/pluginIcon_dark.svg", PawSQLIcons.class);
    public static final Icon DAMENG = IconLoader.getIcon("/icons/workspace/dameng.svg", PawSQLIcons.class);
    public static final Icon DWS = IconLoader.getIcon("/icons/workspace/dws.svg", PawSQLIcons.class);
    public static final Icon HIVE = IconLoader.getIcon("/icons/workspace/hive.svg", PawSQLIcons.class);
    public static final Icon KINGBASE = IconLoader.getIcon("/icons/workspace/kingbase.svg", PawSQLIcons.class);
    public static final Icon MYSQL = IconLoader.getIcon("/icons/workspace/mysql.svg", PawSQLIcons.class);
    public static final Icon OPENGAUSS = IconLoader.getIcon("/icons/workspace/opengauss.svg", PawSQLIcons.class);
    public static final Icon ORACLE = IconLoader.getIcon("/icons/workspace/oracle.svg", PawSQLIcons.class);
    public static final Icon POSTGRES = IconLoader.getIcon("/icons/workspace/postgres.svg", PawSQLIcons.class);

    // UUID-based icons
    public static final Icon ICON_0F4092C8 = IconLoader.getIcon("/icons/workspace/0f4092c8-d8ff-4803-9d40-7c684819ad8d.svg", PawSQLIcons.class);
    public static final Icon ICON_8AFE9A17 = IconLoader.getIcon("/icons/workspace/8afe9a17-1e93-9513-afa4-03cc5117fd50.svg", PawSQLIcons.class);
    public static final Icon ICON_09D38AC7 = IconLoader.getIcon("/icons/workspace/09d38ac7-f73e-4189-b7f9-aa762af5f35a.svg", PawSQLIcons.class);
    public static final Icon ICON_24D2641D = IconLoader.getIcon("/icons/workspace/24d2641d-f7ac-4f48-8216-3a4182f4c03d.svg", PawSQLIcons.class);
    public static final Icon ICON_94FC45F9 = IconLoader.getIcon("/icons/workspace/94fc45f9-3c5c-4b47-8b3a-b396f8540d60.svg", PawSQLIcons.class);
    public static final Icon ICON_148DF0E4 = IconLoader.getIcon("/icons/workspace/148df0e4-7475-4358-bcc8-da9db9c9c623.svg", PawSQLIcons.class);
    public static final Icon ICON_426ECB17 = IconLoader.getIcon("/icons/workspace/426ecb17-2341-4d42-b255-4accb260f7f5.svg", PawSQLIcons.class);
    public static final Icon ICON_435BB9A5 = IconLoader.getIcon("/icons/workspace/435bb9a5-7887-4809-aa58-28c27df0d7ad.svg", PawSQLIcons.class);
    public static final Icon ICON_778DAA7C = IconLoader.getIcon("/icons/workspace/778daa7c-feaf-4db6-96f3-70fd645acc77.svg", PawSQLIcons.class);
    public static final Icon ICON_B0ED57DC = IconLoader.getIcon("/icons/workspace/b0ed57dc-1575-0c10-be29-74ef4dbf2267.svg", PawSQLIcons.class);
    public static final Icon ICON_DECD338E = IconLoader.getIcon("/icons/workspace/decd338e-5647-4c0b-adf4-da0e75f5a750.svg", PawSQLIcons.class);
    public static final Icon ICON_FBA03030 = IconLoader.getIcon("/icons/workspace/fba03030-f5c0-426d-8530-85b39bc510d8.svg", PawSQLIcons.class);
    public static final Icon ICON_FC1B699A = IconLoader.getIcon("/icons/workspace/fc1b699a-83cf-4ec2-add8-fb69e45cee00.svg", PawSQLIcons.class);
    public static final Icon ICON_DEFAULT = IconLoader.getIcon("/icons/workspace/default-db-online.png", PawSQLIcons.class);

    private static final Map<String, Icon> ICON_MAP = new HashMap<String, Icon>() {{
        // Database icons
        put("dameng", PawSQLIcons.DAMENG);
        put("dws", PawSQLIcons.DWS);
        put("hive", PawSQLIcons.HIVE);
        put("kingbase", PawSQLIcons.KINGBASE);
        put("mysql", PawSQLIcons.MYSQL);
        put("opengauss", PawSQLIcons.OPENGAUSS);
        put("oracle", PawSQLIcons.ORACLE);
        put("postgres", PawSQLIcons.POSTGRES);

        // UUID-based icons
        put("0f4092c8-d8ff-4803-9d40-7c684819ad8d", PawSQLIcons.ICON_0F4092C8);
        put("8afe9a17-1e93-9513-afa4-03cc5117fd50", PawSQLIcons.ICON_8AFE9A17);
        put("09d38ac7-f73e-4189-b7f9-aa762af5f35a", PawSQLIcons.ICON_09D38AC7);
        put("24d2641d-f7ac-4f48-8216-3a4182f4c03d", PawSQLIcons.ICON_24D2641D);
        put("94fc45f9-3c5c-4b47-8b3a-b396f8540d60", PawSQLIcons.ICON_94FC45F9);
        put("148df0e4-7475-4358-bcc8-da9db9c9c623", PawSQLIcons.ICON_148DF0E4);
        put("426ecb17-2341-4d42-b255-4accb260f7f5", PawSQLIcons.ICON_426ECB17);
        put("435bb9a5-7887-4809-aa58-28c27df0d7ad", PawSQLIcons.ICON_435BB9A5);
        put("778daa7c-feaf-4db6-96f3-70fd645acc77", PawSQLIcons.ICON_778DAA7C);
        put("b0ed57dc-1575-0c10-be29-74ef4dbf2267", PawSQLIcons.ICON_B0ED57DC);
        put("decd338e-5647-4c0b-adf4-da0e75f5a750", PawSQLIcons.ICON_DECD338E);
        put("fba03030-f5c0-426d-8530-85b39bc510d8", PawSQLIcons.ICON_FBA03030);
        put("fc1b699a-83cf-4ec2-add8-fb69e45cee00", PawSQLIcons.ICON_FC1B699A);
        put("default-db-online", ICON_DEFAULT);
    }};

    public static Icon getIcon(String filename) {
        return ICON_MAP.get(filename);
    }

    public static boolean hasIcon(String filename) {
        return ICON_MAP.containsKey(filename);
    }
}
