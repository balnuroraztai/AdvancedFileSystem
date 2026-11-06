
import java.util.ArrayList;
import java.util.List;

public class DirectoryNode extends AbstractNode {
    private final List<Element> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    @Override
    public List<Element> getChildren() {
        return List.copyOf(children);
    }

    public void add(AbstractNode element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }

        if (element.getParent() != null) {
            throw new IllegalArgumentException("Already has a parent");
        }

        for (Element child : children) {
            if (child.getName().equals(element.getName())) {
                throw new IllegalArgumentException("Duplicate name");
            }
        }

        for (DirectoryNode current = this;
             current != null;
             current = current.getParent()) {

            if (current == element) {
                throw new IllegalArgumentException("Cycle detected");
            }
        }

        element.attachTo(this);
        children.add(element);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
