package org.crystalkste.secretsantatg.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GiftAssignment {
    public Map<String, String> assignments = new HashMap<>();

    public void assignGift(String giverId, String receiverId) {
        assignments.put(giverId, receiverId);
    }

    public boolean isAssigned(String receiverId) {
        return assignments.containsValue(receiverId);
    }

    public String getAssignment(String giverId) {
        return assignments.get(giverId);
    }
}
