package com.agroknow.search.config.email;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:/com/agroknow/search/config/email/email.properties")
public class EmailConfig {

    @Autowired
    private Environment env;

    @Bean(name = "mailSender")
    public JavaMailSenderImpl mailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(env.getProperty("email.host"));
        mailSender.setUsername(env.getProperty("email.user"));
        mailSender.setPassword(env.getProperty("email.password"));
        mailSender.setPort(Integer.parseInt(env.getProperty("email.port")));
        mailSender.setProtocol("smtp");

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", env.getProperty("email.usetls","true"));
        mailSender.setJavaMailProperties(mailProps);

        return mailSender;
    }
}
