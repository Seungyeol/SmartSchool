package com.aura.smartschool.database;

public enum ConsultType {
    //학교폭력, 친구관계, 가정문제, 성상담, 학업상담, 진로상담, 심리상담, 성장상담, 흡연상담.
    SCHOOL_VIOLENCE_CONSULT("학교폭력", "tb_school_violence_fail", 1001),
    FRIEND_RELATIONSHIP_CONSULT("친구관계", "tb_friend_relationship_fail", 1002),
    FAMILY_CONSULT("가정문제", "tb_family_fail", 1003),
    SEXUAL_CONSULT("성상담", "tb_sexual_fail", 1),
    ACADEMIC_CONSULT("학업상담", "tb_academic_fail", 2),
    CAREER_CONSULT("진로상담", "tb_career_fail", 3),
    PSYCHOLOGY_CONSULT("심리상담", "tb_psychology_fail", 4),
    GROWTH_CONSULT("성장상담", "tb_growth_fail", 5),
    SMOKING_CONSULT("흡연상담", "tb_smoking_fail", 6);

    public static final int MSG_FROM_STUDENT = 0;
    public static final int MSG_FROM_TEACHER = 1;

    public String consultName;
    public String failTableName;
    public int code;

    private ConsultType (String consultName, String failTableName, int code) {
        this.consultName = consultName;
        this.failTableName = failTableName;
        this.code = code;
    }

    public static ConsultType findConsultTypeByConsultName(String consultName) {
        ConsultType[] types = values();
        for (ConsultType type:types) {
            if (type.consultName.equalsIgnoreCase(consultName)) {
                return type;
            }
        }
        return null;
    }

    public static ConsultType findConsultTypeByConsultCode(int code) {
        ConsultType[] types = values();
        for (ConsultType type:types) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
