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
 * SentMessageResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-05-29T12:06:06.683943100+02:00[Europe/Paris]")
public class SentMessageResponse  implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("ok")
  private Boolean ok;

  @JsonProperty("message_id")
  private Integer messageId;

  @JsonProperty("chat_id")
  private Long chatId;

  @JsonProperty("text")
  private String text;

  public SentMessageResponse ok(Boolean ok) {
    this.ok = ok;
    return this;
  }

  /**
   * Get ok
   * @return ok
  */
  @NotNull 
  @Schema(name = "ok", required = true)
  public Boolean getOk() {
    return ok;
  }

  public void setOk(Boolean ok) {
    this.ok = ok;
  }

  public SentMessageResponse messageId(Integer messageId) {
    this.messageId = messageId;
    return this;
  }

  /**
   * Get messageId
   * @return messageId
  */
  
  @Schema(name = "message_id", required = false)
  public Integer getMessageId() {
    return messageId;
  }

  public void setMessageId(Integer messageId) {
    this.messageId = messageId;
  }

  public SentMessageResponse chatId(Long chatId) {
    this.chatId = chatId;
    return this;
  }

  /**
   * Get chatId
   * @return chatId
  */
  
  @Schema(name = "chat_id", required = false)
  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public SentMessageResponse text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  
  @Schema(name = "text", required = false)
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
    SentMessageResponse sentMessageResponse = (SentMessageResponse) o;
    return Objects.equals(this.ok, sentMessageResponse.ok) &&
        Objects.equals(this.messageId, sentMessageResponse.messageId) &&
        Objects.equals(this.chatId, sentMessageResponse.chatId) &&
        Objects.equals(this.text, sentMessageResponse.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ok, messageId, chatId, text);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SentMessageResponse {\n");
    sb.append("    ok: ").append(toIndentedString(ok)).append("\n");
    sb.append("    messageId: ").append(toIndentedString(messageId)).append("\n");
    sb.append("    chatId: ").append(toIndentedString(chatId)).append("\n");
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

