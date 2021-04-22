package com.campaign.subscription.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Sequence")
public class Sequence {
    @Id
    private String Id;
    private int sequence;

    public int getSequence() {
        return sequence;
    }
}
