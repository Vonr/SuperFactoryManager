package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.RegexCache;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class TagMatcher implements Predicate<Object>, ASTNode {
    public static final TagMatcher MATCH_ALL = TagMatcher.fromNamespaceAndPath(".*", ".*");
    public final String namespacePattern;
    public final List<String> pathElementPatterns;
    private final Predicate<String> namespacePredicate;
    private final List<Predicate<String>> pathElementPredicates;

    private TagMatcher(
            String namespacePattern,
            List<String> pathElementPatterns
    ) {
        this.namespacePattern = namespacePattern;
        this.pathElementPatterns = Collections.unmodifiableList(pathElementPatterns);
        this.namespacePredicate = RegexCache.buildPredicate(namespacePattern);
        this.pathElementPredicates = this.pathElementPatterns.stream().map(RegexCache::buildPredicate).toList();
    }

    public static TagMatcher fromNamespaceAndPath(String namespace, String path) {
        return new TagMatcher(namespace, List.of(path.split("/")));
    }

    public static TagMatcher fromNamespaceAndPath(String namespace, Collection<String> path) {
        return new TagMatcher(namespace, new ArrayList<>(path));
    }

    public static TagMatcher fromPath(List<String> pathElements) {
        return new TagMatcher(".*", pathElements);
    }

    public static TagMatcher fromString(String string) {
        var parts = string.split(":", 1);
        if (parts.length == 1) {
            String path = parts[0];
            List<String> pathElements = List.of(path.split("/"));
            return new TagMatcher(".*", pathElements);
        } else {
            String namespace = parts[0];
            String path = parts[1];
            List<String> pathElements = List.of(path.split("/"));
            return new TagMatcher(namespace, pathElements);
        }
    }

    @Override
    public boolean test(Object o) {
        if (o instanceof ResourceLocation resourceLocation) {
            return testResourceLocation(resourceLocation);
        } else if (o instanceof String string) {
            return testString(string);
        }
        return false;
    }

    public boolean testResourceLocation(ResourceLocation resourceLocation) {
        return testPath(resourceLocation.getNamespace(), resourceLocation.getPath().split("/"));
    }

    public boolean testString(String string) {
        String[] chunks = string.split(":");
        if (chunks.length == 0) {
            return false;
        } else if (chunks.length == 1) {
            return testPath("", string.split("/"));
        } else {
            return testPath(chunks[0], chunks[1].split("/"));
        }
    }

    private boolean testPath(
            String checkNamespace,
            String[] checkPathElements
    ) {
        if (!namespacePredicate.test(checkNamespace)) {
            return false;
        }
        for (int i = 0; i < checkPathElements.length; i++) {
            // fail if path has more elements than pattern
            if (i >= this.pathElementPatterns.size()) {
                return false;
            }
            // succeed when pattern is deep-match-all
            if (this.pathElementPatterns.get(i).equals(".*.*")) {
                return true;
            }
            // fail if path element does not match pattern
            if (!pathElementPredicates.get(i).test(checkPathElements[i])) {
                return false;
            }
        }
        return true;
    }
}
