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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.spectra.sports.constant.SpectraConstant.*;
import static com.spectra.sports.constant.SuccessOrErrorMessages.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
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
        } catch (JsonProcessingException exception) {
            log.error(SOMETHING_WENT_WRONG_WHILE_GENERATING_THE_TOKEN, exception);
        }

        var firstName = user.firstName();
        var email = user.email();
        var subject = String.format(SIGNUP_SUBJECT, firstName.concat(user.lastName()));
        var content = SIGNUP_CONTENT.formatted(domain.concat("/user/verify/").concat(jwtToken));

        Map<String, Object> jsonObject = createEmailContent(email, firstName, subject, content);
        sendEmailRequest(new Gson().toJson(jsonObject));
    }

    @Transactional
    public void sendForgotPasswordVerificationEmail(UserDto user) {
        var otp = generateOtp();
        userRepository.updateUserOtp(otp, user.userId());

        var firstName = user.firstName();
        var email = user.email();
        var emailContent = createEmailContent(email, firstName, OTP_SUBJECT, OTP_MESSAGE.formatted(firstName, otp));
        sendEmailRequest(new Gson().toJson(emailContent));
    }

    @Override
    public void sendSubscriptionEmail(String email, String message) {
        var name = email.substring(0, email.indexOf("@"));
        var emailContent = createEmailContent(email, name, SUBSCRIPTION_SUBJECT, SUBSCRIPTION_CONTENT.formatted(message));
        log.info("Sending the subscription mapping email....");
        sendEmailRequest(new Gson().toJson(emailContent));
    }

    private Map<String, Object> createEmailContent(String email, String name, String subject, String content) {
        HashMap<String, Object> json = new HashMap();
        json.put(SENDER, Map.of(NAME, SPECTRA_SPORTS, EMAIL, SPECTRA_SPORTS_EMAIL));
        json.put(SUBJECT, subject);
        json.put(HTML_CONTENT, content);
        json.put(TO, List.of(Map.of(NAME, name, EMAIL, email)));

        return json;
    }

    private void sendEmailRequest(String content) {
        RequestEntity<String> accept = RequestEntity
                .post(URI.create(SEND_IN_BLUE_URL))
                .header("accept", new String[]{APPLICATION_JSON_VALUE})
                .header("content-type", new String[]{APPLICATION_JSON_VALUE})
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
