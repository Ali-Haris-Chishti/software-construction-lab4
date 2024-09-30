package twitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T09:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "@alyssa mentioned you!", d1);
    private static final Tweet tweet4 = new Tweet(4, "david", "Contact us at support@company.com", d2);
    private static final Tweet tweet5 = new Tweet(5, "eve", "@alyssa is collaborating with @bbitdiddle", d3);
    
    /*
     * Tests for getTimespan
     */
    
    @Test
    public void testGetTimespanNoTweets() {
        Timespan timespan = Extract.getTimespan(Collections.emptyList());
        assertNull("expected null timespan for no tweets", timespan);
    }
    
    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Collections.singletonList(tweet1));
        assertEquals("expected same start and end", d1, timespan.getStart());
        assertEquals("expected same start and end", d1, timespan.getEnd());
    }
    
    @Test
    public void testGetTimespanMultipleTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet5));
        assertEquals("expected earliest start", d3, timespan.getStart());
        assertEquals("expected latest end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanSameTimestamp() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3));
        assertEquals("expected same start and end", d1, timespan.getStart());
        assertEquals("expected same start and end", d1, timespan.getEnd());
    }
    
    /*
     * Tests for getMentionedUsers
     */
    
    @Test
    public void testGetMentionedUsersSingleMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertEquals("expected one mentioned user", 1, mentionedUsers.size());
        assertTrue("expected mention of 'alyssa'", mentionedUsers.contains("alyssa"));
    }
    
    @Test
    public void testGetMentionedUsersMultipleMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet5));
        assertEquals("expected two mentioned users", 2, mentionedUsers.size());
        assertTrue("expected mention of 'alyssa'", mentionedUsers.contains("alyssa"));
        assertTrue("expected mention of 'bbitdiddle'", mentionedUsers.contains("bbitdiddle"));
    }

    @Test
    public void testGetMentionedUsersCaseInsensitive() {
        Tweet tweet6 = new Tweet(6, "frank", "@Alyssa is working with @BbitDiddle", d2);
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet6));
        assertEquals("expected two mentioned users", 2, mentionedUsers.size());
        assertTrue("expected mention of 'alyssa'", mentionedUsers.contains("alyssa"));
        assertTrue("expected mention of 'bbitdiddle'", mentionedUsers.contains("bbitdiddle"));
    }
    
    @Test
    public void testGetMentionedUsersNoMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersIgnoreEmails() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
}
