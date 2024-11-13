package qengine.storage;

import fr.boreal.model.logicalElements.api.Atom;
import fr.boreal.model.logicalElements.api.Substitution;
import fr.boreal.model.logicalElements.api.Term;
import fr.boreal.model.logicalElements.api.Variable;
import fr.boreal.model.logicalElements.impl.SubstitutionImpl;
import qengine.model.RDFAtom;
import qengine.model.StarQuery;

import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation of a HexaStore to store RDFAtoms.
 * This class uses six indexes to optimize searches.
 */
public class RDFHexaStore implements RDFStorage {

    // Six indexes for optimized queries
    private final Map<Term, Map<Term, Map<Term, RDFAtom>>> spo = new HashMap<>();
    private final Map<Term, Map<Term, Map<Term, RDFAtom>>> sop = new HashMap<>();
    private final Map<Term, Map<Term, Map<Term, RDFAtom>>> pso = new HashMap<>();
    private final Map<Term, Map<Term, Map<Term, RDFAtom>>> pos = new HashMap<>();
    private final Map<Term, Map<Term, Map<Term, RDFAtom>>> osp = new HashMap<>();
    private final Map<Term, Map<Term, Map<Term, RDFAtom>>> ops = new HashMap<>();

    private long size = 0;

    @Override
    public boolean add(RDFAtom atom) {
        Term subject = atom.getTripleSubject();
        Term predicate = atom.getTriplePredicate();
        Term object = atom.getTripleObject();

        addToIndex(spo, subject, predicate, object, atom);
        addToIndex(sop, subject, object, predicate, atom);
        addToIndex(pso, predicate, subject, object, atom);
        addToIndex(pos, predicate, object, subject, atom);
        addToIndex(osp, object, subject, predicate, atom);
        addToIndex(ops, object, predicate, subject, atom);

        size++;
        return true;
    }

    private void addToIndex(Map<Term, Map<Term, Map<Term, RDFAtom>>> index,
                            Term first, Term second, Term third, RDFAtom atom) {
        index.computeIfAbsent(first, k -> new HashMap<>())
             .computeIfAbsent(second, k -> new HashMap<>())
             .put(third, atom);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public Iterator<Substitution> match(RDFAtom atom) {
        Term subject = atom.getTripleSubject();
        Term predicate = atom.getTriplePredicate();
        Term object = atom.getTripleObject();

        List<Substitution> results = new ArrayList<>();

        if (subject != null && predicate != null && object != null) {
            results.addAll(findMatches(spo, subject, predicate, object));
        } else if (subject != null && predicate != null) {
            results.addAll(findMatches(spo, subject, predicate, null));
        } else if (predicate != null && object != null) {
            results.addAll(findMatches(pos, predicate, object, null));
        } else if (subject != null && object != null) {
            results.addAll(findMatches(sop, subject, object, null));
        }

        return results.iterator();
    }

    private List<Substitution> findMatches(Map<Term, Map<Term, Map<Term, RDFAtom>>> index,
                                           Term first, Term second, Term third) {
        List<Substitution> results = new ArrayList<>();

        for (Map.Entry<Term, Map<Term, Map<Term, RDFAtom>>> firstEntry : index.entrySet()) {
            if (first != null && !first.equals(firstEntry.getKey()) && !(first instanceof Variable)) continue;
            for (Map.Entry<Term, Map<Term, RDFAtom>> secondEntry : firstEntry.getValue().entrySet()) {
                if (second != null && !second.equals(secondEntry.getKey()) && !(second instanceof Variable)) continue;
                for (Map.Entry<Term, RDFAtom> thirdEntry : secondEntry.getValue().entrySet()) {
                    if (third != null && !third.equals(thirdEntry.getKey()) && !(third instanceof Variable)) continue;
                    results.add(createSubstitutionFromAtom(thirdEntry.getValue(), first, second, third));
                }
            }
        }
        return results;
    }

    private Substitution createSubstitutionFromAtom(RDFAtom atom, Term subjectTerm, Term predicateTerm, Term objectTerm) {
        Map<Variable, Term> substitutionMap = new HashMap<>();

        if (subjectTerm instanceof Variable) {
            substitutionMap.put((Variable) subjectTerm, atom.getTripleSubject());
        }
        if (predicateTerm instanceof Variable) {
            substitutionMap.put((Variable) predicateTerm, atom.getTriplePredicate());
        }
        if (objectTerm instanceof Variable) {
            substitutionMap.put((Variable) objectTerm, atom.getTripleObject());
        }

        return new SubstitutionImpl(substitutionMap);
    }

    @Override
    public Iterator<Substitution> match(StarQuery query) {
        List<Substitution> cumulativeResults = new ArrayList<>();

        for (RDFAtom atom : query.getRdfAtoms()) {
            Iterator<Substitution> atomMatches = match(atom);
            List<Substitution> currentAtomResults = new ArrayList<>();

            while (atomMatches.hasNext()) {
                Substitution substitution = atomMatches.next();
                if (cumulativeResults.isEmpty()) {
                    currentAtomResults.add(substitution);
                } else {
                    for (Substitution existingSub : cumulativeResults) {
                        Substitution merged = mergeSubstitutions(existingSub, substitution);
                        if (merged != null) currentAtomResults.add(merged);
                    }
                }
            }

            cumulativeResults = currentAtomResults;
            if (cumulativeResults.isEmpty()) break;
        }

        return cumulativeResults.iterator();
    }

    private Substitution mergeSubstitutions(Substitution sub1, Substitution sub2) {
        Map<Variable, Term> mergedMap = new HashMap<>(sub1.toMap());

        for (Map.Entry<Variable, Term> entry : sub2.toMap().entrySet()) {
            Variable var = entry.getKey();
            Term term = entry.getValue();
            if (mergedMap.containsKey(var) && !mergedMap.get(var).equals(term)) {
                return null; // Conflict in substitution
            }
            mergedMap.put(var, term);
        }
        return new SubstitutionImpl(mergedMap);
    }

    @Override
    public Collection<Atom> getAtoms() {
        Set<Atom> atoms = new HashSet<>();
        for (Map<Term, Map<Term, RDFAtom>> predicatesMap : spo.values()) {
            for (Map<Term, RDFAtom> objectsMap : predicatesMap.values()) {
                atoms.addAll(objectsMap.values());
            }
        }
        return atoms;
    }
    // Method to add multiple RDFAtoms from a stream
    public boolean addAll(Stream<RDFAtom> rdfAtomsStream) {
        rdfAtomsStream.forEach(this::add);
        return true;
    }

    // Method to add multiple RDFAtoms from a collection
    public boolean addAll(Collection<RDFAtom> rdfAtoms) {
        rdfAtoms.forEach(this::add);
        return true;
    }
}