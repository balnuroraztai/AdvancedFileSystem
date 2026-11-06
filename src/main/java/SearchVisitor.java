
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class SearchVisitor implements Visitor {

    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private Element root;

    public SearchVisitor(
            Predicate<String> filenameCriterion,
            long minimumSize) {

        if (filenameCriterion == null || minimumSize < 0) {
            throw new IllegalArgumentException("Invalid search criteria");
        }

        this.filenameCriterion = filenameCriterion;
        this.minimumSize = minimumSize;
    }

    public List<String> search(Element root) {
        this.root = root;
        matches.clear();

        FileSystemWalker.walk(root, this);
        Collections.sort(matches);

        return List.copyOf(matches);
    }

    @Override
    public void visit(FileNode file) {
        if (filenameCriterion.test(file.getName())
                && file.getSize() >= minimumSize) {
            matches.add(buildPath(file));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    private String buildPath(Element element) {
        List<String> parts = new ArrayList<>();

        Element current = element;

        while (current != root) {
            parts.add(current.getName());
            current = current.getParent();

            if (current == null) {
                throw new IllegalStateException("Element is outside root");
            }
        }

        Collections.reverse(parts);

        if (parts.isEmpty()) {
            return element.getName();
        }

        return String.join("/", parts);
    }

    public List<String> getMatches() {
        return List.copyOf(matches);
    }
}
