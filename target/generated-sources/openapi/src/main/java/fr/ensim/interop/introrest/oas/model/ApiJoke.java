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
 * ApiJoke
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-29T12:06:06.683943100+02:00[Europe/Paris]")
public class ApiJoke  implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("id")
  private Long id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("text")
  private String text;

  @JsonProperty("rating")
  private Integer rating;

  public ApiJoke id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull 
  @Schema(name = "id", required = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ApiJoke title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Titre de la blague.
   * @return title
  */
  @NotNull 
  @Schema(name = "title", description = "Titre de la blague.", required = true)
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ApiJoke text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Texte de la blague.
   * @return text
  */
  @NotNull 
  @Schema(name = "text", description = "Texte de la blague.", required = true)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public ApiJoke rating(Integer rating) {
    this.rating = rating;
    return this;
  }

  /**
   * Note sur 10.
   * minimum: 0
   * maximum: 10
   * @return rating
  */
  @NotNull @Min(0) @Max(10) 
  @Schema(name = "rating", description = "Note sur 10.", required = true)
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
    ApiJoke apiJoke = (ApiJoke) o;
    return Objects.equals(this.id, apiJoke.id) &&
        Objects.equals(this.title, apiJoke.title) &&
        Objects.equals(this.text, apiJoke.text) &&
        Objects.equals(this.rating, apiJoke.rating);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, text, rating);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiJoke {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

