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
 * SendMessageRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-29T12:06:06.683943100+02:00[Europe/Paris]")
public class SendMessageRequest  implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("chat_id")
  private Long chatId;

  @JsonProperty("text")
  private String text;

  @JsonProperty("reply_to_message_id")
  private Integer replyToMessageId;

  public SendMessageRequest chatId(Long chatId) {
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

  public SendMessageRequest text(String text) {
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

  public SendMessageRequest replyToMessageId(Integer replyToMessageId) {
    this.replyToMessageId = replyToMessageId;
    return this;
  }

  /**
   * Identifiant du message Telegram a citer.
   * @return replyToMessageId
  */
  
  @Schema(name = "reply_to_message_id", description = "Identifiant du message Telegram a citer.", required = false)
  public Integer getReplyToMessageId() {
    return replyToMessageId;
  }

  public void setReplyToMessageId(Integer replyToMessageId) {
    this.replyToMessageId = replyToMessageId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SendMessageRequest sendMessageRequest = (SendMessageRequest) o;
    return Objects.equals(this.chatId, sendMessageRequest.chatId) &&
        Objects.equals(this.text, sendMessageRequest.text) &&
        Objects.equals(this.replyToMessageId, sendMessageRequest.replyToMessageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chatId, text, replyToMessageId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SendMessageRequest {\n");
    sb.append("    chatId: ").append(toIndentedString(chatId)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    replyToMessageId: ").append(toIndentedString(replyToMessageId)).append("\n");
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

