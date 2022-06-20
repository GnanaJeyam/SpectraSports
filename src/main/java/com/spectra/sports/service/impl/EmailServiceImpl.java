//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.spectra.sports.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.spectra.sports.dto.UserDto;
import com.spectra.sports.helper.JwtHelper;
import com.spectra.sports.repository.UserRepository;
import com.spectra.sports.service.EmailService;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailServiceImpl implements EmailService {
    private static final String SENDER = "sender";
    private static final String TO = "to";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String SUBJECT = "subject";
    private static final String HTML_CONTENT = "htmlContent";
    private static final String SENDINBLUE_URL = "https://api.sendinblue.com/v3/smtp/email";

    public static final String DOMAIN = "DOMAIN";

    private final RestTemplate restTemplate;
    private final JwtHelper jwtHelper;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final String domain;

    @Value("${spectra-sports.email.api-key}")
    private String emailApiKey;

    public EmailServiceImpl(RestTemplate restTemplate,
                            JwtHelper jwtHelper,
                            UserRepository userRepository,
                            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        boolean hasDomain = System.getenv().containsKey(DOMAIN);
        this.domain = (hasDomain ? System.getenv(DOMAIN) : System.getProperty(DOMAIN, "localhost")).concat(":8080");
    }

    public void sendSignUpVerificationEmail(UserDto user) {
        String jwtToken = null;

        try {
            jwtToken = jwtHelper.createToken(user);
        } catch (JsonProcessingException var6) {

        }

        var subject = String.format("Welcome to SpectraSports, %s", user.firstName().concat(user.lastName()));
        var content = """
            <html>
            <body>
            <p> Please click this <a href=\"%s\">link</a> to verify</p>
            </body>
            </html>
            """.formatted(domain.concat("/user/verify/").concat(jwtToken));

        Map<String, Object> jsonObject = createEmailContent(user, subject, content);
        sendEmailRequest(new Gson().toJson(jsonObject));
    }

    @Transactional
    public void sendForgotPasswordVerificationEmail(UserDto user) {
        var otp = this.generateOtp();
        userRepository.updateUserOtp(otp, user.userId());
        var subject = "Welcome back to SpectraSports";
        var body = """
                <html>
                <body>
                <h5>Hello %s </h5>
                <p>Please find your OTP below.</p>
                <p style=\"font-size:40px\"> %s </p>
                </body>   
                </html>
            """.formatted(user.firstName(), otp);
        var emailContent = this.createEmailContent(user, subject, body);
        sendEmailRequest(new Gson().toJson(emailContent));
    }

    private Map<String, Object> createEmailContent(UserDto user, String subject, String content) {
        HashMap<String, Object> json = new HashMap();
        json.put(SENDER, Map.of(NAME, "SpectraSports", EMAIL, "spectra.sports@support.com"));
        json.put(SUBJECT, subject);
        json.put(HTML_CONTENT, content);
        json.put(TO, List.of(Map.of(NAME, user.firstName(), EMAIL, user.email())));

        return json;
    }

    private void sendEmailRequest(String content) {
        RequestEntity<String> accept = RequestEntity
                .post(URI.create("https://api.sendinblue.com/v3/smtp/email"))
                .header("accept", new String[]{"application/json"})
                .header("content-type", new String[]{"application/json"})
                .header("api-key", new String[]{this.emailApiKey})
                .body(content);

        restTemplate.exchange(accept, String.class);
    }

    private String generateOtp() {
        String numbers = "1234567890";
        Random random = new Random();
        char[] otp = new char[6];

        for(int i = 0; i < 6; ++i) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }

        return String.valueOf(otp);
    }
}
