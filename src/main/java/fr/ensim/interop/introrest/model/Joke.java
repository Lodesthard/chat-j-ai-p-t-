package fr.ensim.interop.introrest.model;

/**
 * Une blague telle que decrite dans le sujet : un id, un titre, un texte et une note sur 10.
 */
public class Joke {
    private Long id;
    private String title;
    private String text;
    private Integer rating;

    public Joke() {}

    public Joke(Long id, String title, String text, Integer rating) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.rating = rating;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
