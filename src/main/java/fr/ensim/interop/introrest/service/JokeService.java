package fr.ensim.interop.introrest.service;

import fr.ensim.interop.introrest.model.Joke;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class JokeService {

    private final Map<Long, Joke> store = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);
    private final Random random = new Random();

    public JokeService() {
        add("Les plongeurs",
                "Pourquoi les plongeurs plongent-ils toujours en arriere et jamais en avant ? "
                        + "Parce que sinon ils tomberaient dans le bateau !", 7);
        add("Le crocodile",
                "Qu'est-ce qu'un crocodile qui surveille des enfants ? Un gardien de creche !", 5);
        add("Le scarabee",
                "Un scarabee entre dans un bar et commande une biere. Le barman dit : "
                        + "« Desole, on ne sert pas les insectes. » Le scarabee repond : "
                        + "« Pas grave, je reviendrai quand vous aurez moins de monde. »", 8);
        add("La taupe",
                "Que dit une taupe quand elle se cogne ? Aie, je n'y vois plus rien !", 2);
        add("Le clavier",
                "Pourquoi le clavier d'ordinateur ne dort-il jamais ? Parce qu'il a deux Shift !", 4);
    }

    public Joke add(String title, String text, Integer rating) {
        Long id = counter.getAndIncrement();
        Joke joke = new Joke(id, title, text, clampRating(rating));
        store.put(id, joke);
        return joke;
    }

    public Optional<Joke> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Joke> findByTitle(String keyword) {
        String lower = keyword.toLowerCase();
        List<Joke> result = new ArrayList<>();
        for (Joke j : store.values()) {
            if (j.getTitle() != null && j.getTitle().toLowerCase().contains(lower)) {
                result.add(j);
            }
        }
        return result;
    }

    public Optional<Joke> update(Long id, String title, String text, Integer rating) {
        Joke joke = store.get(id);
        if (joke == null) {
            return Optional.empty();
        }
        joke.setTitle(title);
        joke.setText(text);
        joke.setRating(clampRating(rating));
        return Optional.of(joke);
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
    }

    public Optional<Joke> getRandom() {
        if (store.isEmpty()) {
            return Optional.empty();
        }
        List<Joke> jokes = new ArrayList<>(store.values());
        return Optional.of(jokes.get(random.nextInt(jokes.size())));
    }

    /** Blague la mieux notee (pour une "tres bonne blague"). */
    public Optional<Joke> getBest() {
        return store.values().stream().max(Comparator.comparingInt(Joke::getRating));
    }

    /** Blague la moins bien notee (pour une "blague nulle"). */
    public Optional<Joke> getWorst() {
        return store.values().stream().min(Comparator.comparingInt(Joke::getRating));
    }

    public Collection<Joke> getAll() {
        return store.values();
    }

    private Integer clampRating(Integer rating) {
        if (rating == null) {
            return 5;
        }
        if (rating < 0) {
            return 0;
        }
        if (rating > 10) {
            return 10;
        }
        return rating;
    }
}
