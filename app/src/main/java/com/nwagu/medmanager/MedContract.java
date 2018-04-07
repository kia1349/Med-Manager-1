package com.nwagu.medmanager;

import android.provider.BaseColumns;

class MedContract {
	static final class PillsEntry implements BaseColumns {
		static final String TABLE_NAME = "pills";

		static final String COLUMN_PILL_NAME = "pillName";
		static final String COLUMN_PILL_DESC = "pillDesc";
		static final String COLUMN_PILL_INTERVAL= "pillInterval";
		static final String COLUMN_PILL_START = "pillStart";
		static final String COLUMN_PILL_END= "pillEnd";
        static final String COLUMN_PILL_LAST= "pillLast"; //represents time pill was last taken
	}
}
