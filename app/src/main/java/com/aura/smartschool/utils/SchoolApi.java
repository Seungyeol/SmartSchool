package com.aura.smartschool.utils;

import com.aura.smartschool.vo.MenuData;
import com.aura.smartschool.vo.ScheduleData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by eastflag on 2015-08-11.
 */
public class SchoolApi {
    /**
     * 학교 종류
     */
    public static class SchoolType {
        public static final int KINDERGARTEN = 1;
        public static final int ELEMENTARY = 2;
        public static final int MIDDLE = 3;
        public static final int HIGH = 4;
    }

    /**
     * 지역 관할 교육청 코드
     */
    public static class Country {
        public static final String SEOUL = "stu.sen.go.kr";
        public static final String ULSAN = "stu.use.go.kr";
        public static final String JEONBUK = "stu.jbe.go.kr";
        public static final String BUSAN = "stu.pen.go.kr";
        public static final String SEJONG = "stu.sje.go.kr";
        public static final String JEONNAM = "stu.jne.go.kr";
        public static final String DAEGU = "stu.dge.go.kr";
        public static final String GYEONGGI = "stu.goe.go.kr";
        public static final String GYEONGBUK = "stu.gbe.go.kr";
        public static final String INCHEON = "stu.ice.go.kr";
        public static final String KANGWON = "stu.kwe.go.kr";
        public static final String GYEONGNAM = "stu.gne.go.kr";
        public static final String GWANGJU = "stu.gen.go.kr";
        public static final String CHUNGBUK = "stu.cbe.go.kr";
        public static final String JEJU = "stu.jje.go.kr";
        public static final String DAEJEON = "stu.dje.go.kr";
        public static final String CHUNGNAM = "stu.cne.go.kr";
    }

    /**
     * 급식 식단표 정보(월간) URL
     */
    public static final String MENU_URL = "sts_sci_md00_001.do";

    /**
     * 학사일정 정보(월간) URL
     */
    public static final String SCHEDULE_URL = "sts_sci_sf01_001.do";

