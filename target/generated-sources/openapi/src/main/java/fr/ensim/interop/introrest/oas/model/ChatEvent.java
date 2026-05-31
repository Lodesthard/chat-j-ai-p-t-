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
 * ChatEvent
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-29T12:06:06.683943100+02:00[Europe/Paris]")
public class ChatEvent  implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("update_id")
  private Integer updateId;

  @JsonProperty("chat_id")
  private Long chatId;

  @JsonProperty("message_id")
  private Integer messageId;

  @JsonProperty("text")
  private String text;

  public ChatEvent updateId(Integer updateId) {
    this.updateId = updateId;
    return this;
  }

  /**
   * Get updateId
   * @return updateId
  */
  @NotNull 
  @Schema(name = "update_id", required = true)
  public Integer getUpdateId() {
    return updateId;
  }

  public void setUpdateId(Integer updateId) {
    this.updateId = updateId;
  }

  public ChatEvent chatId(Long chatId) {
    this.chatId = chatId;
    return this;
  }

  /**
   * Get chatId
   * @return chatId
  */
  @NotNull 
  @Schema(name = "chat_id", required = true)
  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public ChatEvent messageId(Integer messageId) {
    this.messageId = messageId;
    return this;
  }

  /**
   * Get messageId
   * @return messageId
  */
  @NotNull 
  @Schema(name = "message_id", required = true)
  public Integer getMessageId() {
    return messageId;
  }

  public void setMessageId(Integer messageId) {
    this.messageId = messageId;
  }

  public ChatEvent text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  @NotNull 
  @Schema(name = "text", required = true)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChatEvent chatEvent = (ChatEvent) o;
    return Objects.equals(this.updateId, chatEvent.updateId) &&
        Objects.equals(this.chatId, chatEvent.chatId) &&
        Objects.equals(this.messageId, chatEvent.messageId) &&
        Objects.equals(this.text, chatEvent.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(updateId, chatId, messageId, text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChatEvent {\n");
    sb.append("    updateId: ").append(toIndentedString(updateId)).append("\n");
    sb.append("    chatId: ").append(toIndentedString(chatId)).append("\n");
    sb.append("    messageId: ").append(toIndentedString(messageId)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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

