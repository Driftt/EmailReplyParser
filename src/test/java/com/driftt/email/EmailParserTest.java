package com.driftt.email;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.driftt.email.EmailMessage.read;
import static java.util.stream.Collectors.toList;

@RunWith(MockitoJUnitRunner.class)
public class EmailParserTest {

  private String getEmail(String name) {
    try {
      URL url = Resources.getResource(this.getClass(), String.format("/emails/%s.txt", name));
      return Resources.toString(url, Charsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(String.format("No email file found: %s", name), e);
    }
  }

  @Test
  public void capturesDateString() {
    EmailMessage message = read(getEmail("email_1_4"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(3);
    Assertions.assertThat(fragments.get(0).getContent()).contains("Awesome");
    Assertions.assertThat(fragments.get(1).getContent()).contains("On");
    Assertions.assertThat(fragments.get(1).getContent()).contains("Loader");
  }

  @Test
  public void readsTopPost() {
    EmailMessage message = read(getEmail("email_1_3"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(5);
  }

  @Test
  public void multiLineReplyHeader() {
    EmailMessage message = read(getEmail("email_1_6"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(3);
    Assertions.assertThat(fragments.get(0).getContent()).contains("I get");
    Assertions.assertThat(fragments.get(1).getContent()).contains("On");
  }

  @Test
  public void complextBodyWithOneFragment() {
    EmailMessage message = read(getEmail("email_1_5"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(1);
  }

  @Test
  public void windowLineEndings() {
    EmailMessage message = read(getEmail("email_1_7"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(3);
    Assertions.assertThat(fragments.get(0).getContent()).contains(":+1:");
    Assertions.assertThat(fragments.get(1).getContent()).contains("On");
    Assertions.assertThat(fragments.get(1).getContent()).contains("Steps 0-2");
  }

  @Test
  public void correctSignature() {
    EmailMessage message = read(getEmail("correct_sig"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(2);
    Assertions.assertThat(fragments.get(1).getContent()).contains("--");

    List<Boolean> quoted = message.getFragments().stream().map(f -> f.isQuoted()).collect(toList());
    Assertions.assertThat(quoted).containsExactly(false, false);

    List<Boolean> signatures = message.getFragments().stream().map(f -> f.isSignature()).collect(toList());
    Assertions.assertThat(signatures).containsExactly(false, true);

    List<Boolean> hidden = message.getFragments().stream().map(f -> f.isHidden()).collect(toList());
    Assertions.assertThat(hidden).containsExactly(false, true);
  }

  @Test
  public void simpleBody() {
    EmailMessage message = read(getEmail("email_1_1"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(3);
    Assertions.assertThat(fragments.get(0).getContent()).contains("folks");
    Assertions.assertThat(fragments.get(2).getContent()).contains("riak-users");

    List<Boolean> signatures = message.getFragments().stream().map(f -> f.isSignature()).collect(toList());
    Assertions.assertThat(signatures).containsExactly(false, true, true);

    List<Boolean> hidden = message.getFragments().stream().map(f -> f.isHidden()).collect(toList());
    Assertions.assertThat(hidden).containsExactly(false, true, true);
  }

  @Test
  public void readsBottomMessage() {
    EmailMessage message = read(getEmail("email_1_2"));
    List<EmailMessage.Fragment> fragments = message.getFragments();
    Assertions.assertThat(fragments).hasSize(6);

    List<Boolean> quoted = message.getFragments().stream().map(f -> f.isQuoted()).collect(toList());
    Assertions.assertThat(quoted).containsExactly(false, true, false, true, false, false);

    List<Boolean> signatures = message.getFragments().stream().map(f -> f.isSignature()).collect(toList());
    Assertions.assertThat(signatures).containsExactly(false, false, false, false, false, true);

    List<Boolean> hidden = message.getFragments().stream().map(f -> f.isHidden()).collect(toList());
    Assertions.assertThat(hidden).containsExactly(false, false, false, true, true, true);

    Assertions.assertThat(fragments.get(0).getContent()).contains("Hi");
    Assertions.assertThat(fragments.get(1).getContent()).contains("On");
    Assertions.assertThat(fragments.get(3).getContent()).contains(">");
    Assertions.assertThat(fragments.get(5).getContent()).contains("riak-users");
  }

  @Test
  public void sentFromiPhone() {
    EmailMessage message = read(getEmail("email_1_2"));
    Assertions.assertThat(message.getReply()).doesNotContain("Sent from my iPhone");
  }

  @Test
  public void emailOneIsNotOn() {
    EmailMessage message = read(getEmail("email_one_is_not_on"));
    Assertions.assertThat(message.getReply()).doesNotContain("On Oct 1, 2012, at 11:55 PM, Dave Tapley wrote:");
  }

  @Test
  public void partialQuoteHeader() {
    EmailMessage message = read(getEmail("email_partial_quote_header"));
    String reply = message.getReply();
    Assertions.assertThat(reply).contains("On your remote host you can run:");
    Assertions.assertThat(reply).contains("telnet 127.0.0.1 52698");
    Assertions.assertThat(reply).contains("This should connect to TextMate");
  }

  @Test
  public void reply() {
    EmailMessage message = read(getEmail("email_1_2"));
    Assertions.assertThat(message.getReply()).contains("You can list the keys for the bucket");
  }

  @Test
  public void firstDrifttReply() {
    EmailMessage message = read(getEmail("driftt_1"));
    Assertions.assertThat(message.getReply()).isEqualTo("hey notifications!");
  }
}
