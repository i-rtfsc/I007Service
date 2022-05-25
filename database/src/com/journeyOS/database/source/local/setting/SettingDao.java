package com.journeyOS.database.source.local.setting;


import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.journeyOS.database.source.local.base.DBConfigs;

@Dao
public interface SettingDao {

    @Insert(onConflict = REPLACE)
    void saveSetting(Setting setting);

    @Query("DELETE FROM " + DBConfigs.Setting.TABLE_NAME + " WHERE " + DBConfigs.Setting.KEY + " LIKE :key")
    void deleteSetting(String key);

    @Query("SELECT * FROM " + DBConfigs.Setting.TABLE_NAME + " WHERE " + DBConfigs.Setting.KEY + " LIKE :key")
    Setting getSetting(String key);
}
