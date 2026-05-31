package fr.ensim.interop.introrest.oas.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * JokeRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-29T12:06:06.683943100+02:00[Europe/Paris]")
public class JokeRequest  implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("title")
  private String title;

  @JsonProperty("text")
  private String text;

  @JsonProperty("rating")
  private Integer rating;

  public JokeRequest title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  @NotNull @Size(min = 1) 
  @Schema(name = "title", required = true)
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public JokeRequest text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  @NotNull @Size(min = 1) 
  @Schema(name = "text", required = true)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public JokeRequest rating(Integer rating) {
    this.rating = rating;
    return this;
  }

  /**
   * Note sur 10 (defaut 5 si absente).
   * minimum: 0
   * maximum: 10
   * @return rating
  */
  @Min(0) @Max(10) 
  @Schema(name = "rating", description = "Note sur 10 (defaut 5 si absente).", required = false)
  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JokeRequest jokeRequest = (JokeRequest) o;
    return Objects.equals(this.title, jokeRequest.title) &&
        Objects.equals(this.text, jokeRequest.text) &&
        Objects.equals(this.rating, jokeRequest.rating);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, text, rating);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class JokeRequest {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    rating: ").append(toIndentedString(rating)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

