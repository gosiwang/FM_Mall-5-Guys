// 인트로 표시 여부 확인 (sessionStorage 사용)
export const shouldShowIntro = () => {
    return !sessionStorage.getItem('hasVisitedFMMall');
};

// 인트로 본 것으로 표시 (sessionStorage에 저장)
export const markIntroAsViewed = () => {
    sessionStorage.setItem('hasVisitedFMMall', 'true');
};

// 테스트용: 인트로 초기화
export const resetIntro = () => {
    sessionStorage.removeItem('hasVisitedFMMall');
};