package com.telerikacademy.web.carpooling.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private int id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "travel_id", nullable = false, foreignKey = @ForeignKey(name = "feedbacks_ibfk_1"))
    private Travel travel;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "feedbacks_ibfk_2"))
    private User author;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "recipient_id", nullable = false, foreignKey = @ForeignKey(name = "feedbacks_ibfk_3"))
    private User recipient;

    @Column(name = "rating", nullable = false)
    @Min(0)
    @Max(5)
    private int rating;

    @JsonManagedReference
    @OneToOne(mappedBy = "feedback", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private FeedbackComment comment;

    public Feedback() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public FeedbackComment getComment() {
        return comment;
    }

    public void setComment(FeedbackComment comments) {
        this.comment = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback feedback = (Feedback) o;
        return Objects.equals(author, feedback.author) && Objects.equals(recipient, feedback.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, recipient);
    }
}
