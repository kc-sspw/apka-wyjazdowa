/**
 * @Author Łukasz Piętka (FUT 2025)
 */
package me.havooc.poligon.controller;

import lombok.RequiredArgsConstructor;
import me.havooc.poligon.model.Song;
import me.havooc.poligon.repository.SongRepository;
import me.havooc.poligon.security.WebSocketHandler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/songbook")
@RequiredArgsConstructor
public class SongController {
    private final SongRepository songRepository;
    private final WebSocketHandler ws;

    @GetMapping
    public ResponseEntity<List<Song>> getAll(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime lastUpdated) {
        if (lastUpdated != null && !songRepository.existsUpdatedAfter(lastUpdated))
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        return ResponseEntity.ok(songRepository.findAll());
    }

    @PostMapping
    public Song saveOrUpdate(@RequestBody Song song) {
        // 1. Jeśli ID jest puste, to od razu nowy rekord
        if (song.getId() == null)
            return createNewSong(song);

        // 2. Próbujemy znaleźć po ID. Jeśli jest -> Update. Jeśli nie ma -> Insert
        return songRepository.findById(song.getId())
                .map(existing -> {
                    // LOGIKA AKTUALIZACJI
                    existing.setTitle(song.getTitle());
                    existing.setSongText(song.getSongText());
                    existing.setLastModified(LocalDateTime.now());

                    Song updated = songRepository.save(existing);
                    ws.broadcast(songRepository.findAll(), "songbook");
                    return updated;
                })
                .orElseGet(() -> {
                    // LOGIKA TWORZENIA NOWEGO (gdy podano ID, ale nie ma go w bazie)
                    return createNewSong(song);
                });
    }

    private Song createNewSong(Song song) {
        song.setId(null); // Upewniamy się, że ID jest null, żeby baza nadała nowe
        song.setLastModified(LocalDateTime.now());

        Song saved = songRepository.save(song);
        ws.broadcast(songRepository.findAll(), "songbook");
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        songRepository.deleteById(id);
        ws.broadcast(songRepository.findAll(), "songbook");
        return ResponseEntity.ok().build();
    }
} 