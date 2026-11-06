
public abstract class AbstractNode implements Element {
    private final String name;
    private DirectoryNode parent;

    protected AbstractNode(String name) {
        if (name == null || name.isBlank()
                || name.contains("/")) {
            throw new IllegalArgumentException("Invalid name");
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    final void attachTo(DirectoryNode directory) {
        if (directory == null || parent != null) {
            throw new IllegalArgumentException("Invalid parent");
        }
        parent = directory;
    }
}
