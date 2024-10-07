package ezen.ezenbulja.controller;

public class SessionConst {
    public static final String LOGIN_MEMBER = "loginMember"; // 로그인한 사용자의 세션 키

    // 생성자를 private으로 정의하여 인스턴스화 방지
    private SessionConst() {
        throw new UnsupportedOperationException("SessionConst is a utility class and cannot be instantiated.");
    }
}
