package com.example.hvale.loginapp;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.example.hvale.loginapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Content {


    /**
     * An array of sample (dummy) items.
     */

    public static final List<Content.DummyItem> ITEMS = new ArrayList<Content.DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Content.DummyItem> ITEM_MAP = new HashMap<String, Content.DummyItem>();

    private static final int COUNT = 5;

    private static final String OCCURRENCE = "Occurrence description ";
    private static final String OCCURRENCE_TITLE = "Occurrence's Title ";

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private void doInBackGround() {

    }
    private static void addItem(Content.DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static Content.DummyItem createDummyItem(int position) {
        return new Content.DummyItem(String.valueOf(OCCURRENCE_TITLE), OCCURRENCE + position, makeDetails(position));
    }



    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }

}
