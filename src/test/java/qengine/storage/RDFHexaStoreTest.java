package qengine.storage;

import fr.boreal.model.logicalElements.api.*;
import fr.boreal.model.logicalElements.factory.impl.SameObjectTermFactory;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import org.apache.commons.lang3.NotImplementedException;
import qengine.model.RDFAtom;
import qengine.storage.RDFHexaStore;
import org.junit.jupiter.api.Test;
import qengine.model.StarQuery;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe {@link RDFHexaStore}.
 */
public class RDFHexaStoreTest {
    private static final Literal<String> SUBJECT_1 = SameObjectTermFactory.instance().createOrGetLiteral("subject1");
    private static final Literal<String> PREDICATE_1 = SameObjectTermFactory.instance().createOrGetLiteral("predicate1");
    private static final Literal<String> OBJECT_1 = SameObjectTermFactory.instance().createOrGetLiteral("object1");
    private static final Literal<String> SUBJECT_2 = SameObjectTermFactory.instance().createOrGetLiteral("subject2");
    private static final Literal<String> PREDICATE_2 = SameObjectTermFactory.instance().createOrGetLiteral("predicate2");
    private static final Literal<String> OBJECT_2 = SameObjectTermFactory.instance().createOrGetLiteral("object2");
    private static final Literal<String> OBJECT_3 = SameObjectTermFactory.instance().createOrGetLiteral("object3");
    private static final Variable VAR_X = SameObjectTermFactory.instance().createOrGetVariable("?x");
    private static final Variable VAR_Y = SameObjectTermFactory.instance().createOrGetVariable("?y");
    @Test
    public void testAddRDFAtom() {
        RDFHexaStore store = new RDFHexaStore();
        RDFAtom rdfAtom = new RDFAtom(SUBJECT_1, PREDICATE_1, OBJECT_1);

        assertTrue(store.add(rdfAtom), "RDFAtom should be added successfully.");
        assertTrue(store.getAtoms().contains(rdfAtom), "The RDFHexaStore should contain the added RDFAtom.");
    }

    @Test
    public void testAddDuplicateAtom() {
        RDFHexaStore store = new RDFHexaStore();
        RDFAtom rdfAtom = new RDFAtom(SUBJECT_1, PREDICATE_1, OBJECT_1);

        store.add(rdfAtom);
        long initialSize = store.size();
        store.add(rdfAtom); // Attempt to add the same atom again

        assertEquals(initialSize, store.size(), "Adding a duplicate RDFAtom should not increase the size.");
    }

    @Test
    public void testSize() {
        RDFHexaStore store = new RDFHexaStore();
        assertEquals(0, store.size(), "Initial size should be zero.");

        store.add(new RDFAtom(SUBJECT_1, PREDICATE_1, OBJECT_1));
        store.add(new RDFAtom(SUBJECT_2, PREDICATE_1, OBJECT_2));

        assertEquals(2, store.size(), "Size should reflect the number of added RDFAtoms.");
    }

    @Test
    public void testMatchAtom() {
        RDFHexaStore store = new RDFHexaStore();
        store.add(new RDFAtom(SUBJECT_1, PREDICATE_1, OBJECT_1));
        store.add(new RDFAtom(SUBJECT_2, PREDICATE_1, OBJECT_2));
        store.add(new RDFAtom(SUBJECT_1, PREDICATE_1, OBJECT_3));

        RDFAtom matchingAtom = new RDFAtom(SUBJECT_1, PREDICATE_1, VAR_X);
        Iterator<Substitution> matchedAtoms = store.match(matchingAtom);
        List<Substitution> matchedList = new ArrayList<>();
        matchedAtoms.forEachRemaining(matchedList::add);

        Substitution expectedResult1 = new SubstitutionImpl();
        expectedResult1.add(VAR_X, OBJECT_1);
        Substitution expectedResult2 = new SubstitutionImpl();
        expectedResult2.add(VAR_X, OBJECT_3);

        assertEquals(2, matchedList.size(), "There should be two matched RDFAtoms.");
        assertTrue(matchedList.contains(expectedResult1), "The substitution should contain OBJECT_1.");
        assertTrue(matchedList.contains(expectedResult2), "The substitution should contain OBJECT_3.");
    }


    @Test
    public void testMatchStarQuery() {
        RDFHexaStore store = new RDFHexaStore();
        RDFAtom rdfAtom1 = new RDFAtom(SUBJECT_1, PREDICATE_1, OBJECT_1);
        RDFAtom rdfAtom2 = new RDFAtom(SUBJECT_1, PREDICATE_1, OBJECT_2);
        store.add(rdfAtom1);
        store.add(rdfAtom2);

        // Specify the label and answer variables for StarQuery instantiation
        String label = "TestQuery";
        List<RDFAtom> rdfAtoms = List.of(new RDFAtom(SUBJECT_1, PREDICATE_1, VAR_X));
        Collection<Variable> answerVariables = Set.of(VAR_X);

        StarQuery query = new StarQuery(label, rdfAtoms, answerVariables);

        Iterator<Substitution> substitutions = store.match(query);
        List<Substitution> substitutionList = new ArrayList<>();
        substitutions.forEachRemaining(substitutionList::add);

        assertEquals(2, substitutionList.size(), "The StarQuery should match two RDFAtoms.");
        Substitution expectedSubstitution1 = new SubstitutionImpl();
        expectedSubstitution1.add(VAR_X, OBJECT_1);
        Substitution expectedSubstitution2 = new SubstitutionImpl();
        expectedSubstitution2.add(VAR_X, OBJECT_2);

        assertTrue(substitutionList.contains(expectedSubstitution1), "The substitution should contain OBJECT_1.");
        assertTrue(substitutionList.contains(expectedSubstitution2), "The substitution should contain OBJECT_2.");
    }

}