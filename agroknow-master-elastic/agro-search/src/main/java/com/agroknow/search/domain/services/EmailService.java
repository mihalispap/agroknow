package com.agroknow.search.domain.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class EmailService {

    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    private final Map<String, String> mailTemplates = new HashMap<String, String>();

    @Async
    public void sendRegistrationMail(String recipient, String key) {
        try {
            String content = buildRegistrationMessage(recipient, key);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipient);
            helper.setFrom(env.getProperty("email.registration.from"), "AgroKnow Search API");
            helper.setSubject(env.getProperty("email.registration.subject"));
            helper.setText(content, true);

            mailSender.send(helper.getMimeMessage());
        } catch (Exception e) {
            log.error("Error in sending registration message to recipient: {}", recipient, e);
        }
    }

    private String buildRegistrationMessage(String recipient, String key) throws IOException {
        String registrationTemplate = mailTemplates.get("registration");
        if (registrationTemplate == null) {
            InputStream tmpl = null;
            try {
                tmpl = this.getClass().getClassLoader().getResourceAsStream("mail_registration.html");
                registrationTemplate = StreamUtils.copyToString(tmpl, Charset.forName("UTF-8"));
                registrationTemplate = (registrationTemplate == null) ? "" : registrationTemplate;
                mailTemplates.put("registration", registrationTemplate);
            } finally {
                if(tmpl!=null) {
                    tmpl.close();
                }
            }
        }

        return registrationTemplate.replace("${name}", recipient.substring(0, recipient.indexOf('@')))
                                   .replace("${key}", key)
                                   .replace("${year}", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
    }
}
