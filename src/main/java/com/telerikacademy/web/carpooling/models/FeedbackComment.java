package com.telerikacademy.web.carpooling.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback_comments")
public class FeedbackComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int id;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "feedback_id", nullable = false, foreignKey = @ForeignKey(name = "feedback_comments_ibfk_1"))
    private Feedback feedback;

    @Column(name = "comment", nullable = false)
    private String comment;

    public FeedbackComment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
