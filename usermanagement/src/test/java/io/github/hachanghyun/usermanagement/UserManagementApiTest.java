package io.github.hachanghyun.usermanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hachanghyun.usermanagement.auth.jwt.JwtTokenProvider;
import io.github.hachanghyun.usermanagement.common.dto.*;
import io.github.hachanghyun.usermanagement.user.entity.User;
import io.github.hachanghyun.usermanagement.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = {"your-topic-name"})
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
@Transactional
class UserManagementApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

//    @BeforeEach
//    public void setup() {
//        userRepository.deleteAll();
//    }

    @Test
    public void 회원가입_성공() throws Exception {
        UserSignupRequest req = UserSignupRequest.builder()
                .account("test123")
                .password("1234")
                .name("홍길동")
                .residentRegistrationNumber("9001011234567")
                .phoneNumber("01012345678")
                .address("서울특별시 강남구 역삼동")
                .build();

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    public void 로그인_및_내정보조회() throws Exception {
        String rawPassword = "1234";
        userRepository.save(User.builder()
                .account("test123")
                .password(passwordEncoder.encode(rawPassword))
                .name("홍길동")
                .residentRegistrationNumber("9001011234567")
                .phoneNumber("01012345678")
                .address("서울특별시 강남구 역삼동")
                .build());

        LoginRequest loginReq = new LoginRequest("test123", rawPassword);

        String token = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(token).get("message").asText(); // "message"로 변경

        mockMvc.perform(get("/users/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account").value("test123"))
                .andExpect(jsonPath("$.topLevelAddress").value("서울특별시"));
    }

    @Test
    public void 관리자_API_조회_수정_삭제() throws Exception {
        User user = userRepository.save(User.builder()
                .account("testuser")
                .password("1234")
                .name("테스트유저")
                .residentRegistrationNumber("8001011234567")
                .phoneNumber("01000001111")
                .address("경기도 성남시 분당구")
                .build());

        String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString("admin:1212".getBytes());

        mockMvc.perform(get("/admin/users")
                        .header("Authorization", basicAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].account").value("testuser"));

        UserUpdateRequest update = new UserUpdateRequest(null, "경기도 수원시");
        mockMvc.perform(put("/admin/users/" + user.getId())
                        .header("Authorization", basicAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("경기도 수원시"));

        mockMvc.perform(delete("/admin/users/" + user.getId())
                        .header("Authorization", basicAuth))
                .andExpect(status().isOk());
    }
}
