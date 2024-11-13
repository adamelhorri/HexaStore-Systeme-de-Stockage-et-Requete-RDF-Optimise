package qengine.program;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {

    // Dictionnaires pour encodage et décodage des ressources RDF
    private final Map<Integer, String> idToResource = new HashMap<>();
    private final Map<String, Integer> resourceToId = new HashMap<>();
    private int keyCounter = 0;

    /**
     * Récupère l'identifiant d'une ressource si elle existe, sinon crée un nouvel identifiant pour cette ressource.
     * 
     * @param resource La ressource RDF à encoder
     * @return L'identifiant unique associé à la ressource
     * @throws IllegalArgumentException si la ressource est null
     */
    public int addOrGetId(String resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }

        if (resourceToId.containsKey(resource)) {
            return resourceToId.get(resource);
        }

        int key = generateKey();
        idToResource.put(key, resource);
        resourceToId.put(resource, key);
        return key;
    }

    /**
     * Récupère la ressource associée à un identifiant donné.
     * 
     * @param id L'identifiant de la ressource
     * @return La ressource correspondante ou null si l'identifiant n'existe pas
     */
    public String getResourceById(Integer id) {
        return idToResource.get(id);
    }

    /**
     * Vérifie si une ressource est déjà encodée dans le dictionnaire.
     * 
     * @param resource La ressource à vérifier
     * @return true si la ressource est encodée, sinon false
     * @throws IllegalArgumentException si la ressource est null
     */
    public boolean containsResource(String resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        return resourceToId.containsKey(resource);
    }

    /**
     * Renvoie le nombre de ressources stockées dans le dictionnaire.
     * 
     * @return Le nombre de ressources uniques dans le dictionnaire
     */
    public int getResourceCount() {
        return resourceToId.size();
    }

    /**
     * Renvoie toutes les ressources actuellement encodées dans le dictionnaire sous forme d'une copie non modifiable.
     * 
     * @return Un map des identifiants vers les ressources encodées
     */
    public Map<Integer, String> getAllResources() {
        return Collections.unmodifiableMap(new HashMap<>(idToResource));
    }

    /**
     * Génère un nouvel identifiant unique pour une ressource.
     * 
     * @return Un nouvel identifiant unique
     */
    private synchronized int generateKey() {
        return keyCounter++;
    }
}
