package com.campaign.subscription.campaignenum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryType {
    GAMING("Gaming"),
    BANKING("Banking"),
    MOVIE("Movie"),
    MUSIC("Music");

    private String type;

    CategoryType(String type) {

        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
