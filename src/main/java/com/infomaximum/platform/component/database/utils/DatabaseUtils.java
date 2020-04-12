package com.infomaximum.platform.component.database.utils;

import com.infomaximum.platform.component.database.DatabaseConsts;

/**
 * Created by user on 25.08.2017.
 */
public class DatabaseUtils {

    public static String getDefaultKey() {
        return getKey(0);
    }

    //TODO Ulitin V. разобраться с uuid
    private static String getKey(int shard) {
        return new StringBuilder()
                .append(DatabaseConsts.UUID).append(':').append(shard)
                .toString();
    }

}
