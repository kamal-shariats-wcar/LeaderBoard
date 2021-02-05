package com.wini.leader_board_integration.data.dto;

import java.io.Serializable;

public class TempItem implements Serializable {
    private String itemId;
    private Integer count;

    public TempItem(String itemId, Integer count) {
        this.itemId = itemId;
        this.count = count;
    }

    public TempItem() {
    }

    public String getItemId() {
        return itemId;
    }

    public Integer getCount() {
        return count;
    }

    public com.wini.leader_board_integration.data.dto.TempItem updateCount() {
        this.count = this.count + 1;
        return this;
    }
}
