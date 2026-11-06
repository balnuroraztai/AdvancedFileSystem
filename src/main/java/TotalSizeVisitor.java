
public class TotalSizeVisitor implements Visitor {

    private long totalSize;

    public long calculate(Element root) {
        totalSize = 0;
        FileSystemWalker.walk(root, this);
        return totalSize;
    }

    @Override
    public void visit(FileNode file) {
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
    }

    public long getTotalSize() {
        return totalSize;
    }
}
