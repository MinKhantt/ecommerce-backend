package com.example.ecommercebackend.helper;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailHelper {
    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine;

    @Async
    public void sendOrderConfirmation(String toEmail, OrderEmailContext ctx) {
        try {
            Context context = new Context();
            context.setVariable("ctx", ctx);

            String htmlContent = templateEngine.process("emails/order-confirmation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Order Confirmation - #" + ctx.orderId());
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Order confirmation email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }
}
