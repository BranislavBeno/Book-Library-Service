package com.book.library.recommendation;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public record DefaultBookRecommendationListener(
        BookRecommendationService service,
        MailSender mailSender,
        boolean autoConfirmCollaborations,
        String confirmEmailFromAddress,
        String externalUrl)
        implements BookRecommendationListener {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBookRecommendationListener.class);

    @Override
    @SqsListener("${custom.recommendation-queue}")
    public void listenToMessages(BookRecommendationNotification payload) {
        LOG.info("Incoming book recommendation payload: {}", payload);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(confirmEmailFromAddress);
        message.setTo(payload.getRecommencedEmail());
        message.setSubject("A book was recommended to you");
        message.setText("""
                                Hi %s,\s

                                someone recommended a book from %s to you.

                                Book title and author: %s\s

                                You can accept the collaboration by clicking this link: %s/book/%s/recommend/%s/confirm?token=%s\s

                                Kind regards,\s
                                Book library service""".formatted(
                        payload.getRecommencedName(),
                        externalUrl,
                        payload.getBookInfo(),
                        externalUrl,
                        payload.getBookId(),
                        payload.getRecommencedId(),
                        payload.getToken()));
        mailSender.send(message);

        LOG.info("Successfully informed reader about recommended book.");
    }
}
