# Email Reply Parser for Java
A port of GitHub's Email Reply Parser library, by the fine folks at [Driftt](http://www.driftt.com/).

## Summary

Email Reply Parser makes it easy to grab *only* the last reply to an on-going email thread.

Say you'd like to parse out a user's response to your transaction email messages:

```
Yes that is fine, I will email you in the morning.

On Fri, Nov 16, 2012 at 1:48 PM, Driftt <support@driftt.com> wrote:

> Our support team just commented on your open Ticket:
> "Hi Royce, can we chat in the morning about your question?"
```

Email clients handle reply formatting differently, making parsing a pain. [We include tests for many cases](https://github.com/Driftt/EmailReplyParser/tree/master/src/test/resources/emails). The parsed email:

```
Yes that is fine, I will email you in the morning.
```

[![Build Status](https://secure.travis-ci.org/Driftt/EmailReplyParser.png?branch=master)](https://travis-ci.org/Driftt/EmailReplyParser)

## Installation

Using maven:

```xml
    <dependencies>
        <dependency>
            <groupId>com.driftt.email</groupId>
            <artifactId>EmailReplyParser</artifactId>
            <version>0.1</version>
        </dependency>
    </dependencies>
```

## Tutorial

### How to parse an email message

Step 1: Import email reply parser package

```java
import static com.driftt.email.EmailMessage.read;
```

Step 2: Provide email message as type String

```java
EmailMessage message = read(contents);
```

### How to only retrieve the reply message

Step 1: Import email reply parser package

```java
import static com.driftt.email.EmailMessage.read;
```

Step 2: Provide email message as type string using parse_reply class method.

```java
String reply = read(contents).getReply();
```


