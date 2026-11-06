
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileSystemTest {

    @Test
    void correctDoubleDispatch() {
        FileNode file = new FileNode("sample.txt", 100);
        DirectoryNode directory = new DirectoryNode("folder");

        int[] visits = new int[2];

        Visitor visitor = new Visitor() {
            @Override
            public void visit(FileNode fileNode) {
                visits[0]++;
            }

            @Override
            public void visit(DirectoryNode directoryNode) {
                visits[1]++;
            }
        };

        file.accept(visitor);
        directory.accept(visitor);

        assertEquals(1, visits[0]);
        assertEquals(1, visits[1]);
    }

    @Test
    void calculatesTotalSizeAcrossNestedDirectories() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode documents = new DirectoryNode("documents");
        DirectoryNode archive = new DirectoryNode("archive");

        root.add(new FileNode("readme.txt", 120));
        documents.add(new FileNode("report.pdf", 350));
        archive.add(new FileNode("backup.zip", 530));

        documents.add(archive);
        root.add(documents);

        TotalSizeVisitor visitor = new TotalSizeVisitor();

        assertEquals(1000, visitor.calculate(root));
    }

    @Test
    void searchCombinesNameAndMinimumSize() {
        DirectoryNode root = new DirectoryNode("root");

        root.add(new FileNode("report.txt", 400));
        root.add(new FileNode("notes.txt", 100));
        root.add(new FileNode("photo.png", 800));
        root.add(new FileNode("summary.txt", 250));

        SearchVisitor visitor = new SearchVisitor(
                name -> name.endsWith(".txt"), 250
        );

        assertEquals(
                java.util.List.of("report.txt", "summary.txt"),
                visitor.search(root)
        );
    }

    @Test
    void searchReturnsSortedRootRelativePaths() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode projects = new DirectoryNode("projects");
        DirectoryNode archive = new DirectoryNode("archive");

        projects.add(new FileNode("zebra.txt", 300));
        projects.add(new FileNode("alpha.txt", 150));
        archive.add(new FileNode("notes.txt", 200));

        root.add(projects);
        root.add(archive);
        root.add(new FileNode("readme.txt", 100));

        SearchVisitor visitor = new SearchVisitor(
                name -> name.endsWith(".txt"), 0
        );

        assertEquals(
                java.util.List.of(
                        "archive/notes.txt",
                        "projects/alpha.txt",
                        "projects/zebra.txt",
                        "readme.txt"
                ),
                visitor.search(root)
        );
    }

    @Test
    void statisticsCountFilesDirectoriesAndDepth() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode work = new DirectoryNode("work");
        DirectoryNode archive = new DirectoryNode("archive");
        DirectoryNode old = new DirectoryNode("old");

        root.add(new FileNode("home.txt", 100));
        work.add(new FileNode("task.java", 250));
        old.add(new FileNode("backup.zip", 500));

        archive.add(old);
        work.add(archive);
        root.add(work);

        StatisticsVisitor visitor = new StatisticsVisitor();
        visitor.collect(root);

        assertEquals(3, visitor.getFileCount());
        assertEquals(4, visitor.getDirectoryCount());
        assertEquals(4, visitor.getMaxDepth());
    }

    @Test
    void handlesEmptyDirectoriesAndNoMatches() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode empty = new DirectoryNode("empty");

        root.add(empty);

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        assertEquals(0, sizeVisitor.calculate(root));

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        statisticsVisitor.collect(root);

        assertEquals(0, statisticsVisitor.getFileCount());
        assertEquals(2, statisticsVisitor.getDirectoryCount());
        assertEquals(1, statisticsVisitor.getMaxDepth());

        SearchVisitor searchVisitor = new SearchVisitor(
                name -> name.endsWith(".pdf"), 100
        );

        assertTrue(searchVisitor.search(root).isEmpty());
    }

    @Test
    void repeatedExecutionsDoNotAccumulateState() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode documents = new DirectoryNode("documents");

        root.add(new FileNode("readme.txt", 100));
        documents.add(new FileNode("report.txt", 300));
        root.add(documents);

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();

        assertEquals(400, sizeVisitor.calculate(root));
        assertEquals(400, sizeVisitor.calculate(root));

        SearchVisitor searchVisitor = new SearchVisitor(
                name -> name.endsWith(".txt"), 100
        );

        assertEquals(
                java.util.List.of("documents/report.txt", "readme.txt"),
                searchVisitor.search(root)
        );

        assertEquals(
                java.util.List.of("documents/report.txt", "readme.txt"),
                searchVisitor.search(root)
        );

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();

        statisticsVisitor.collect(root);
        assertEquals(2, statisticsVisitor.getFileCount());
        assertEquals(2, statisticsVisitor.getDirectoryCount());
        assertEquals(2, statisticsVisitor.getMaxDepth());

        statisticsVisitor.collect(root);
        assertEquals(2, statisticsVisitor.getFileCount());
        assertEquals(2, statisticsVisitor.getDirectoryCount());
        assertEquals(2, statisticsVisitor.getMaxDepth());
    }

    @Test
    void rejectsInvalidTreeMutations() {
        assertThrows(IllegalArgumentException.class,
                () -> new FileNode("invalid.txt", -10));

        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode documents = new DirectoryNode("documents");
        DirectoryNode other = new DirectoryNode("other");

        FileNode file = new FileNode("report.txt", 200);
        documents.add(file);

        assertThrows(IllegalArgumentException.class,
                () -> documents.add(new FileNode("report.txt", 300)));

        root.add(documents);
        root.add(other);

        assertThrows(IllegalArgumentException.class,
                () -> other.add(file));

        assertThrows(IllegalArgumentException.class,
                () -> root.add(root));

        assertThrows(IllegalArgumentException.class,
                () -> documents.add(root));

        DirectoryNode nested = new DirectoryNode("nested");
        documents.add(nested);
        nested.add(new FileNode("valid.txt", 100));

        TotalSizeVisitor visitor = new TotalSizeVisitor();
        assertEquals(300, visitor.calculate(root));
    }

}
