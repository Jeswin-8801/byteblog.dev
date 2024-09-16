package com.jeswin8801.byteBlog.service.mail;

import com.jeswin8801.byteBlog.config.ApplicationProperties;
import com.jeswin8801.byteBlog.service.mail.abstracts.EmailService;
import com.jeswin8801.byteBlog.util.MailUtil;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private ApplicationProperties properties;

    @Override
    public void sendMail(MailType mailType, String destinationEmail, MultiValueMap<String, String> queryParams) {
        new Thread(() -> {
            switch (mailType) {
                case VERIFY_EMAIL -> sendVerificationEmail(destinationEmail, queryParams);
                case PASSWORD_RESET_EMAIL -> sendPasswordResetEmail(destinationEmail, queryParams);
            }
        }).start();
    }

    private void sendVerificationEmail(String destinationEmail, MultiValueMap<String, String> appendQueryParamsToVerifyEmailLink) {
        log.info("Initiated: sendVerificationEmail - to {} ", destinationEmail);
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Map<String, String> templateData = new HashMap<>() {{
                put(
                        MailUtil.TemplateKeys.linkEmailVerification,
                        UriComponentsBuilder.fromUriString(properties.getMail().getDestinationUrl())
                                .queryParams(appendQueryParamsToVerifyEmailLink)
                                .queryParam("isProcessVerifyEmail", true)
                                .build().toUriString()

                );
            }};

            String templateContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freeMarkerConfigurer.getConfiguration().getTemplate(
                            MailUtil.MailTemplatePathEnums.EMAIL_VERIFICATION_MAIL.getTemplatePath()
                    ),
                    templateData
            );

            // Sending email
            helper.setTo(destinationEmail);
            helper.setSubject("Verify your email");
            helper.setText(templateContent, true);
            emailSender.send(message);

        } catch (MessagingException | IOException |
                 TemplateException e) {
            log.error("sendVerificationEmail failed with Exception: {} ", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendPasswordResetEmail(String destinationEmail, MultiValueMap<String, String> appendQueryParamsToPasswordResetLink) {
        log.info("Initiated: sendPasswordResetEmail - to {} ", destinationEmail);
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Map<String, String> templateData = new HashMap<>() {{
                put(
                        MailUtil.TemplateKeys.linkPasswordReset,
                        UriComponentsBuilder.fromUriString(properties.getMail().getDestinationUrl())
                                .queryParams(appendQueryParamsToPasswordResetLink)
                                .queryParam("isProcessPasswordReset", true)
                                .build().toUriString()

                );
            }};

            String templateContent = FreeMarkerTemplateUtils.processTemplateIntoString(
                    freeMarkerConfigurer.getConfiguration().getTemplate(
                            MailUtil.MailTemplatePathEnums.RESET_PASSWORD_MAIL.getTemplatePath()
                    ),
                    templateData
            );

            // Sending email
            helper.setTo(destinationEmail);
            helper.setSubject("Password reset request for byteblog");
            helper.setText(templateContent, true);
            emailSender.send(message);

        } catch (MessagingException | IOException | TemplateException e) {
            log.error("sendPasswordResetEmail failed with Exception: {} ", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
