package com.wini.leader_board_integration.data.model.leaderboard;

import java.io.Serializable;
import java.util.Objects;

public class Node implements Comparable<Node> , Serializable {
    private static final long serialVersionUID = 1L;
    private String playerId;
    private int code;

    public Node(String playerId, int code) {
        this.playerId = playerId;
        this.code = code;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getCode() {
        return code;
    }


    @Override
    public String toString() {
        return "Node{" +
                "playerId='" + playerId + '\'' +
                ", code=" + code +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return code == node.code &&
                Objects.equals(playerId, node.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, code);
    }

    @Override
    public int compareTo(Node o1) {
        int result1 = Integer.compare(o1.getCode(), this.getCode());
        if (result1 == 0)
            return o1.getPlayerId().compareTo(this.getPlayerId());
        return result1;
    }
}