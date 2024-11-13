package qengine.program;

import qengine.model.RDFAtom;
import qengine.model.StarQuery;
import qengine.parser.RDFAtomParser;
import qengine.parser.StarQuerySparQLParser;
import qengine.storage.RDFHexaStore;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.query.api.Query;
import org.eclipse.rdf4j.rio.RDFFormat;
import java.util.HashSet;
import java.util.Set;


public final class Main {

    private static final String WORKING_DIR = "data/";
    private static final String SAMPLE_DATA_FILE = WORKING_DIR + "sample_data.nt";
    private static final String SAMPLE_QUERY_FILE = WORKING_DIR + "sample_query.queryset";
    private static final String LARGE_DATA_FILE = WORKING_DIR + "100K.nt";
    private static final String LARGE_QUERY_FILE = WORKING_DIR + "STAR_ALL_workload.queryset";

    public static void main(String[] args) throws IOException {
        System.out.println("=== Initializing Dictionary and RDFHexaStore ===");
        Dictionary dictionary = new Dictionary();
        RDFHexaStore rdfHexaStore = new RDFHexaStore();

        System.out.println("\n=== Parsing RDF Data (Basic Sample) ===");
        List<RDFAtom> rdfAtoms = parseRDFData(SAMPLE_DATA_FILE, dictionary);

        System.out.println("\n=== Adding RDFAtoms to RDFHexaStore ===");
        for (RDFAtom atom : rdfAtoms) {
            rdfHexaStore.add(atom);
        }

        System.out.println("\n=== Parsing Sample Queries and Testing Hexastore ===");
        List<StarQuery> starQueries = parseSparQLQueries(SAMPLE_QUERY_FILE);
        executeStarQueries(starQueries, rdfHexaStore);

        // Si nécessaire pour les tests avancés, décommentez les lignes ci-dessous pour utiliser les fichiers plus grands
        System.out.println("\n=== Parsing Large RDF Data for Performance Testing ===");
        List<RDFAtom> largeRdfAtoms = parseRDFData(LARGE_DATA_FILE, dictionary);
           for (RDFAtom atom : largeRdfAtoms) {
             rdfHexaStore.add(atom);
         }

         System.out.println("\n=== Parsing Large Query Set and Testing Performance ===");
        List<StarQuery> largeStarQueries = parseSparQLQueries(LARGE_QUERY_FILE);
        executeStarQueries(largeStarQueries, rdfHexaStore);
    }

    /**
     * Parse et encode les données RDF depuis un fichier.
     *
     * @param rdfFilePath Chemin vers le fichier RDF à parser
     * @param dictionary  Dictionnaire pour encoder les ressources RDF
     * @return Liste des RDFAtoms parsés
     * @throws IOException en cas de problème de lecture du fichier
     */
    private static List<RDFAtom> parseRDFData(String rdfFilePath, Dictionary dictionary) throws IOException {
        List<RDFAtom> rdfAtoms = new ArrayList<>();
        try (FileReader rdfFile = new FileReader(rdfFilePath);
             RDFAtomParser rdfAtomParser = new RDFAtomParser(rdfFile, RDFFormat.NTRIPLES)) {
            int count = 0;
            while (rdfAtomParser.hasNext()) {
                RDFAtom atom = rdfAtomParser.next();
                dictionary.addOrGetId(atom.getTripleSubject().toString());
                dictionary.addOrGetId(atom.getTriplePredicate().toString());
                dictionary.addOrGetId(atom.getTripleObject().toString());
                rdfAtoms.add(atom);
                System.out.println("RDF Atom #" + (++count) + ": " + atom);
            }
            System.out.println("Total RDF Atoms parsed and added to Dictionary: " + count);
        }
        return rdfAtoms;
    }

    /**
     * Parse les requêtes en étoile depuis un fichier.
     *
     * @param queryFilePath Chemin vers le fichier de requêtes SparQL
     * @return Liste des StarQueries parsées
     * @throws IOException en cas de problème de lecture du fichier
     */
    private static List<StarQuery> parseSparQLQueries(String queryFilePath) throws IOException {
        List<StarQuery> starQueries = new ArrayList<>();
        try (StarQuerySparQLParser queryParser = new StarQuerySparQLParser(queryFilePath)) {
            int queryCount = 0;
            while (queryParser.hasNext()) {
                Query query = queryParser.next();
                if (query instanceof StarQuery starQuery) {
                    starQueries.add(starQuery);
                    System.out.println("Star Query #" + (++queryCount) + ": " + starQuery);
                } else {
                    System.err.println("Unknown query ignored.");
                }
            }
            System.out.println("Total Queries parsed: " + starQueries.size());
        }
        return starQueries;
    }

    /**
     * Exécute chaque requête en étoile sur RDFHexaStore et affiche les résultats.
     *
     * @param starQueries Liste des requêtes en étoile
     * @param rdfHexaStore Le store contenant les triplets RDF
     */
    private static void executeStarQueries(List<StarQuery> starQueries, RDFHexaStore rdfHexaStore) {
        for (StarQuery starQuery : starQueries) {
            System.out.printf("Executing StarQuery: %s%n", starQuery);
            System.out.println("Results:");

            Set<Substitution> uniqueResults = new HashSet<>();  // Use Set to avoid duplicates

            // Accumulate unique matches across all RDFAtoms in the query
            for (RDFAtom atom : starQuery.getRdfAtoms()) {
                var results = rdfHexaStore.match(atom);
                while (results.hasNext()) {
                    uniqueResults.add(results.next());
                }
            }

            if (uniqueResults.isEmpty()) {
                System.out.println("No results found.");
            } else {
                // Print each unique result
                uniqueResults.forEach(System.out::println);
            }
            System.out.println();
        }
    }
}
