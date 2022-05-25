package com.journeyOS.database.source.local.setting;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.journeyOS.database.source.local.base.DBConfigs;

/**
 * setting entity
 *
 * @author solo
 */
@Entity(tableName = DBConfigs.Setting.TABLE_NAME, primaryKeys = {DBConfigs.Setting.KEY})
public class Setting {

    /**
     * primary key
     */
    @NonNull
    @ColumnInfo(name = DBConfigs.Setting.KEY)
    public String key = "";

    @ColumnInfo(name = DBConfigs.Setting.VALUE)
    public String value;

    @ColumnInfo(name = DBConfigs.Setting.OBJECT)
    public String object;

    @Ignore
    public boolean getBoolean() {
        if (Boolean.class.getName().equals(object)) {
            return Boolean.parseBoolean(value);
        } else {
            throw new IllegalStateException("settings is " + object);
        }
    }

    @Ignore
    public int getInt() {
        if (Integer.class.getName().equals(object)) {
            return (Integer) Integer.parseInt(value);
        } else {
            throw new IllegalStateException("settings is " + object);
        }
    }

    @Ignore
    public String getString() {
        if (String.class.getName().equals(object)) {
            return (String) value;
        } else {
            throw new IllegalStateException("settings is " + object);
        }
    }

    @Ignore
    public float getFloat() {
        if (Float.class.getName().equals(object)) {
            return Float.parseFloat(value);
        } else {
            throw new IllegalStateException("settings is " + object);
        }
    }
}
