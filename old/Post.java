public class Post extends PlatformEntity {
    private final User author;
    private String content;
    private int likes;

    public Post(int id, User author, String content) {
        super(id);
        this.author = author;
        this.content = content;
        this.likes = 0;
    }

    public User getAuthor() {
        return author;
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

    public void like() {
        likes++;
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + getId() + ", author=" + author.getName() + ", likes=" + likes +
                ", content='" + content + "'}";
    }

}
