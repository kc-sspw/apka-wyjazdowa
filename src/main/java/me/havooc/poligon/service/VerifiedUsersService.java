package me.havooc.poligon.service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class VerifiedUsersService {

    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    private volatile Set<String> allowedEmails = Set.of();
    private volatile Instant lastRefresh = Instant.EPOCH;

    public boolean isEmailAllowed(String email) {
        refreshIfNeeded();

        boolean allowed = email != null && allowedEmails.contains(email.trim().toLowerCase());
        return allowed;
    }

    /*@SuppressWarnings("unchecked")
    private synchronized void refreshIfNeeded() {
        if (Instant.now().isBefore(lastRefresh.plus(CACHE_TTL))) {
            return;
        }

        try {
            Firestore firestore = FirestoreClient.getFirestore();

            DocumentSnapshot document = firestore
                    .collection("allowedEmails")
                    .document("data")
                    .get()
                    .get();

            List<String> emails = (List<String>) document.get("emails");

            Set<String> newCache = new HashSet<>();

            if (emails != null) {
                emails.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .forEach(newCache::add);
            }

            allowedEmails = Collections.unmodifiableSet(newCache);
            lastRefresh = Instant.now();

        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh allowed emails", e);
        }
    }*/

    @SuppressWarnings("unchecked")
    private synchronized void refreshIfNeeded() {
        if (Instant.now().isBefore(lastRefresh.plus(CACHE_TTL))) {
            return;
        }

        try {
            Firestore firestore = FirestoreClient.getFirestore();

            DocumentSnapshot document = firestore
                    .collection("allowedEmails")
                    .document("data")
                    .get()
                    .get();

            Set<String> newCache = new HashSet<>();
            Object rawEmails = document.get("emails");

            if (rawEmails instanceof List<?> list) {
                for (Object item : list) {
                    if (item != null) {
                        // converts item to String, removes whitespace and normalizes case
                        String emailStr = item.toString().trim().toLowerCase();
                        if (!emailStr.isEmpty()) {
                            newCache.add(emailStr);
                        }
                    }
                }
            }

            allowedEmails = Collections.unmodifiableSet(newCache);
            lastRefresh = Instant.now();

        } catch (Exception e) {
            System.err.println("Błąd pobierania e-maili z Firestore:");
        }
    }
}