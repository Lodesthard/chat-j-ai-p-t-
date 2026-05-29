package fr.ensim.interop.introrest.service;

import fr.ensim.interop.introrest.model.Joke;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class JokeService {

    private final Map<Long, Joke> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public JokeService() {
        add("Pourquoi les plongeurs plongent-ils toujours en arrière et jamais en avant ?",
                "Parce que sinon ils tomberaient dans le bateau !");
        add("Qu'est-ce qu'un crocodile qui surveille des enfants ?",
                "Un gardien de crèche !");
        add("C'est l'histoire d'un scarabée qui entre dans un bar... Il commande une bière.",
                "Le barman lui dit : « Désolé, on ne sert pas les insectes. » Le scarabée répond : « C'est pas grave, je reviendrai quand vous aurez moins de monde. »");
    }

    public Joke add(String question, String answer) {
        Long id = counter.getAndIncrement();
        Joke joke = new Joke(id, question, answer);
        store.put(id, joke);
        return joke;
    }

    public Optional<Joke> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Joke> findByQuestion(String keyword) {
        String lower = keyword.toLowerCase();
        List<Joke> result = new ArrayList<>();
        for (Joke j : store.values()) {
            if (j.getQuestion().toLowerCase().contains(lower)) result.add(j);
        }
        return result;
    }

    public Optional<Joke> update(Long id, String question, String answer) {
        Joke joke = store.get(id);
        if (joke == null) return Optional.empty();
        joke.setQuestion(question);
        joke.setAnswer(answer);
        return Optional.of(joke);
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }

    public Optional<Joke> getRandom() {
        if (store.isEmpty()) return Optional.empty();
        List<Joke> jokes = new ArrayList<>(store.values());
        return Optional.of(jokes.get(new Random().nextInt(jokes.size())));
    }

    public Collection<Joke> getAll() {
        return store.values();
    }
}
