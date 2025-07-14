# basic auth 처리 플로우
| 클래스                                                 | 역할                                                           |
|-----------------------------------------------------| ------------------------------------------------------------ |
| `JwtAuthenticationFilter` (내가 만든 커스텀 필터)            | `Authorization: Bearer <토큰>`이 있을 경우 → JWT 인증 처리              |
| `BasicAuthenticationFilter` (Spring Security 내장 필터) | `Authorization: Basic <base64>` 헤더가 있을 경우 → Basic Auth 인증 처리 |
| `DaoAuthenticationProvider`                         | 내부적으로 `UserDetailsService` 호출 → 사용자 정보 로딩 + 비밀번호 비교          |
| `InMemoryUserDetailsManager`                        | `loadUserByUsername("admin")` 으로 admin 계정 정보 반환              |
| `PasswordEncoder`                                   | 입력한 "1212" 비밀번호와 저장된 해시값을 비교                                 |
| `FilterSecurityInterceptor`                         | `@PreAuthorize` 또는 `.hasRole("ADMIN")` 같은 접근 제어 판단 수행        |
| `SecurityContextHolder`                             | 인증 성공 시 Authentication 객체 저장 → 이후 컨트롤러에서 사용 가능               |

    [사용자 요청 - PUT /admin/users/3000 (Basic Auth 포함)]
    │
    ▼
    [JwtAuthenticationFilter] (패스: Bearer 없음)
    │
    ▼
    [BasicAuthenticationFilter]
    │
    ▼
    [AuthenticationManager.authenticate()]
    │
    ▼
    [DaoAuthenticationProvider]
    │
    ▼
    userDetailsService.loadUserByUsername("admin")
    │
    ▼
    passwordEncoder.matches("입력:1212", 저장된 해시)
    │
    ▼
    [인증 성공] → SecurityContextHolder.setAuthentication()
    │
    ▼
    [FilterSecurityInterceptor] → hasRole("ADMIN") 통과
    │
    ▼
    [AdminUserController] 도달

# Q 필터체인이 빈에 등록되면 자동으로 체인에 등록되는 이유?
    항목	필수 여부
    메서드 이름 (filterChain)	❌ 전혀 아님. 마음대로 지어도 됨 (security(), apiFilterChain() 등)
    @Bean 어노테이션	✅ 반드시 필요
    리턴 타입: SecurityFilterChain	✅ 반드시 필요
    파라미터: HttpSecurity http

# jwt
    1. Authorization: Bearer ... 헤더 감지
       String authHeader = request.getHeader("Authorization");
       if (authHeader != null && authHeader.startsWith("Bearer ")) {
       → JWT 헤더가 있을 때만 인증 시도

    2. JWT 유효성 검증
    if (jwtTokenProvider.validateToken(token)) {
    → 토큰의 서명, 만료 시간 등을 검증 (JwtTokenProvider.validateToken() 내부)
    
    3. JWT에서 계정(account) 추출
    String account = jwtTokenProvider.getAccount(token);
    → JWT subject를 account로 사용 (ex: setSubject(account))
    
    4. DB(UserRepository)에서 사용자 정보 조회
    User user = userRepository.findByAccount(account).orElse(null);
    → JWT에서 추출한 account로 사용자 엔티티를 DB에서 가져옴
    
    5. UsernamePasswordAuthenticationToken 생성
    UsernamePasswordAuthenticationToken authentication =
    new UsernamePasswordAuthenticationToken(user, null, null);
    → 사용자 객체만 넣은 인증 토큰 생성 (권한 정보는 null이지만 인증은 됨)
    
    6. SecurityContextHolder에 인증 정보 저장
    SecurityContextHolder.getContext().setAuthentication(authentication);
    → 이걸로 "인증 완료" 상태가 됨
    → 이후 컨트롤러에서 @AuthenticationPrincipal 또는 SecurityContextHolder.getContext().getAuthentication() 으로 사용자 접근 가능