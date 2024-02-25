package com.telerikacademy.web.carpooling.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_blocks")
public class UserBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "block_id")
    private int blockId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_Id")
    private User user;

    @Column(name = "block_expire_timestamp")
    private LocalDateTime blockExpireTimestamp;

    public UserBlock() {
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getBlockExpireTimestamp() {
        return blockExpireTimestamp;
    }

    public void setBlockExpireTimestamp(LocalDateTime blockExpireTimestamp) {
        this.blockExpireTimestamp = blockExpireTimestamp;
    }
}
