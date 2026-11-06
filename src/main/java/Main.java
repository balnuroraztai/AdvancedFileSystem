
public class Main {

    public static void main(String[] args) {

        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode documents = new DirectoryNode("documents");
        DirectoryNode images = new DirectoryNode("images");

        documents.add(new FileNode("report.txt", 500));
        documents.add(new FileNode("notes.txt", 200));
        images.add(new FileNode("photo.png", 1000));

        root.add(documents);
        root.add(images);

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        System.out.println("Total size: "
                + sizeVisitor.calculate(root) + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor(
                name -> name.endsWith(".txt"), 200
        );
        System.out.println("Matching files: "
                + searchVisitor.search(root));

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        statisticsVisitor.collect(root);

        System.out.println("Files: "
                + statisticsVisitor.getFileCount());
        System.out.println("Directories: "
                + statisticsVisitor.getDirectoryCount());
        System.out.println("Maximum depth: "
                + statisticsVisitor.getMaxDepth());
    }
}
