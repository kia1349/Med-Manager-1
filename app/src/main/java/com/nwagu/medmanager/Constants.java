package com.nwagu.medmanager;

interface Constants {
    String EXTRA_MED_INDEX = "med index";
    String EXTRA_MED_NAME = "med name";
    String EXTRA_MED_DESC = "med desc";
    String EXTRA_MED_FREQ = "med freq";
    String EXTRA_MED_START = "med start";
    String EXTRA_MED_END = "med end";
    String EXTRA_MED_LAST = "med last taken";

    String DUE_PILL_NAME = "due pill";

    int REQUEST_NEW_MED = 1;
    int REQUEST_PILL_EDIT = 2;
    int PILL_DISPLAY_COLOURS = 7;
    int RC_SIGN_IN = 9;

    int RESULT_MED_TAKE = 1;
    int RESULT_MED_DELETE = 2;
}
