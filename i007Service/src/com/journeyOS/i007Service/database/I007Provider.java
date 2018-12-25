/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.i007Service.database;


import com.journeyOS.i007Service.liteprovider.Column;
import com.journeyOS.i007Service.liteprovider.LiteProvider;
import com.journeyOS.i007Service.liteprovider.Table;

public class I007Provider extends LiteProvider {
    @Override
    protected String getAuthority() {
        return DBConfig.AUTHORITIES;
    }

    @Override
    protected int getSchemaVersion() {
        return DBConfig.SCHEMA_VERSION;
    }

    @Table
    public class Apps {
        @Column(Column.FieldType.TEXT)
        public static final String PACKAGE_NAME = DBConfig.PACKAGE_NAME;

        @Column(Column.FieldType.TEXT)
        public static final String TYPE = DBConfig.TYPE;

        @Column(Column.FieldType.TEXT)
        public static final String SUB_TYPE = DBConfig.SUB_TYPE;
    }

    @Table
    public class BLApps {
        @Column(Column.FieldType.TEXT)
        public static final String PACKAGE_NAME = DBConfig.PACKAGE_NAME;

        @Column(Column.FieldType.TEXT)
        public static final String TYPE = DBConfig.TYPE;

        @Column(Column.FieldType.TEXT)
        public static final String SUB_TYPE = DBConfig.SUB_TYPE;
    }

}
