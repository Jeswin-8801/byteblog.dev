package com.jeswin8801.byteBlog.service.mail.abstracts;

import com.jeswin8801.byteBlog.service.mail.MailType;
import org.springframework.util.MultiValueMap;

public interface EmailService {
    void sendMail(MailType mailType, String destinationEmail, MultiValueMap<String, String> queryParams);
}
