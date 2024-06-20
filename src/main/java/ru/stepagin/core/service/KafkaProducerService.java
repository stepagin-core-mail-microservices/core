package ru.stepagin.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.stepagin.core.entity.ImageEntity;
import ru.stepagin.core.entity.UserEntity;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, ImageEntity> imageKafkaTemplate;
    private final KafkaTemplate<String, UserEntity> userKafkaTemplate;
    @Value(value = "${app.kafka.topics.image.name}")
    private String imageTopicName;
    @Value(value = "${app.kafka.topic.user.name}")
    private String userTopicName;

    public void sendImage(ImageEntity image) {
        imageKafkaTemplate.send(imageTopicName, image.getId().toString(), image);
        imageKafkaTemplate.flush();
    }

    public void sendUser(UserEntity user) {
        userKafkaTemplate.send(userTopicName, user.getLogin(), user);
        userKafkaTemplate.flush();
    }
}
