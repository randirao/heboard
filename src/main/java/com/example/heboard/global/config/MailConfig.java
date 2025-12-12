package com.example.heboard.global.config;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;
import java.util.Properties;

@Configuration
public class MailConfig {

    /**
     * 메일 기능을 사용하지 않을 때도 애플리케이션이 부팅되도록
     * No-Op JavaMailSender 빈을 등록한다. 실메일 전송은 수행하지 않는다.
     */
    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender() {
        return new NoOpMailSender();
    }

    @Slf4j
    private static class NoOpMailSender implements JavaMailSender {

        private final Session session = Session.getInstance(new Properties());

        @Override
        public MimeMessage createMimeMessage() {
            return new MimeMessage(session);
        }

        @Override
        public MimeMessage createMimeMessage(InputStream contentStream) {
            try {
                return new MimeMessage(session, contentStream);
            } catch (Exception e) {
                log.warn("Failed to create MimeMessage from stream", e);
                return new MimeMessage(session);
            }
        }

        @Override
        public void send(MimeMessage mimeMessage) {
            log.info("Skip sending MimeMessage (noop)");
        }

        @Override
        public void send(MimeMessage... mimeMessages) {
            log.info("Skip sending {} MimeMessages (noop)", mimeMessages.length);
        }

        @Override
        public void send(MimeMessagePreparator mimeMessagePreparator) {
            log.info("Skip sending MimeMessagePreparator (noop)");
        }

        @Override
        public void send(MimeMessagePreparator... mimeMessagePreparators) {
            log.info("Skip sending {} MimeMessagePreparators (noop)", mimeMessagePreparators.length);
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) {
            log.info("Skip sending SimpleMailMessage to {} (noop)", (Object[]) simpleMessage.getTo());
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) {
            log.info("Skip sending {} SimpleMailMessages (noop)", simpleMessages.length);
        }
    }
}
