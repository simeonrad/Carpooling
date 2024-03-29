package com.telerikacademy.web.carpooling.models;

import java.util.Optional;

public class FilterFeedbackOptions {
    private Optional<String> author;
    private Optional<String> recipient;
    private Optional<String> comment;
    private Optional<Integer> rating;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilterFeedbackOptions(String author,
                                 String recipient,
                                 String sortBy,
                                 String sortOrder) {
        this.author = Optional.ofNullable(author);
        this.recipient = Optional.ofNullable(recipient);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public FilterFeedbackOptions(String author,
                                 String recipient,
                                 String comment,
                                 int rating,
                                 String sortBy,
                                 String sortOrder) {
        this.author = Optional.ofNullable(author);
        this.recipient = Optional.ofNullable(recipient);
        this.comment = Optional.ofNullable(comment);
        this.rating = Optional.ofNullable(rating);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getAuthor() {
        return author;
    }

    public void setAuthor(Optional<String> author) {
        this.author = author;
    }

    public Optional<String> getRecipient() {
        return recipient;
    }

    public void setRecipient(Optional<String> recipient) {
        this.recipient = recipient;
    }

    public Optional<String> getComment() {
        return comment;
    }

    public void setComment(Optional<String> comment) {
        this.comment = comment;
    }

    public Optional<Integer> getRating() {
        return rating;
    }

    public void setRating(Optional<Integer> rating) {
        this.rating = rating;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public void setSortBy(Optional<String> sortBy) {
        this.sortBy = sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Optional<String> sortOrder) {
        this.sortOrder = sortOrder;
    }
}
