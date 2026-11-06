
public class StatisticsVisitor implements Visitor {

    private int fileCount;
    private int directoryCount;
    private int maxDepth;
    private Element root;

    public void collect(Element root) {
        this.root = root;
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;

        FileSystemWalker.walk(root, this);
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        updateDepth(file);
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        updateDepth(directory);
    }

    private void updateDepth(Element element) {
        int depth = 0;
        Element current = element;

        while (current != root) {
            current = current.getParent();

            if (current == null) {
                throw new IllegalStateException("Element is outside root");
            }

            depth++;
        }

        maxDepth = Math.max(maxDepth, depth);
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
