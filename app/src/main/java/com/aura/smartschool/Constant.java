package com.aura.smartschool;

public final class Constant {
	public static final String HOST = "http://aurasystem.kr:9000";
	
	public static final String API_SIGNIN = "/api/signIn";
	public static final String API_SIGNUP = "/api/signUp";
	public static final String API_ADD_MEMBER = "/api/addMember";
	public static final String API_UPDATE_MEMBER = "/api/updateMember";
	public static final String API_GET_MEMBERLIST= "/api/getMemberList";
	public static final String API_ADD_LOCATION = "/api/addLocation";
	public static final String API_GET_SCHOOLLIST = "/api/getSchoolList";
	public static final String API_GET_LASTLOCATION = "/api/getLastLocation";
	public static final String API_GET_LOCATIONLIST = "/api/getLocationList";

	public static final String API_GET_MEASURESUMMARY = "/api/getMeasureSummary";
	public static final String API_GET_HEIGHT = "/api/getHeight";
	public static final String API_GET_WEIGHT = "/api/getWeight";

	public static final String API_ADD_AREA = "/api/addArea";
	public static final String API_GET_AREA = "/api/getArea";
	public static final String API_GET_AREALIST = "/api/getAreaList";

	public static final String API_GET_SCHOOL_NOTI_LIST = "/admin/api/getSchoolNotiList";
	public static final String API_GET_SCHOOL_NOTI_LIST_BY_MEMBER = "/api/getSchoolNotiListByMember";

	public static final String API_GET_CONSULT_LIST = "/api/getConsultList";
	public static final String API_ADD_CONSULT = "/admin/api/addConsult";
	public static final String API_RATE_CONSULT = "/api/rateConsult";
	public static final String API_GET_CONSULT_HISTORY = "/api/getConsultHistory";

	public static final String API_GET_APP_NOTI_LIST = "/api/getNotiList";
	public static final String API_GET_BOARD_LIST = "/api/getBoardList";
	public static final String API_ADD_BOARD = "/api/addBoard";
	public static final String API_ADD_NOTI_BOOKMARK = "/api/addNotiBookmark";
	public static final String API_REMOVE_NOTI_BOOKMARK = "/api/removeNotiBookmark";

	public static final String API_ADD_ACTIVITY = "/api/addActivity";
	public static final String API_GET_ACTIVITY_LIST = "/api/getActivityList";

	//비디오 리스트 가져오기
	public static final String API_GET_VIDEOLIST_BY_MASTERID = "/api/getVideoListByMasterGradeId";
	public static final String API_GET_VIDEOLIST_BY_SECTION = "/api/getVideoListByInfoType";

	public static final int NOTIFICATION_STEP = 1001;
	public static final int NOTIFICATION_SCHOOL_ALIMI = 1002;
	public static final int NOTIFICATION_CONSULT = 1003;

	public static final String NOTIFCATION_DESTINATION_FRAGMENT = "des_fragment";
	public static final String CONSULT_CATEGORY = "category";
}
