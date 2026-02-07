package com.socialmedia.model;

import java.time.LocalDateTime;


public class Post extends PlatformEntity {

    private int authorId;
    private String authorName;  // denormalized for display
    private String content;
    private int likes;
    private LocalDateTime createdAt;

    public Post(int id, int authorId, String authorName, String content, int likes, LocalDateTime createdAt) {
        super(id);
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.likes = likes;
        this.createdAt = createdAt;
    }

    // convenience constructor without timestamp
    public Post(int id, int authorId, String authorName, String content, int likes) {
        this(id, authorId, authorName, content, likes, LocalDateTime.now());
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    /** encapsulation: likes can only go up by 1, never set to arbitrary value */
    public void like() {
        this.likes++;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Business feature: returns the number of words in the post content.
     */
    public int getWordCount() {
        if (content == null || content.isBlank()) return 0;
        return content.trim().split("\\s+").length;
    }

    @Override
    public String toString() {
        return "Post{id=" + getId() + ", author=" + authorName + ", likes=" + likes +
                ", content='" + content + "'}";
    }
}
