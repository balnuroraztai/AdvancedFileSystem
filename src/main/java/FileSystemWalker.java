
public class FileSystemWalker {

    public static void walk(Element root, Visitor visitor) {
        root.accept(visitor);

        for (Element child : root.getChildren()) {
            walk(child, visitor);
        }
    }
}
