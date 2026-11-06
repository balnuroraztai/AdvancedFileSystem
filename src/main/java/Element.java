
import java.util.List;

public interface Element {
    String getName();
    DirectoryNode getParent();
    void accept(Visitor visitor);
    List<Element> getChildren();
}