    public static MenuData[] getMonthlyMenu(String countryCode, String schoolCode, int schoolType,
                                            int year, int month) {

        StringBuffer targetURL = new StringBuffer("http://");
        targetURL.append(countryCode);
        targetURL.append("/");
        targetURL.append(MENU_URL);

        try {
            Document menuData = Jsoup.connect(targetURL.toString()).data("schulCode", schoolCode)
                    .data("schulCrseScCode", schoolType + "")
                    .data("schulKndScCode", "0" + schoolType)
                    //.data("schYm", year + "." + asTwoWord(month)).post();
                    .data("ay", String.valueOf(year))
                    .data("mm", String.format("%02d", month))
                    .post();

            return menuParser(menuData);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 월간 학사 일정을 가져온다.
     */
    public static ScheduleData[] getMonthlySchedule(String countryCode, String schoolCode,
                                                    int schoolType, int year, int month) {

        StringBuffer targetURL = new StringBuffer("http://");
        targetURL.append(countryCode);
        targetURL.append("/");
        targetURL.append(SCHEDULE_URL);

        try {
            Document scheduleData = Jsoup.connect(targetURL.toString())
                    .data("schulCode", schoolCode).data("schulCrseScCode", schoolType + "")
                    .data("schulKndScCode", "0" + schoolType)
                    .data("ay", "" + year)
                    .data("mm", String.format("%02d", month))
                    .post();

            return scheduleParser(scheduleData);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static MenuData[] menuParser(Document menuData) throws NullPointerException {

        MenuData[] monthlyMenu = new MenuData[31];

        Element month = menuData.getElementById("contents").getElementsByClass("sub_con").first()
                .getElementsByClass("tbl_calendar").first().getElementsByTag("tbody").first();

        // 월 단위 메뉴 목록
        Elements weeks = month.getElementsByTag("tr");
        for (Element week : weeks) {

            // 주 단위 메뉴 목록
            Elements days = week.getElementsByTag("td");

            for (Element day : days) {
                // 일 단위 메뉴 목록
                Elements menus = day.getElementsByTag("div").first().getAllElements();

                //Elements menus = day.getElementsByTag("div").first().getAllElements();
                for (Element menu : menus) {

                    // 빈 메뉴가 아닐 경우에만 프로세싱
                    String data = menu.text().trim();
                    if (data.length() < 1)
                        continue;

                    // 불필요한 문자를 제거한다.
                    data = data.replace("(석식)", "");

                    // 급식 데이터의 첫 단위는 날짜이다.
                    String[] chunk = data.split(" ");
                    int date = Integer.valueOf(chunk[0]);

                    // 급식 정보가 존재할 경우에만 데이터를 쓴다.
                    if (chunk.length > 1)
                        monthlyMenu[date - 1] = new MenuData(data);
                    else
                        monthlyMenu[date - 1] = new MenuData();
                }
            }
        }

        return monthlyMenu;
    }

    public static ScheduleData[] scheduleParser(Document scheduleData) throws NullPointerException {

        ScheduleData[] monthlySchedule = new ScheduleData[31];

        Element month = scheduleData.getElementById("contents").getElementsByClass("sub_con")
                .first().getElementsByClass("tbl_calendar").first().getElementsByTag("tbody")
                .first();

        // 월간 일정 목록
        Elements weeks = month.getElementsByTag("tr");
        for (Element week : weeks) {

            // 주간 일정 목록
            Elements days = week.getElementsByTag("td");
            for (Element day : days) {

                // 일간 일정
                Element schedule = day.getElementsByTag("div").first();

                String dateString = schedule.getElementsByTag("em").first().text();
                if (dateString.length() < 1)
                    continue;

                // 일자 취득
                int date = Integer.valueOf(dateString);

                // 일정 취득
                String events = schedule.getElementsByTag("a").text().trim();

                if (events.length() > 1)
                    monthlySchedule[date - 1] = new ScheduleData(events);
                else
                    monthlySchedule[date - 1] = new ScheduleData();

            }
        }

        return monthlySchedule;
    }


    public static  String getContry(String contry) {
        if ("서울".equals(contry)) {
            return Country.SEOUL;
        } else if ("울산".equals(contry)) {
            return Country.ULSAN;
        } else if ("전북".equals(contry)) {
            return Country.JEONBUK;
        } else if ("부산".equals(contry)) {
            return Country.BUSAN;
        } else if ("세종".equals(contry)) {
            return Country.SEJONG;
        } else if ("전남".equals(contry)) {
            return Country.JEONNAM;
        } else if ("대구".equals(contry)) {
            return Country.DAEGU;
        } else if ("경기".equals(contry)) {
            return Country.GYEONGGI;
        } else if ("경북".equals(contry)) {
            return Country.GYEONGBUK;
        } else if ("인천".equals(contry)) {
            return Country.INCHEON;
        } else if ("강원".equals(contry)) {
            return Country.KANGWON;
        } else if ("경남".equals(contry)) {
            return Country.GYEONGNAM;
        } else if ("광주".equals(contry)) {
            return Country.GWANGJU;
        } else if ("충북".equals(contry)) {
            return Country.CHUNGBUK;
        } else if ("제주".equals(contry)) {
            return Country.JEJU;
        } else if ("대전".equals(contry)) {
            return Country.DAEJEON;
        } else if ("충남".equals(contry)) {
            return Country.CHUNGNAM;
        } else {
            return null;
        }
    }

    public static int getSchoolType(String type) {
        if(type.contains("유치원")) {
            return SchoolType.KINDERGARTEN;
        } else if(type.contains("초등")) {
            return SchoolType.ELEMENTARY;
        }  else if(type.contains("중학")) {
            return SchoolType.MIDDLE;
        } else if(type.contains("고등")) {
            return SchoolType.HIGH;
        } else {
            return 0;
        }
    }
}
