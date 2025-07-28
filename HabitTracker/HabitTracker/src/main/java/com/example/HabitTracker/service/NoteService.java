package com.example.HabitTracker.service;

import com.example.HabitTracker.model.Note;
import com.example.HabitTracker.model.User;
import com.example.HabitTracker.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;

    public Note createNote(String content, User user) {
        Note note = new Note();
        note.setContent(content);
        note.setUser(user);
        return noteRepository.save(note);
    }

    public List<Note> getNotesByUser(User user) {
        return noteRepository.findByUser(user);
    }

    public Note updateNote(Long noteId, String content, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update the Note");
        }

        note.setContent(content);
        return noteRepository.save(note);
    }

    public void deleteNote(Long noteId, User user) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this note");
        }

        noteRepository.delete(note);
    }
}
