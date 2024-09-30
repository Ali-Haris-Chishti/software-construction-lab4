package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "talking about security", d3);

    // Test case for writtenBy when no tweets are authored by the given user
    @Test
    public void testWrittenByNoMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "nonexistent");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    // Test case for writtenBy when multiple tweets are authored by the same user
    @Test
    public void testWrittenByMultipleTweetsBySameUser() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "alyssa");
        
        assertEquals("expected 2 tweets", 2, writtenBy.size());
        assertTrue("expected list to contain tweet1 and tweet3", writtenBy.containsAll(Arrays.asList(tweet1, tweet3)));
    }
    
    // Test case for writtenBy with case-insensitive username
    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "ALYSSA");
        
        assertEquals("expected 1 tweet", 1, writtenBy.size());
        assertTrue("expected list to contain tweet1", writtenBy.contains(tweet1));
    }

    // Test case for inTimespan when no tweets are within the timespan
    @Test
    public void testInTimespanNoTweetsInRange() {
        Instant testStart = Instant.parse("2016-02-17T12:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T13:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    // Test case for inTimespan with some tweets outside the range
    @Test
    public void testInTimespanSomeTweetsOutsideRange() {
        Instant testStart = Instant.parse("2016-02-17T10:30:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:30:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), new Timespan(testStart, testEnd));
        
        assertEquals("expected 1 tweet", 1, inTimespan.size());
        assertTrue("expected list to contain tweet2", inTimespan.contains(tweet2));
    }
    
    // Test case for inTimespan when the timespan is a single point in time
    @Test
    public void testInTimespanSinglePoint() {
        Instant testStart = Instant.parse("2016-02-17T10:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertEquals("expected 1 tweet", 1, inTimespan.size());
        assertTrue("expected list to contain tweet1", inTimespan.contains(tweet1));
    }

    // Test case for containing when no tweets contain the specified words
    @Test
    public void testContainingNoMatch() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("nonexistent"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    // Test case for containing when multiple tweets contain the specified words
    @Test
    public void testContainingMultipleMatches() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("talk", "about"));
        
        assertEquals("expected 3 tweets", 3, containing.size());
        assertTrue("expected list to contain all tweets", containing.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
    }
    
    // Test case for containing with case-insensitivity
    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("TALK"));
        
        assertEquals("expected 2 tweets", 2, containing.size());
        assertTrue("expected list to contain both tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
    }

    // Test case for containing with special characters
    @Test
    public void testContainingSpecialCharacters() {
        Tweet tweetSpecial = new Tweet(3, "alyssa", "Let's talk about @Rivest!", d3);
        List<Tweet> containing = Filter.containing(Arrays.asList(tweetSpecial), Arrays.asList("about", "rivest"));
        
        assertEquals("expected 1 tweet", 1, containing.size());
        assertTrue("expected list to contain the tweet", containing.contains(tweetSpecial));
    }
}
